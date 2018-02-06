package Server;

import java.io.Serializable;

/*
 *  Class that defines the parameters of the Users, such as their nickname, in which
 * modes they are logged in and if they can join the chat. 
 */

public class Users   {
	
	    private String id;
	    private boolean sender;
	    private boolean receiver;
	    
	    // Active will be 'true' if the user is logged in both sender and receiver mode
	    private boolean active;

	    /*
	     * Constructor that will set all the fields.
	     * id = nickname, the first boolean represents sender status, the second one
	     * the receiver one.
	     */
	    public Users (String id, boolean s, boolean r){
	        this.setId(id);
	        sender = s;
	        receiver =r;
	        if(s&&r==true)
	        	active = true;
	        else
	        	active = false;
	    }
	    
	    /*
	     *  Constructor that instantiate the nickname. The booleans are set to false.
	     */
	    public Users (String id){
	    	this.setId(id);
	    	sender = false;
	    	receiver = false;
	    }
	    
	    /*
	     * 	Initialize 'Users' id(String) to the first parameter, and set sender or 
	     * receiver to true, logged.
	     * If boolean is true -> sender; if false -> receiver.
	     */
	    public Users (String id, boolean x){
	    	this.setId(id);
	    	if(x==false){
	    		sender = true;
	    		receiver = false;
	    	}
	    	else{
	    		receiver = true;
	    		sender = false;
	    	}
	    	active = false;
	    }
	    
	    public void setSender(boolean s){
	    	sender = s;
	    }
	    
		public void setId(String id) {
			this.id = id;
		}
	    
	    public void setReceiver(boolean r){
	    	receiver = r;
	    }
	    
	    public void setActive(boolean a){
	    	active = a;
	    }
	    
	    public boolean getSender(){
	    	return sender;
	    }
	    
	    public boolean getReceiver(){
	    	return receiver;
	    }
	    
	    /*
	     * Returns true if the user is logged in at least one mode.
	     */
	    public boolean isOn(){
	    	if(receiver||sender == true)
	    		return true;
	    	else
	    		return false;
	    }
	    
	    /*
	     * Returns true when 'user' is logged in both modes (=ready to join the chat).
	     */
	    public boolean isActive(){
	    	if(receiver&&sender == true){
	    		this.active=true;
	    		return true;
	    	}
	    	else{
	    		this.active=false;
	    		return false;
	    	}
	    }
	    
	    public boolean getActive(){
	    	return active;
	    }

		public String getId() {
			return id;
		}
		
	}
