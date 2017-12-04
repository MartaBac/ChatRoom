package Client;

//nella classe bin :   java client.ChatClient 127.0.0.1
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import Server.ChatRoom;
import Server.ChatServer;
import Utils.ChatMessage;
import Utils.ChatRensponse;
import Utils.ChatRequest;
public class ReceiverClient {

	private static int port = 4000;
	
	
	public static void main(String[] args) {
		
		ChatRensponse chatRes;
		ArrayList<ChatMessage> msg = new ArrayList<ChatMessage>();
		String ipaddr = args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		String nickname = null;
		ChatRequest cr;
		int count = -1;
	
		try {
			s.connect(addr);
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader buffer = new BufferedReader(reader);
			
			OutputStream os = s.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(os);
			BufferedWriter outbuffer = new BufferedWriter(wr);
			
			// canale input per mess da Server a Client
			InputStream is = s.getInputStream();
			InputStreamReader rd = new InputStreamReader(is);
			BufferedReader inbuffer = new BufferedReader(rd);
			
			//richiesta nickname		
			while (nickname==null){
				System.out.println("Inserire un nickname valido per accedere alla chat");
				String lineNick = buffer.readLine();
				// check se valido
				cr = new ChatRequest("loginrequestrc",lineNick);
				//outbuffer.write("loginrequestrc"+lineNick);
				outbuffer.newLine();
				outbuffer.flush();
				System.out.println("read_rec");
				String response = inbuffer.readLine();
				System.out.println(response);
				if(!response.equals("Unable to log."))
					nickname = lineNick;
				else
					System.out.println("hula_rec");
				
			}
			System.out.println("cycle 2_rec  "+ nickname + "-33-"+ ChatServer.utenti.get(nickname));
			while(ChatServer.utenti.get(nickname).getActive()==true){	
				// Scarico tutti i messaggi che mi interessano
				chatRes = ChatRoom.getMessages(count, nickname);
				// <--- to check, così potrei perdere dei messaggi
				count = ChatRoom.addMessage(null);			
				msg = (ArrayList<ChatMessage>) chatRes.getObject();

				
				for(ChatMessage ch: msg){
					System.out.println(ch.getMessage());
				}
				
				
			}
			/*
			while(true){
				String line = buffer.readLine();
				
				if(line.equals("quit")){
					outbuffer.write(line);
					outbuffer.newLine();
					outbuffer.flush();
					break;
				}
				String in = inbuffer.readLine();
				System.out.println(in);
			}
			*/

		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}