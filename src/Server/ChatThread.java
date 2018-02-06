package Server;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Utils.ChatMessage;
import Utils.ChatRensponse;
import Utils.ChatRequest;

public class ChatThread implements Runnable {
	private Socket client = null;
	private ObjectOutputStream oos;
	private  static String type = null;
	private  static boolean act = false;

	public ChatThread (Socket client){
		this.client = client;		
	}

	@Override
	public void run() {
		try{
			System.out.println("new thread");
			InputStream is = this.client.getInputStream();
			ObjectInputStream ois = null;
			OutputStream os = client.getOutputStream();
			oos = new ObjectOutputStream(os);
			ChatRequest request = null;
			String code;
			String param = null ;
			ChatRensponse cr= null;

			// Input channel to communicate from client to server
			is = this.client.getInputStream();
			ois = new ObjectInputStream(is);
			
			// Login
			while(type == null){
				System.out.println("While ON");
				request = (ChatRequest) ois.readObject();
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
					case "active?":
						cr = new ChatRensponse();
						if(ChatServer.utenti.get(request.getNick()).isActive()==true){				
							cr.setRensponseCode(6);
						}
						else
							cr.setRensponseCode(7);
						break;
				}
				System.out.println("Switch ended.");
				oos.writeObject(cr);
				oos.flush();
			}
			cr = new ChatRensponse();
			
			
			// Preparing chatResponse with response code 6 o 5 ( inactive, active )
			if(act == false)
				cr.setRensponseCode(6);
			else
				cr.setRensponseCode(5);
			
			
			oos.writeObject(cr);
			oos.flush();
			System.out.println("Checking if logged active." + type+ "  " + param);
			boolean check = ChatServer.utenti.get(param).isActive();

			// Waiting for the user to become active
			while(check == false){	
				System.out.println("Checking if logged active." + type);
				check = ChatServer.utenti.get(param).isActive();
				System.out.println(check);
				Thread.sleep(1000);	
			}
			cr.setRensponseCode(5);
			
			oos.writeObject(cr);
			oos.flush();
			
			while (true){
				request = (ChatRequest) ois.readObject();
				code = request.getRequestCode();
				System.out.println("Execute: "+code+".cycle 2");
				
				switch (code){
					case "getmessages":
						System.out.println("get messages :-D");
						cr = getMessages((int) request.getParam(), request.getNick());
						break;
					case "addMessage":
						System.out.println("add message :-)");
						ChatMessage cm = (ChatMessage) request.getParam();
						String r = cm.getReceiver();
						int c = -1;
						System.out.println("r:"+r);
						//commentando il codice si manda messaggi anche ai non loggati
						//if(r == null || ChatServer.utenti.containsKey(r)==true ){
							// Se il destinatario esiste posso mandare il messaggio
							// --- check --- se esiste ? o anche se no? se attivo o inattivo..?
							c = ChatRoom.addMessage((ChatMessage) request.getParam());
						//}	
						//else{
						//	System.out.println("Invalid receiver.");
						//}
							// preparo risposta con l'indice del messaggio
						
						cr = new ChatRensponse(2,c);
						break;
				}
			// Ho la risposta cr
			oos.writeObject(cr);
			oos.flush();
			}
			
			// Wait for messages
			/*
			while(true){				
				Object in = ois.readObject();
				ChatRequest cReq = null;
				if(!in.getClass().equals(ChatRequest.class)){
					System.out.println("Fatal error. Unexpected input format");
					
				}
				else{
					cReq = (ChatRequest) in;
				}
				int count = -1;
				ChatRensponse mess = new ChatRensponse();							
				if(cReq.getParam().getClass().equals(ChatMessage.class) ){
					ChatMessage cm = (ChatMessage) cReq.getParam();
					count = ChatRoom.addMessage(cm);
					mess.setParam(count);
					if(count!=-1){	
						mess.setError("Messaggio ricevuto con successo.",4);	
					}
					else{
						mess.setError("Errore invio messaggio.",5);
					}
				}
				oos.writeObject(mess);
				oos.flush();
				

			}
			*/
		}catch(Exception e ){
			System.out.println("ChatTread Exception:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 *   Function that manages login requests in sender mode. 
	 *  Returns a ChatResponse; responseCode = 3 if logged in sender mode; =5
	 *  if logged in sender mode and active;= 0 if login failed,already logged as sender.
	 */
	public static ChatRensponse loginSender(String param) throws IOException{
		System.out.println("Logging sender " + param);
		System.out.println("Start Sender with param: " + param);
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
					k.setError("Logged in sender mode.");
					k.setRensponseCode(3);
				}
				else{ // In list and already logged as receiver -> active
					k.setError("Active");
					act = true;
					k.setRensponseCode(5); 
				}
			}
			else{
				System.out.println("Error logging in. Someone else is already logged "
						+ "with this nickname in sender mode");
				//error logging in
				k.setError("Someone already logged with the selected nickname in sender"
						+ " mode.");
				k.setRensponseCode(0);
				return k;
			}
			
		}
		
		System.out.println("--------");	
		// Sets that you are logged as sender.
		type = "sender";
		System.out.println("returning"+k.getResponseCode()+k.getCount());
		return k;
	}
	
	public static ChatRensponse loginReceiver(String param) throws IOException{
		System.out.println("Logging receiver " + param);
		Users check = null;
		ChatRensponse k = new ChatRensponse();

		Users u=  new Users( param, true);
		check = ChatServer.utenti.putIfAbsent(param, u);
		//
		System.out.println(ChatServer.utenti.toString());
		System.out.println(check);
		//
		Users temp = ChatServer.utenti.get(param);
		// Se check == null vuol dire che non c'era il record in memoria, sicuramente non attivo
		if(check == null){		
			k.setError("Logged in receiver mode.");
			k.setRensponseCode(4);
			ChatServer.utenti.get(param).setActive(false);
			}
		else{
			System.out.println("3" + temp.getReceiver() + temp.getSender());
			if(temp.getReceiver()==false){
				ChatServer.utenti.get(param).setReceiver(true);
				k.setError("Logged in receiver mode.");
				k.setRensponseCode(4);
			}
			else{
				System.out.println("Error logging in. Someone else is already logged with this nickname in receiver mode");
				//error logging in
				k.setError("Someone already logged with the selected nickname.");
				k.setRensponseCode(0);
				return k;
			}
			/*
			if(temp.getSender()== true){
				System.out.println("4" + temp.getReceiver() + temp.getSender());
				System.out.println(param);
				ChatServer.utenti.get(param).setActive(true);
				
				k.setError("Logged in receiver and sender mode. Account activated.",5);		
				ChatServer.utenti.get(param).setActive(true);
			}		
			*/
		}

		// Stampare su sender out;
		type = "receiver";
		return k;
		
	}
	
	public static ChatRensponse getMessages(int count, String nick){
		
		ChatRensponse cre;
		/*
		if(ChatRoom.getMessages(count,nick)==null){
			System.out.println("No messages to retrieve");
			return null;
		}*/
			
		ArrayList<ChatMessage> msg = new ArrayList<ChatMessage>();
		ChatUpdate cu =  ChatRoom.getMessages(count,nick);
		
		// Lista messaggi da aggiungere nella chat del receiver
		msg = cu.getMessages();
		int c = cu.getCount();
		
		cre = new ChatRensponse(msg);
		cre.setCount(c);
		if(cre.getResponseCode()!=-1){
			cre.setRensponseCode(1);
		}
		return cre;
				
		
	}

}



