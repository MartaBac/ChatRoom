package Utils;
/*
 *  ChatMessage defines the parameters that can have every message sent through the chat.
 * There will be the corpse of the message, WHO sent it, WHEN it has been sent and, in 
 * case of a private message, for who this message is.
 */

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessage implements Serializable {

	private String message;
    private String sender;
    private String receiver;
    private String date;

	/*
	 *  Constructor in case of a private message, contains: message corpse, who sent it and
	 * the recipient.
	 */
    public ChatMessage (String message, String sender, String receiver){
    	this.message= message;
    	this.sender=sender;
    	this.receiver=receiver;
    	date = LocalDateTime.now().toString();

    }
    /*
     * Constructor for public messages: message corpse, sender.
     */
    
    public ChatMessage (String message, String sender){
    	this.message= message;
    	this.sender=sender;
    	this.receiver=null;

    }
    
    public String getMessage(){
    	return message;
    }
    
    public String getSender(){
    	return sender;
    }
    
    public String getReceiver(){
    	return receiver;
    }
    
    public String getTime(){
    	return date;
    }
    
}