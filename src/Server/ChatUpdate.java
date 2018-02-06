package Server;

/*
 * Class to manage the updates of the receivers
 */
import java.util.ArrayList;

import Utils.ChatMessage;

public class ChatUpdate  {
	ArrayList<ChatMessage> list;
	int count;
	
    public ChatUpdate (ArrayList<ChatMessage> list, int count){
        this.list = list;
        this.count = count;

    }
    public int getCount(){
    	return this.count;
    }
    
    public ArrayList<ChatMessage> getMessages(){
    	return this.list;
    }
}
