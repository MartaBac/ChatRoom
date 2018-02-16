package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer {

	private static int port = 4000;
	//lista gente connessa
	private static ServerSocket server;
	public static HashMap<String, Users> utenti = new HashMap<String, Users>();
	public static void main(String[] args) {
		try {
			System.out.println("SERVER STARTED");
			server = new ServerSocket(port);
			
			while(true){
				Socket s = server.accept();
				System.out.println("CONNECTION ACCEPTED");

				//Runner
				ChatThread chatThread = new ChatThread(s); 
				//Launcher
				Thread chatThreadT = new Thread(chatThread);
				chatThreadT.start();
			}
				
		}catch(Exception e ){
			e.printStackTrace();
		}

	}
	
	

}
