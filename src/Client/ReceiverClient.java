package Client;

// Nella classe bin :   java client.ChatClient 127.0.0.1
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
import Server.ChatServer;
import Utils.ChatMessage;
import Utils.ChatRensponse;
import Utils.ChatRequest;
public class ReceiverClient {

	private static int port = 4000;
		
	public static void main(String[] args) throws IOException {		
		ArrayList<ChatMessage> msg = new ArrayList<ChatMessage>();
		String ipaddr = args[0];
		String nickname = null;
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		boolean active = false;
		ChatRequest chatRequest;
		int count = -1;
		try {
			ObjectInputStream iis = null;
			s.connect(addr);
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader buffer = new BufferedReader(reader);
			ChatRequest req;
			OutputStream os = s.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
					
			// canale input per mess da Server a Client
			InputStream is = s.getInputStream();
			
			// Nickname request	
			while (nickname==null){
				System.out.println("Inserire un nickname valido per accedere alla chat in receiver mode:");
				String lineNick = buffer.readLine();
				
				// Check nickname validity
				System.out.println("letto");
				chatRequest = new ChatRequest("loginrequestrc",lineNick);
				oos.writeObject(chatRequest);
				oos.flush();				
				is = s.getInputStream();
				iis = new ObjectInputStream(is);	
				
				//Server response
				ChatRensponse response = (ChatRensponse) iis.readObject();
				System.out.println(response);
				
				// Check se il response code dà login ok o errore
				System.out.println("44");
				
				if(response.getResponseCode()==0){
					// Errore di login
					System.out.println(response.getError());
					nickname = null;			
				}else{
					nickname = lineNick;
					System.out.println(response.getError());
					if(response.getResponseCode()==3)
						active = true;
				}		
			}
			
			System.out.println("cycle 2");

			// To start receiver's routine, the account must be activated logging in also in sender mode
			while(active == false){
				System.out.println("Waiting for sender to log in.");
				ChatRensponse response = (ChatRensponse) iis.readObject();
				if(response.getResponseCode()==3){
					// Si è loggato anche il receiver
					System.out.println("receiver logged in");
					active = true;
				}
				else{
					System.out.println("Receiver still not logged ");
					
				}
				Thread.sleep(1000);
			}
				

			System.out.println("cycle 2_rec  "+ nickname + "-33-"+ ChatServer.utenti.get(nickname));
			// Cycle that must be interrupted if receiver or/and sender log out
			while(active==true){	
				// Request to download the new messages
				os = s.getOutputStream();			
				oos = new ObjectOutputStream(os);
				req = new ChatRequest("getmessages", count, nickname);
				oos.writeObject(req);
				oos.flush();

				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				// Server response
				ChatRensponse ob = (ChatRensponse) iis.readObject();
				// If there is no new messages
				if(ob.getResponseCode()==-1)
					continue;
				else
					// Handling unexpected responses
					if(ob.getResponseCode()!=1){
						System.out.println("Unexpected server response format.");
						continue;
					}
				// Getting new messages and updating count	
				msg = (ArrayList<ChatMessage>) ob.getParam();
				count = ob.getCount();
				
				// Write on receiver's terminal the new messages (public AND private)
				for(ChatMessage ch: msg){
					System.out.println(ch.getMessage());
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}	
		s.close();
	}

}