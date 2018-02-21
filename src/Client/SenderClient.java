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

public class SenderClient {
	private static int port = 4000;

	public static void main(String[] args) throws IOException {
		String ipaddr = args[0];
		InetSocketAddress addr = new InetSocketAddress(ipaddr, port);
		Socket s = new Socket();
		String nickname = null;
		ChatRequest cr = null;
		ArrayList<String> u = null;

		// Output channel to communicate from client to server
		OutputStream os = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		InputStream is = null;

		try {
			s.connect(addr);
			InputStreamReader reader = new InputStreamReader(System.in);
			BufferedReader buffer = new BufferedReader(reader);
			ChatRequest request;
			ChatRensponse response;
			
			// Output channel to communicate from server to client
			os = s.getOutputStream();
			oos = new ObjectOutputStream(os);

			// Nickname request
			while (nickname == null) {
				System.out.println("Inserire un nickname valido per accedere alla chat:");
				String lineNick = buffer.readLine();

				// New ChatRequest to check if it's a valid nickname to log with
				cr = new ChatRequest("loginrequestsd", lineNick);
				oos.writeObject(cr);
				oos.flush();

				// Waiting server response
				is = s.getInputStream();
				ois = new ObjectInputStream(is);
				response = (ChatRensponse) ois.readObject();

				/*
				 * If responseCode==0->login error; if responseCode==3 login
				 * successful; otherwise, generic error
				 */
				if (response.getResponseCode() == 0) {

					// Login error, nickname not set & re-ask for a nickname
					nickname = null;
					continue;
				} else {

					// Successfully logged in sender mode
					System.out.println("You successfully logged in sender mode. Write '\\help' to see chat options.");
					if (response.getResponseCode() == 3) {
						nickname = lineNick;
					} else {

						// Generic error, ask again for a valid nickname
						System.out.println("Error logging in: unknown error.");
						nickname = null;
						continue;
					}
				}
			}
			String receiver;
			ChatMessage cm;
			ChatRequest crMess;
			System.out.println("Write a message: \n");
			outerloop: while (true) {
				System.out.print(">>");
				String line = buffer.readLine();
				receiver = null;

				switch (line) {
				case "\\quit":
					request = new ChatRequest("quit", nickname);
					request.setParam("sender");
					System.out.println("Request of logout sent.");
					System.out.println(request.getNick() + request.getRequestCode());
					oos.writeObject(request);
					oos.flush();
					is = s.getInputStream();
					ois = new ObjectInputStream(is);
					response = (ChatRensponse) ois.readObject();
					System.out.println("Quit response: " + (String) response.getError() + response.getResponseCode());
					break outerloop;
				case "\\help":
					System.out.println("Hello " + nickname +" here are your chat commands: \n'\\quit' to logout;"
							+ "\n'\\users' to show who is active;\n'\\help' to display chat options;\n'@nickname:'"
							+ " to send private messages; \n!(replace 'nickname' with the user you want to whisper "
							+ "to and don't write the '').");
					break;
				case "\\users":
					request = new ChatRequest("users",nickname);
					request.setParam("sender");
					oos.writeObject(request);
					oos.flush();
					is = s.getInputStream();
					ois = new ObjectInputStream(is);
					response = (ChatRensponse) ois.readObject();
					if(response.getResponseCode()!=7){
						System.out.println("Unknown error: server didn't return the requested list.");
					}
					else{
						u = (ArrayList<String>) response.getParam();
					}
					System.out.println("Active accounts:");
					if(u.size()==0 || u == null)
						System.out.println("no active accounts detected.");
					else{
						for(String aU : u){
							System.out.println(aU);
						}				
					}
					break;

				// Normal message to send
				default:

					/*
					 * Creates a ChatMessage that can be private or public,and a
					 * ChatRequest to send it in chat.
					 */
					if (line.charAt(0) == '@') { // Private message
						String[] parts;
						parts = line.split("\\:");

						// Setting receiver name (in case of public msg = null)
						receiver = parts[0].substring(1, parts[0].length());
						line = parts[1];
					}
					ChatRensponse resAct = null;
					Object param = "";
					ChatRequest req = new ChatRequest("isactive", nickname);

					while (param == null || !((String) param).equals(nickname)) {
						oos.writeObject(req);
						oos.flush();
						is = s.getInputStream();
						ois = new ObjectInputStream(is);
						resAct = (ChatRensponse) ois.readObject();
						param = resAct.getParam();
						Thread.sleep(1000);
					}

					if (resAct.getResponseCode() == 5) {
						cm = new ChatMessage(line, nickname, receiver);
						crMess = new ChatRequest("addmessages", cm);
						crMess.setNick(nickname);
						oos.writeObject(crMess);
						oos.flush();

						// Waiting for the Server response
						ChatRensponse responseMess = new ChatRensponse();
						is = s.getInputStream();
						ois = new ObjectInputStream(is);
						responseMess = (ChatRensponse) ois.readObject();
						int rs = responseMess.getResponseCode();
						if (rs == 0) {

							// Server has returned a generic error; writing out error message.
							System.out.println("Invio fallito:" + responseMess.getError().toString());
						} else {
							if (rs != 2)
								System.out.println("Error: unexpected answer from the server.");
								
							/* else <- Message sent. The response contains the number (count) 
							 associated to the message. Not going to use it. 
								size = responseMess.getCount();		*/
						}
					} else

						// Not active so i can't send messages
						System.out.println("Account not active. Impossible to send messages.");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		s.close();
	}

}