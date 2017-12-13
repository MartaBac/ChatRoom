package Server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import Utils.ChatMessage;
import Utils.ChatRensponse;
import Utils.ChatRequest;

public class ChatThread implements Runnable {

	private Socket client = null;
	private ObjectOutputStream oos;
	private  static String type = null;
	
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

			//Canale input per mess dal client a Server

			is = this.client.getInputStream();
			ois = new ObjectInputStream(is);
			// Login
			while(type == null){
				ChatRequest request = (ChatRequest) ois.readObject();
				String code = request.getRequestCode();
				String param = (String) request.getParam();
				ChatRensponse cr = null;
				System.out.println("Execute: "+code+param);
			
				//Smistamento richieste
				switch (code){
					case "loginrequestsd":
						cr = this.loginSender( param);
						if(cr.getResponseCode()==1)
						break;
					case "loginrequestrc":
						cr = loginReceiver( param);
						break;			
				}
				// Ho la risposta cr
				oos.writeObject(cr);
				oos.flush();
			}
			// Wait for messages
			while(true){
				System.out.println("here fools");
				
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
				System.out.println("chat thread before if");
							
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
		}catch(Exception e ){
			System.out.println("ChatTread Exception:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public static ChatRensponse loginSender(String param) throws IOException{
		
		System.out.println("Start Sender with param: " + param);
		Users check = null;
		ChatRensponse k = new ChatRensponse();
		Users u=  new Users( param, false);
		check = ChatServer.utenti.putIfAbsent(param, u);
		//
		//
		Users temp = ChatServer.utenti.get(param);
		// Se check == null vuol dire che non c'era il record in memoria, sicuramente non attivo
		if(check == null){		
			k.setError("Logged in sender mode.",2);
			ChatServer.utenti.get(param).setActive(false);
			}
		else{
			if(temp.getSender()==false){
				ChatServer.utenti.get(param).setSender(true);
				k.setError("Logged in sender mode.",2);
			}
			else{
				System.out.println("Error logging in. Someone else is already logged with this nickname in sender mode");
				//error logging in
				k.setError("Someone already logged with the selected nickname in sender mode.",0);
				return k;
			}
			
			if(temp.getSender()== true){
				System.out.println("4" + temp.getReceiver() + temp.getSender());
				System.out.println(param);
				ChatServer.utenti.get(param).setActive(true);
				
				k.setError("Logged in receiver and sender mode. Account activated.",3);						
				ChatServer.utenti.get(param).setActive(true);
			}
		
		}
		System.out.println("end: " + k.getResponseCode() + k.getError());
		// stampa su sender return out;
		type = "sender";
		return k;
	}
	
	public static ChatRensponse loginReceiver(String param) throws IOException{
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
			k.setError("Logged in receiver mode.",1);
			ChatServer.utenti.get(param).setActive(false);
			}
		else{
			System.out.println("3" + temp.getReceiver() + temp.getSender());
			if(temp.getReceiver()==false){
				ChatServer.utenti.get(param).setReceiver(true);
				k.setError("Logged in receiver mode.",1);
			}
			else{
				System.out.println("Error logging in. Someone else is already logged with this nickname in receiver mode");
				//error logging in
				k.setError("Someone already logged with the selected nickname.",0);
				return k;
			}
			
			if(temp.getSender()== true){
				System.out.println("4" + temp.getReceiver() + temp.getSender());
				System.out.println(param);
				ChatServer.utenti.get(param).setActive(true);
				
				k.setError("Logged in receiver and sender mode. Account activated.",3);		
				ChatServer.utenti.get(param).setActive(true);
			}		
		}

		// Stampare su sender out;
		type = "receiver";
		return k;
		
	}

}



