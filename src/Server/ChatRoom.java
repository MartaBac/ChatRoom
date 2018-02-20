package Server;

import java.util.ArrayList;

import Utils.ChatMessage;

public class ChatRoom {
	public static ArrayList<ChatMessage> msg = new ArrayList<ChatMessage>();
	/*
	Lista messaggi + ultimo progressivo
	int synchronized addMessage(ChatMessage msg);
	Ritorna il progressivo assegnato al messaggio
	List<ChatMessage> listMessages(String chatUser, int lastMessage);
	lastMessage può anche essere -1. In tal caso l’elenco dei messaggi contiene solo l’ultimo messaggio (pubblico o privato) destinato all’utente 
*/

	public static ChatUpdate getMessages(int c, String nick) {
		ChatUpdate cu=null;	
		if(msg.size() <= c || msg.size() == 0){
			System.out.println("No new messages available");
			cu = new ChatUpdate(null, c);
			return cu;
		}
		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
		int i = 0;
		int count = c;
		for(ChatMessage ch : msg){
			if((count == -1 || count <= i) &&(ch.getReceiver()==null||ch.getReceiver().equals(nick))){
				list.add(ch);
			}
		i++;
		}	
		count = msg.size();	
		cu = new ChatUpdate(list,i);
		return cu;	
	}
	
	
	public static int addMessage(ChatMessage m) {
		if(m!=null)
			msg.add(m);
		else
			return -1;
		System.out.println("Message size"+msg.size());
		return msg.size();
	}

}
