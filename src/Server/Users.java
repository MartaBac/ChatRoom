package Server;


public class Users {

	    private String id;
	    private boolean sender;
	    private boolean receiver;
	    private boolean active;

	    public Users (String id, boolean s, boolean r){
	        this.setId(id);
	        sender = s;
	        receiver =r;
	        if(s&&r==true)
	        	active = true;
	        else
	        	active = false;

	    }
	    
	    public Users (String id){
	    	this.setId(id);
	    	sender = false;
	    	receiver = false;
	    }
	    
	    /*
	     *  If boolean is true -> sender; if false -> receiver
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
	    
	    public boolean isActive(){
	    	if(receiver||sender == true)
	    		return true;
	    	else
	    		return false;
	    }
	    public boolean isFullActive(){
	    	if(receiver&&sender == true)
	    		return true;
	    	else
	    		return false;
	    }
	    
	    public boolean getActive(){
	    	return active;
	    }

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
