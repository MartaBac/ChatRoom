package Utils;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRensponse implements Serializable {

	private Object param;
	private int responseCode;
	private String error;
	
	public ChatRensponse (){
		error = "";
		param = null;
		responseCode = -1;
	}
	
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
	
	
    public void setParam(Object param){
    	System.out.println("set param chat rensponse");
    	this.param = (Object) param;
    }
	
    public void setError(String error, int responseCode){
    	System.out.println("set error chat rensponse");
    	this.responseCode=responseCode;
    	this.error=error;
    }
    
    public Object getParam(){
    	return param;
    }
    
    public Object getError(){
    	return error;
    }
    
    public int getResponseCode(){
    	return responseCode;
    }
    
}
