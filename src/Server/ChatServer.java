package Server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import Client.ReceiverClient;
import Utils.ChatMessage;
import Utils.ChatRensponse;
public class ChatServer {

	private static int port = 4000;
	//lista gente connessa
	public static HashMap<String, Users> utenti = new HashMap<String, Users>();
	public static void main(String[] args) {
		try {
			System.out.println("SERVER STARTED");
			ServerSocket server = new ServerSocket(4000);
			
			
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
