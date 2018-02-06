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
import Utils.ChatMessage;
import Utils.ChatRensponse;
import Utils.ChatRequest;
public class SenderClient {
	private static int port = 4000;	
	public static void main(String[] args) throws IOException {		
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
			
			// Output channel to communicate from client to server
			OutputStream os = s.getOutputStream();		
			ObjectOutputStream oos = new ObjectOutputStream(os);
			
			// Output channel to communicate from server to client
			InputStream is = s.getInputStream();
			
			// Nickname request
			while (nickname==null){
				System.out.println("Inserire un nickname valido per accedere alla chat");
				String lineNick = buffer.readLine();
				
				// New ChatRequest to check if it's a valid nickname to log with
				cr = new ChatRequest("loginrequestsd",lineNick);
				
				// Write the message to send a login request to the server
				oos.writeObject(cr);
				oos.flush();
				
				// Waiting server response
				is = s.getInputStream();
				iis = new ObjectInputStream(is);
				ChatRensponse response = (ChatRensponse) iis.readObject();
				System.out.println("44");
				System.out.println(response.getResponseCode()+(String) response.getError());
				
				/*
				 *  If responseCode==0->login error; if responseCode==3 login successful;
				 *  otherwise, generic error
				 */	
				if(response.getResponseCode()==0){
					
					// Login error, nickname not set & re-ask for a nickname
					System.out.println(response.getError());
					nickname = null;	
					continue;
				}else{
					
					// Successfully logged in sender mode
					if(response.getResponseCode()==3){
						nickname = lineNick;	
						System.out.println("response error"+response.getError());
						System.out.println("response code"+response.getResponseCode());
					}
					else{
			
						// Generic error, ask again for a valid nickname
						System.out.println("Error logging in: unknown error.");
						continue;
					}
				}
				System.out.println("active?\t"+active);
				System.out.println("Nickname?\t"+nickname);
			}
			
		
			// ChatThread periodically sends your status: waiting for it.		
			ChatRensponse response = (ChatRensponse) iis.readObject();
			
			// Checks the response
			System.out.println("check if active");
			while(active==false){
				System.out.println(active);
				int res = response.getResponseCode();
				//int res = ((ChatRensponse) iis.readObject()).getResponseCode();
				if(res==5)
					active = true;
				else if(res==6)
					active = false;
				else
					
					// An unexpected ChatResponse has been read ->wait for the next one.
					System.out.println("Uhandled error.");
				
				Thread.sleep(1000);
				response = (ChatRensponse) iis.readObject();
			}
				
			String receiver;
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
				else{
					if(line.charAt(0)=='@'){
						
					// I'm sending a private message
						String[] parts;
						parts = line.split("\\:");
						
						// Setting receiver name (in case of public msg = null)
						receiver = parts[0];
					}
				}
				System.out.println("Message:"+line+"\t"+"at"+receiver+"from"+nickname);
				
				/*
				 *  Creates a ChatMessage that can be private or public,and a ChatRequest
				 * to send it in chat.
				 */		
				cm = new ChatMessage(line,nickname, receiver);
				crMess = new ChatRequest(cm);
				oos.writeObject(crMess);
				oos.flush();

				// Waiting for the Server response
				ChatRensponse responseMess = new ChatRensponse();
				System.out.println("Waiting for an answer.");
				responseMess = (ChatRensponse) iis.readObject();
				int rs = responseMess.getResponseCode();
				if(rs==0){
					
					// Server has returned a generic error; writing out error message.
					System.out.println("Invio fallito:" 
							+ responseMess.getError().toString());
				}
				else{
					if(rs==2){
						System.out.println("invio ok");
						
						/*
						 *  Message sent. The response contains the number (count)
						 * associated to the message.
						 */	
						System.out.println(responseMess.getParam());
						System.out.println(responseMess.getError());
						System.out.println(responseMess.getResponseCode());
						size = responseMess.getCount();
						System.out.println("Invio messaggio eseguito con successo. "
								+ "Messaggio in posizione: " + size);
					}
					else{
						System.out.println("Error: unexpected answer from the server.");
					}
				}			
			}

		}catch(Exception e){
			e.printStackTrace();
		}	
		s.close();
	}
	
}