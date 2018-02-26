package Client;

// In bin class :   java client.ChatClient 127.0.0.1
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import Utils.ChatMessage;
import Utils.ChatRensponse;
import Utils.ChatRequest;
public class ReceiverClient {

	private static int port = 4000;
	  
	public static void main(String[] args) throws IOException {			
		ArrayList<ChatMessage> msg = new ArrayList<ChatMessage>();
		String ipaddr = args[0];
		String nickname = null;
		ChatRensponse ob;
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		boolean active = false;
		ChatRequest chatRequest;
		int count = -1;
		String out = null;
		
		try {
			ObjectInputStream iis = null;
			s.connect(addr);
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader buffer = new BufferedReader(reader);
			ChatRequest req;
			OutputStream os = s.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			ChatRensponse response;
			InputStream is = s.getInputStream();
			
			// Nickname request	
			while (nickname==null){
				System.out.println("Inserire un nickname valido per accedere alla chat in receiver mode:");
				String lineNick = buffer.readLine();
				
				// Check nickname validity
				chatRequest = new ChatRequest("loginrequestrc",lineNick);
				oos.writeObject(chatRequest);
				oos.flush();				
				is = s.getInputStream();
				iis = new ObjectInputStream(is);	
				
				// Server response check: logged successfully or not?
				response = (ChatRensponse) iis.readObject();	
				if(response.getResponseCode()==0){
					
					// Login error
					System.out.println(response.getError());
					nickname = null;			
				}else{
					nickname = lineNick;
					System.out.println(response.getError());
					if(response.getResponseCode()==3)
						active = true;
				}		
			}		
			ChatRequest act = new ChatRequest("isactive", nickname);
			
			while(true){
				
			// To start receiver's routine, the account must be activated logging in also in sender mode
			System.out.println("Waiting for the account to be active (you must be logged in both modes).");
			while(active == false){
				oos.writeObject(act);
				oos.flush();
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				response = (ChatRensponse) iis.readObject();
				if(response.getResponseCode()==5){
					
					// Also sender is logged in
					active = true;
				}
				else{
					//System.out.println("Sender still not logged ");	
				}
				Thread.sleep(1000);
			}						
			System.out.println("Chat active: waiting for new messages");
			
			// Cycle that must be interrupted if receiver or/and sender log out
			while(active==true){	

				// Request to download the new messages
				req = new ChatRequest("getmessages", count, nickname);
				oos.writeObject(req);
				oos.flush();
				
				// Server response
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				ob = (ChatRensponse) iis.readObject();
				
				// If there is no new messages
				if(ob.getResponseCode()==-1){
					continue;
				}				
				else{
					
					// Not active
					if(ob.getResponseCode()==6 ){
						System.out.println( ob.getError());
						active = false;
						continue;
					}
					else if(ob.getResponseCode()!=1){
						System.out.println("Unexpected server response format: " + ob.getError());
						continue;
					}
				}
				
				// I'm sure i'm getting that
				msg = (ArrayList<ChatMessage>) ob.getParam();			
				count = ob.getCount();		
				
				// Write on receiver's terminal the new messages (public AND private)
				for(ChatMessage ch: msg){		
					out = ch.getSender() + " >> " + ch.getMessage();
					if(ch.getReceiver()!=null){
						out = "[whisper] " + out;
					}
					System.out.println(out);
				}	
				count = ob.getCount();		
				msg = null;		
				}
				Thread.sleep(1000);

			}
		}catch(Exception e){
			
			// Handling receiver logout
			ChatRequest ex = new ChatRequest("quit","receiver", nickname);	
			OutputStream os = s.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);			
			oos.writeObject(ex);
			oos.flush();		
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}		
			e.printStackTrace();
		}	
		s.close();
	}
}

