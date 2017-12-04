package Server;

import java.util.ArrayList;

import Utils.ChatMessage;
import Utils.ChatRensponse;

public class ChatRoom {
	public static ArrayList<ChatMessage> msg;
	/*Lista messaggi + ultimo progressivo
	int synchronized addMessage(ChatMessage msg);
	Ritorna il progressivo assegnato al messaggio
	List<ChatMessage> listMessages(String chatUser, int lastMessage);
	lastMessage pu� anche essere -1. In tal caso l�elenco dei messaggi contiene solo l�ultimo messaggio (pubblico o privato) destinato all�utente 
*/
	public static ChatRensponse getMessages(int c, String nick) {
		ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
		ChatRensponse ret;
		int i = 0;
		int count = c;

		for(ChatMessage ch : msg){
			if(count<=i &&(ch.getReceiver()==null||ch.getReceiver().equals(nick))){
				list.add(ch);
			}
		i++;
		}	
		count = msg.size();
		
		ret = new ChatRensponse(list);
		// count!!
		if(ret.getResponseCode()==0)
			return (ChatRensponse) ret.getObject();
		else{
			System.out.println(ret.getError());
			return null;
		}
	}
	public static int addMessage(ChatMessage m) {
		if(m!=null)
			msg.add(m);
		return msg.size();
	}

}