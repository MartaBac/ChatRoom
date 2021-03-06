package Utils;

/*
 *  This class manages requests from client to server. 
 * That allows communication client-server. It's client-sided.
 */

import java.io.Serializable;

public class ChatRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	/*
	 * request code =  quit -> param contains if send or receiver
	 * request code =  isactive
	 * request code = sendmessage
	 */
	private String requestCode;
	private String nickname;
	private Object param;
	private int responseCode;
	public ChatRequest (){
		requestCode = "";
		nickname ="";
		responseCode=0;
		param = null;	
	}
	
	public ChatRequest (String requestCode,String nick,Object param,int responseCode){
		this.requestCode = requestCode;
		this.nickname = nick;
		this.responseCode = responseCode;
		this.param = param;		
	}
	
	/*
	 *  Constructor that initialize a requestCode and if the parameter Object 'p'
	 * is a String, set 'nickname' to 'p', otherwise sets 'param' to it.
	 */
	public ChatRequest (String requestCode,Object p){
		this.requestCode = requestCode;
		if(p.getClass().equals(String.class))
			this.nickname = (String) p;
		else
			param = p;	
	}
	
	/*
	 * Sets a request code, param and nickname
	 */
	public ChatRequest (String requestCode,Object p,String nickname){
		this.requestCode = requestCode;
		param = p;	
		this.nickname = nickname;
	}
	

	public ChatRequest (String requestCode,String nickname){
		this.requestCode = requestCode;	
		this.nickname = nickname;
	}

	/*
	 *  Set param to the object given. If object.class = ChatMessage automaticcaly
	 * sets requestcode for message adds.
	 */
	public ChatRequest (Object p){
		requestCode = "";
		nickname ="";
		responseCode=0;
		param = p;	
		
		if(p.getClass().equals(ChatMessage.class)){
			requestCode = "addMessage";
		}
	}
	
	public void setRequestCode(String r){
		this.requestCode = r;
	}
	
	public String getRequestCode(){
		return this.requestCode;
	}
	public Object getParam(){
		return param;
	}
	
	public void setParam(Object o){
		this.param = o;
	}
	
	public void setNick(String nick){
		this.nickname = nick;
	}
	
	public String getNick(){
		return nickname;
	}
	
	public int getResponseCode(){
		return responseCode;
	}
	public Object getError(){
		return param;
	}
}