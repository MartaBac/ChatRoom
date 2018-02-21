package Server;

/*
 * Class to manage the updates of the receivers
 */
import java.util.ArrayList;

import Utils.ChatMessage;

public class ChatUpdate  {
	ArrayList<ChatMessage> list;
	int count;
	boolean avaiable;
	
    public ChatUpdate (ArrayList<ChatMessage> list, int count){
    	if(list!=null){
    		this.list = list;
    		this.count = count;
    		avaiable = true;
    		}
    	else
    		avaiable = false;

    }
    public int getCount(){
    	return this.count;
    }
    
    public boolean getAvaiable(){
    	return avaiable;
    }
    
    public ArrayList<ChatMessage> getMessages(){
    	return this.list;
    }
}
