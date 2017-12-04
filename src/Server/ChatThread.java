package Server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import Utils.ChatMessage;

public class ChatThread implements Runnable {

	private Socket client = null;
	static private BufferedWriter outbuffer;
	
	public ChatThread (Socket client){
		this.client = client;
		
	}

	@Override
	public void run() {
		try{
			InputStream is = this.client.getInputStream();
			String nick = null;
			String type = null;
			//Canale input per mess dal client a Server
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader buffer = new BufferedReader(reader);
			String line = buffer.readLine();
			
			//per ricevere strutture java :deserializzo
			//vedi StudentDeserializer.java
			//ObjectInputStream ois = new ObjectInputStream(is);
			
			OutputStream os = this.client.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(os);
			outbuffer = new BufferedWriter(wr);
			System.out.println(line);

			//tentativo login

		}catch(Exception e ){
			System.out.println("ChatTread Exception:" + e.getMessage());
			e.printStackTrace();
		}
		

	}
	
	
	public static String execute(String requestCode,Object param) throws IOException{
		String r= null;

		System.out.println("Execute: "+requestCode+param);
		switch (requestCode){
		case "loginrequestsd":
				System.out.println("Invoke sender");
				loginSender((String) param);
				break;
		case "loginrequestrc":
				loginReceiver((String) param);
				break;
				
	}
		return r;
	}
	
	
	public static void loginSender(String param) throws IOException{
		System.out.println("Start Sender with param: " + param);
		Users check = null;
		String out = null;
		Users u=  new Users( param, false);
		check = ChatServer.utenti.putIfAbsent(param, u);
		//
		System.out.println(ChatServer.utenti.toString());
		System.out.println(check);
		//
		Users temp = ChatServer.utenti.get(param);
		// Se check == null vuol dire che non c'era il record in memoria, sicuramente non attivo
		if(check == null){		

				out = "Logged correctly as "+param+" in sender mode. Not active.";


			ChatServer.utenti.get(param).setActive(false);
			}
		else{
			System.out.println("3" + temp.getReceiver() + temp.getSender());
			if(temp.getSender()==false)
				ChatServer.utenti.get(param).setSender(true);
			else{
				System.out.println("Error logging in. Someone else is already logged with this nickname in sender mode");
				//error logging in
				return;
			}
			
			if(temp.getSender()== true){
				System.out.println("4" + temp.getReceiver() + temp.getSender());
				System.out.println(param);
				ChatServer.utenti.get(param).setActive(true);
				
				out ="Logged as "+ param + ". Your account is ready to join the chatRoom. Receiver: "
						+temp.getReceiver()+"-Sender:" + temp.getSender();

				
				
				ChatServer.utenti.get(param).setActive(true);
			}
		
		}
		// stampa su sender return out;
		outbuffer.write("ggggggggggggg"); 
		outbuffer.newLine();
		outbuffer.flush();
		
	}
	
	public static void loginReceiver(String param) throws IOException{
		Users check = null;
		String out = null;

		Users u=  new Users( param, true);
		check = ChatServer.utenti.putIfAbsent(param, u);
		//
		System.out.println(ChatServer.utenti.toString());
		System.out.println(check);
		//
		Users temp = ChatServer.utenti.get(param);
		// Se check == null vuol dire che non c'era il record in memoria, sicuramente non attivo
		if(check == null){		

				out = "Logged correctly as "+param+" in receiver mode. Not active.";


			ChatServer.utenti.get(param).setActive(false);
			}
		else{
			System.out.println("3" + temp.getReceiver() + temp.getSender());
			if(temp.getReceiver()==false)
				ChatServer.utenti.get(param).setReceiver(true);
			else{
				System.out.println("Error logging in. Someone else is already logged with this nickname in receiver mode");
				//error logging in
				return;
			}
			
			if(temp.getSender()== true){
				System.out.println("4" + temp.getReceiver() + temp.getSender());
				System.out.println(param);
				ChatServer.utenti.get(param).setActive(true);
				
				out ="Logged as "+ param + ". Your account is ready to join the chatRoom. Receiver: "
						+temp.getReceiver()+"-Sender:" + temp.getSender();

				
				
				ChatServer.utenti.get(param).setActive(true);
			}
		
		}
		// Stampare su sender out;
		outbuffer.write("ggggggggggggg"); 
		outbuffer.newLine();
		outbuffer.flush();
		
	}

}



