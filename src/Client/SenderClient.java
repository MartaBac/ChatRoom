package Client;

//nella classe bin :   java client.ChatClient 127.0.0.1
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import Server.ChatRoom;
import Utils.ChatMessage;
import Utils.ChatRequest;
public class SenderClient {

	private static int port = 4000;
	
	public static void main(String[] args) {
		
		String ipaddr = args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		String nickname = null;
		ChatRequest cr;
		
		int size =-1;
	
		try {
			s.connect(addr);
			ObjectInputStream iis = null;
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader buffer = new BufferedReader(reader);
			
			// canale output per mess da client vs server
			OutputStream os = s.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(os);
			//BufferedWriter outbuffer = new BufferedWriter(wr);
			
			//per scrivere strutture java:serializzazione
			//vedi StudentSerializer.java...
			ObjectOutputStream oos = new ObjectOutputStream(os);
			
			// canale input per mess da Server a Client
			InputStream is = s.getInputStream();
			InputStreamReader rd = new InputStreamReader(is);
			BufferedReader inbuffer = new BufferedReader(rd);
			
			//richiesta nickname
			
			while (nickname==null){
				System.out.println("Inserire un nickname valido per accedere alla chat");
				String lineNick = buffer.readLine();
				// check se valido
				cr = new ChatRequest("loginrequestsd",lineNick);
				
				oos.writeObject(cr);
				oos.flush();
				System.out.println("oos wrote object");
				
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				
				//Server response
				ChatRequest response = (ChatRequest) iis.readObject();
				// Check se il response code dà login ok o errore
				
				//
				//agg
				System.out.println("44");
				
				if(response.getError().equals("0")){
					// Errore di login
					System.out.println("Impossibile effettuare il login con questo nickname");
					nickname = null;
					
				}else{
					System.out.println("Logged in as" + lineNick);
					nickname = lineNick;
				}
			}
			System.out.println("cycle 2");

			while(true){
				String line = buffer.readLine();
				String receiver = null;
				
				if(line.equals("quit")){
					break;
				}
				// modifico la cosa sottostante con questo
				if(line.charAt(0)=='@'){
					//private message
					String[] parts;
					parts = line.split("\\:");
					receiver = parts[0];
				}
				ChatMessage cm = new ChatMessage(line,nickname, receiver);
				size = ChatRoom.addMessage(cm);
				/*outbuffer.write(nickname+":"+line);
				outbuffer.newLine();
				outbuffer.flush();*/

				String response = inbuffer.readLine();
				System.out.println("Server risposta : " + response + "Message in position " + size);
			}

		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}