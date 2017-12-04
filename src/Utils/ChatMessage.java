package Utils;

import java.time.LocalDateTime;

public class ChatMessage {

	private String message;
    private String sender;
    private String receiver;
    private String date;


    public ChatMessage (String message, String sender, String receiver){
    	this.message= message;
    	this.sender=sender;
    	this.receiver=receiver;
    	date = LocalDateTime.now().toString();

    }
    
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