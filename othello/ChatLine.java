package othello;

import java.io.Serializable;

public class ChatLine implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 637287650227774144L;
	private String msg;
	private String userID;
	
	
	
	public ChatLine() {
		super();
	}

	public ChatLine(String msg, String userID) {
		super();
		this.msg = msg;
		this.userID = userID;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	
	
	
}
