package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import Utils.ChatMessage;
import Utils.ChatRensponse;
import Utils.ChatRequest;

public class ChatThread implements Runnable {
	private Socket client = null;
	private  static String type = null;
	private boolean exit = false;
	private String param = null;

	public ChatThread (Socket client){
		this.client = client;		
	}

	@Override
	public void run() {		
	ChatRequest request = null;
	String code;
	ChatRensponse cr = null;
	InputStream is = null;
	OutputStream os = null;
	ObjectInputStream ois = null;
	ObjectOutputStream oos = null;

	type = null;	
		try{		
			
			System.out.println("New thread");			
			
			is = client.getInputStream();
			ois = new ObjectInputStream(is);
			
			while(!exit){		
				
			// Read object from input stream			
			// Login
				System.out.println("While ON");		
				Object o = ois.readObject();
				request = (ChatRequest) o;
				code = request.getRequestCode();
				param = (String) request.getNick();		
				System.out.println("Execute: "+code+param);
				
				// Requests sorting
				switch (code){
					case "loginrequestsd":
						cr = ChatThread.loginSender(param);
						break;
						
					case "loginrequestrc":
						cr = ChatThread.loginReceiver(param);
						break;
						
					case "isactive":
						cr.setParam(param);
						if(ChatServer.utenti.get(param).isActive()==true)
							cr.setRensponseCode(5);
						else
							cr.setRensponseCode(6);
						System.out.println(param + " is active result:\t" + cr.getResponseCode());
						break;
						
					case "quit":
						System.out.println("Logging out.");
						exit = true;						
						break;
						
					case "getmessages":
						if(ChatServer.utenti.get(param).getActive()==false){
							System.out.println("Unable to send the message: your account is not active.");
							break;
						}
						cr = getMessages((int) request.getParam(), request.getNick());
						break;
						
					case "addmessages":
						if(ChatServer.utenti.get(param).getActive()==false){
							System.out.println("Unable to send the message: your account is not active.");
							break;
						}
						ChatMessage cm = (ChatMessage) request.getParam();
						
						// Adds the message and returns index 
						int c = ChatRoom.addMessage(cm);
						System.out.println("Receiver:"+cm.getReceiver());
						cr = new ChatRensponse(2,c);
						break;					
				}
				os = this.client.getOutputStream();
				oos = new ObjectOutputStream(os);
				oos.writeObject(cr);
				oos.flush();	
				Thread.sleep(1000);
			}			
		}catch(Exception e ){
			System.out.println(type + " ChatThread Exception:" + e.getMessage());
			System.out.println(param + " " + cr.getResponseCode());
			e.printStackTrace();
		}
	}

	/*
	 *   Function that manages login requests in sender mode. 
	 *  Returns a ChatResponse; responseCode = 3 if logged in sender mode; =5
	 *  if logged in sender mode and active;= 0 if login failed,already logged as sender.
	 */
	public static ChatRensponse loginSender(String param) throws IOException{
		System.out.println(type + "Logging sender " + param);
		System.out.println(type + "Start Sender with param: " + param);
		Users check = null;
		ChatRensponse k = new ChatRensponse();
		
		Users u=  new Users( param, false); // 'false' = sender mode
		
		/*
		 *   Adds the new user in the list of the 'utenti' of the server only if there
		 *  isn't already a user with the same nickname. 'check' will be = to null if
		 *  the user has been added now.
		 */
		check = ChatServer.utenti.putIfAbsent(param, u);
		
		Users temp = ChatServer.utenti.get(param);
		
		if(check == null){	
			
			// For sure not active cuz just added -> logged only in sender mode
			k.setError("Logged in sender mode.");
			k.setRensponseCode(3);
			ChatServer.utenti.get(param).setActive(false);
			}
		else{
			
			// The user was already in the list of users, but not logged as sender
			if(temp.getSender()==false){
				ChatServer.utenti.get(param).setSender(true);
				if(temp.getReceiver()==false){ // In list but not logged as receiver
					
					k.setError("Logged in sender mode. Not active");
					k.setRensponseCode(3);
				}
				else{ 
					
					// In list and already logged as receiver -> active
					k.setError("Logged and active");
					k.setRensponseCode(3); 
				}
			}
			else{			
				k.setError(type + "Someone already logged with the selected nickname in sender"
						+ " mode.");
				System.out.println(k.getError());
				k.setRensponseCode(0);
				return k;
			}		
		}
		
		// Sets that you are logged as sender.
		type = "sender";
		System.out.println(type + "returning sender response code: \t"+k.getResponseCode() + "and count \t"  +k.getCount());	
		return k;
	}
	
	public static ChatRensponse loginReceiver(String param) throws IOException{
		System.out.println(type + "Logging receiver " + param);
		Users check = null;
		ChatRensponse k = new ChatRensponse();
		Users u=  new Users( param, true);
		check = ChatServer.utenti.putIfAbsent(param, u);
		Users temp = ChatServer.utenti.get(param);
		
		// Case for which the record wasn't in memory (-> for sure not active)
		if(check == null){		
			k.setError(type + "Logged in receiver mode.");
			k.setRensponseCode(4);
			ChatServer.utenti.get(param).setActive(false);
			}
		else{
			
			// Record already in memory and user logged in sender mode
			if(temp.getReceiver()==false){
				System.out.println("Already logged in sender mode");
				ChatServer.utenti.get(param).setReceiver(true);
				if(ChatServer.utenti.get(param).getSender()==true){
					ChatServer.utenti.get(param).setActive(true);
					k.setError(type + "Logged in receiver mode. Active.");
				}
				else{
					ChatServer.utenti.get(param).setActive(false);
					k.setError(type + "Logged in receiver mode. Not active.");
				}
				k.setRensponseCode(4);
			}
			
			// Record already in memory and user logged in receiver mode
			else{
				System.out.println(type + "Error logging in. Someone else is already logged with this nickname in receiver mode");
				//error logging in
				k.setError(type + "Someone already logged with the selected nickname.");
				k.setRensponseCode(0);
				return k;
			}
		}
		type = "receiver";
		return k;		
	}
	
	public static ChatRensponse getMessages(int count, String nick){		
		ChatRensponse cre;
		System.out.println("count:"+count+"nick"+nick);
		ChatUpdate cu =  ChatRoom.getMessages(count,nick);
		
		// If there has been an error: getMessages returns a null value
		System.out.println("ChatUpdate in ChatThread-getMessages: " + cu.getCount() + "available/mess :" + cu.getAvaiable() );
	
		if(cu.getAvaiable()==false){
			return cre=new ChatRensponse();
		}
		cre = new ChatRensponse(cu.getMessages());
		cre.setCount(cu.getCount());
		if(cre.getResponseCode()==-1){
			System.out.println("Error. Unexpected object format.");
		}
		else if(cre.getResponseCode() == 1)
			System.out.println("Chat Response contains an object: ArrayList<ChatMessages>");	
		return cre;	
	}
}



