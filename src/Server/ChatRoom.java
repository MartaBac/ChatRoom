package Server;

import java.util.ArrayList;

import Utils.ChatMessage;
import Utils.ChatRensponse;

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
		System.out.println("ChatRoom getMessages");
		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
		ChatRensponse ret;
		int i = 0;
		int count = c;
		ChatUpdate cu=null;
		System.out.println("Entering loop");
		for(ChatMessage ch : msg){
			System.out.println("In loop");
			if(count<=i &&(ch.getReceiver()==null||ch.getReceiver().equals(nick))){
				list.add(ch);
			}
		i++;
		}	
		System.out.println("Out loop");
		count = msg.size();
		System.out.println(count);
		ret = new ChatRensponse(list);
		// count!!
		if(ret.getResponseCode()==1){
			//
			cu = new ChatUpdate(list,i);
			return cu;
		}
			//return (ChatRensponse) ret.getParam();
		else{
			System.out.println("Get Messages failed");
			System.out.println(ret.getResponseCode());
			System.out.println(ret.getError());
			cu = new ChatUpdate(null,-1);
			return cu;
		}
		
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
