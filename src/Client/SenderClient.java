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
		ChatRequest cr = null;	
		int size =-1;	
		
		// Output channel to communicate from client to server
		OutputStream os = null;		
		ObjectOutputStream oos = null;
		
		ObjectInputStream ois = null;
		InputStream is = null;
		
		try {
			s.connect(addr);
			
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader buffer = new BufferedReader(reader);	
			
			// Output channel to communicate from server to client
			os = s.getOutputStream();
			oos = new ObjectOutputStream(os);
			
			// Nickname request
			while (nickname==null){
				System.out.println("Inserire un nickname valido per accedere alla chat");
				String lineNick = buffer.readLine();
				
				// New ChatRequest to check if it's a valid nickname to log with
				cr = new ChatRequest("loginrequestsd",lineNick);			
				oos.writeObject(cr);
				oos.flush();
				
				// Waiting server response
				is = s.getInputStream();
				ois = new ObjectInputStream(is);
				ChatRensponse response = (ChatRensponse) ois.readObject();
				System.out.println("Server Response" + response.getResponseCode() + (String) response.getError());
				System.out.println(response.getParam());
				
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
			String receiver;
			ChatMessage cm;
			ChatRequest crMess;
					
			while(true){
				System.out.println("Write a message: \n");
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
						receiver = parts[0].substring(1, parts[0].length());
						System.out.println("7777777777777777" + parts[1]);
						line = parts[1];
					}
				}
				System.out.println("Message:"+line+"\t"+"at\t"+receiver+"from\t"+nickname);
				
				/*
				 *  Creates a ChatMessage that can be private or public,and a ChatRequest
				 * to send it in chat.
				 */
				
				ChatRensponse resAct = null ;			
				System.out.println("Logged as \t" + nickname);
				// Finchè non ricevo la risposta al questo thread
				Object param = "";
				ChatRequest req = new ChatRequest("isactive", nickname);
							
				while(param == null || !((String) param).equals(nickname)){				
					System.out.println("Going to enter while:"+ param +" "+(String) param);
					System.out.println("Start While. req: " + req.getRequestCode() + " " + req.getNick());
					oos.writeObject(req);
					oos.flush();
					System.out.println("wrote");				
					is = s.getInputStream();
					ois = new ObjectInputStream(is);				
					resAct = (ChatRensponse) ois.readObject();				
					System.out.println("read");
					param =  resAct.getParam();
					System.out.println("I'm in");
					System.out.println(param);				
					Thread.sleep(1000);
				}
				System.out.println("Received response");
							
				if(resAct.getResponseCode()==5){
					active = true;
					System.out.println("Creating chat message : line - "+ line + "nickname - " + nickname +"-receiver"+ receiver);
					cm = new ChatMessage(line,nickname, receiver);
					crMess = new ChatRequest( "addmessages", cm);
					crMess.setNick(nickname);
					oos.writeObject(crMess);
					oos.flush();
				
					System.out.println("Chat message sent");
					// Waiting for the Server response
					ChatRensponse responseMess = new ChatRensponse();
					System.out.println("Waiting for an answer.");
					is = s.getInputStream();
					ois = new ObjectInputStream(is);
					responseMess = (ChatRensponse) ois.readObject();
					System.out.println("Response received");
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
				else{
					// Not active so i can't send messages
					System.out.println("Account not active. Impossible to send messages.");
					active = false;
				}
			}				
		}catch(Exception e){
			e.printStackTrace();
		}	
		s.close();
	}
	
}