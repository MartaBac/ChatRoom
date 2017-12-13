package Utils;


import java.io.IOException;
import java.io.Serializable;

import Server.ChatThread;

public class ChatRequest implements Serializable {
	private String requestCode;
	private String error;
	private Object param;
	private int responseCode;
	public ChatRequest (){
		requestCode = "";
		error ="";
		responseCode=0;
		param = null;	
	}
	
	public ChatRequest (String requestCode,String error,Object param,int responseCode){
		this.requestCode = requestCode;
		this.error = error;
		this.responseCode = responseCode;
		this.param = param;		
	}
	
	public ChatRequest (String requestCode,Object p){
		this.requestCode = requestCode;
		param = p;	
	}
	
	public ChatRequest (Object p){
		requestCode = "";
		error ="";
		responseCode=0;
		param = p;	
		
		if(p.getClass().equals(ChatMessage.class)){
			requestCode = "addMessage";
		}
	}
	
	public String getRequestCode(){
		return this.requestCode;
	}
	public Object getParam(){
		return param;
	}
	
	public int getResponseCode(){
		return responseCode;
	}
	public Object getError(){
		return param;
	}
}