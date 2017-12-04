package Utils;

import java.util.ArrayList;

public class ChatRensponse {

	private Object param;
	private int responseCode;
	private String error;
	
	public ChatRensponse (Object p){
		ArrayList<ChatMessage> k = null;
    	if(p.getClass().equals(k.getClass())){
    		this.param = p ;
    		responseCode = 0;
    	}
    	else {
    		error = "invalid Object format";
    		responseCode = -1;
    	}
    	
    }
    
    public Object getObject(){
    	return param;
    }
    
    public Object getError(){
    	return error;
    }
    
    public int getResponseCode(){
    	return responseCode;
    }
    
}
