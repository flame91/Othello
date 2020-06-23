package othello;


import java.io.Serializable;
import java.net.Socket;

public class LoginTemp implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1598809608291513747L;
	String id, password;
	boolean givePermission;
	transient Socket s;
	public LoginTemp() {
		
	}
	
	public LoginTemp(String id, String password, boolean givePermission,
			Socket s) {
		super();
		this.id = id;
		this.password = password;
		this.givePermission = givePermission;
		this.s = s;
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isGivePermission() {
		return givePermission;
	}

	public void setGivePermission(boolean givePermission) {
		this.givePermission = givePermission;
	}

}
