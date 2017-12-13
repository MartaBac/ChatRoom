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
import Utils.ChatRensponse;
import Utils.ChatRequest;
public class SenderClient {

	private static int port = 4000;
	
	public static void main(String[] args) {
		
		String ipaddr = args[0];
		InetSocketAddress addr  = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		String nickname = null;
		boolean active = false;
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
				System.out.println("letto");
				cr = new ChatRequest("loginrequestsd",lineNick);
				oos.writeObject(cr);
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
			//temporaneo!!!!
			active = true;
			if(active == false){
				ChatRensponse response = (ChatRensponse) iis.readObject();
				if(response.getResponseCode()==3){
					// Si è loggato anche il receiver
					active = true;
				}
			}
			
			String receiver;
			System.out.println("hey");
			ChatMessage cm;
			ChatRequest crMess;
			
			while(active == true){
				String line = buffer.readLine();
				receiver = null;
				System.out.println("Received: "+line);
				if(line.equals("quit")){
					active = false;
					break;
				}
				// modifico la cosa sottostante con questo
				if(line.charAt(0)=='@'){
					//private message
					String[] parts;
					parts = line.split("\\:");
					receiver = parts[0];
				}
				//Creo nuovo messaggio
				cm = new ChatMessage(line,nickname, receiver);
				crMess = new ChatRequest(cm);
				oos.writeObject(crMess);
				oos.flush();
				System.out.println("here");
				System.out.println("here");
				//Server response
				ChatRensponse responseMess = new ChatRensponse();

				System.out.println("Waiting for an answer.");
				responseMess = (ChatRensponse) iis.readObject();
				
				if(responseMess.getResponseCode()==0){
					// errore
					System.out.println("Invio fallito" + responseMess.getError().toString());
				}
				else{
					System.out.println("invio ok");
					// messaggio inviato
					//param = ArrayList<ChatMessage>.size()
					System.out.println(responseMess.getParam());
					System.out.println(responseMess.getError());
					System.out.println(responseMess.getResponseCode());
					size = (int) responseMess.getParam();
					System.out.println("Invio messaggio eseguito con successo. Message in position " + size);
				}
				
			}

		}catch(Exception e){
			e.printStackTrace();
		}	

	}

}