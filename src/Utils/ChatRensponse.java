package Utils;

/*
 *  This class manages responses from the server to the client. 
 * That allows communication between server and client. This class is server-side.
 */

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRensponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object param;
	/*
	 * Response codes:
	 * -1 error
	 * 0 login error
	 * +1 message list
	 * +2 index of the message added
	 * +3 logged in sender mode
	 * +4 logged in receiver mode
	 * +5 account activated
	 * +6 account disabled
	 */
	private int responseCode; 
	private String error;
	private int count;
	
	public ChatRensponse (){
		error = "";
		param = null;
		responseCode = -1;
		count = -1;
	}
	
	/*
	 *  If to the constructor is given an ArrayList of ChatMessage, sets the param
	 * to this list and the response code to '+1', message list.
	 */
	public ChatRensponse (Object p){
		ArrayList<ChatMessage> k = new ArrayList<ChatMessage>();
		System.out.println("ChatResponse creation: "+ p.getClass() + "\t" + k.getClass());
    	if(p.getClass().equals(k.getClass())){
    		this.param = p ;

    		responseCode = 1;
    	}
    	else {
    		error = "invalid Object format";
    		responseCode = -1;
    	}
    	
    }
	/*
	 *  Constructor in case the response is an error. Sets the response code to an int
	 * and the error to a String containing the error specs.
	 */
	public ChatRensponse (String error, int rcode){
		this.error = error;
		this.responseCode = rcode;	
	}
	
	/*
	 *  Sets the response code to +2 and the count number.
	 */
	public ChatRensponse (int responseCode, int count){
		this.responseCode = responseCode;
		this.count = count;	
	}
	
	/*
	 *  Generic set of a response code
	 */
	public void setRensponseCode(int res){
		this.responseCode = res;
	}
	

    public void setParam(Object param){
    	System.out.println("set param chat rensponse");
    	this.param = (Object) param;
    }
	
    public void setError(String error){
    	System.out.println("set error chat rensponse\t"+error);
    	this.error=error;
    	this.responseCode = 2;
    }
    
    
	/*
	 * Return param (Object)
	 */
    public Object getParam(){
    	return param;
    }
    
    public Object getError(){
    	return error;
    }
    
    /*
     * Return responseCode: 
     * -1 se errore
	 * 0 errore login
	 * +1 per lista dei messaggi
	 * +2 se è stato aggiunto un messaggio, ritorna l'indice
	 * +3 logged in sender mode
	 * +4 logged in receiver mode
	 * +5 account attivato
	 * +6 account disattivo
     */
    public int getResponseCode(){
    	return responseCode;
    }
    
    public void setCount(int c){
    	this.count = c;
    }
    
    public int getCount(){
    	return this.count;
    }
}
