package othello;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import userDAO.UserVO;

public class InGamePlayerLlist {
	private UserVO[] inGameRoomParty = new UserVO[2];
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ArrayList<Socket> playerSocket = new ArrayList<Socket>();
	
	public InGamePlayerLlist() {
		super();
	}
	
	
	
	public InGamePlayerLlist(ObjectOutputStream oos, ObjectInputStream ois, ArrayList<Socket> playerSocket) {
		super();
		this.oos = oos;
		this.ois = ois;
		this.playerSocket = playerSocket;
	}

	

	public InGamePlayerLlist(UserVO[] inGameRoomParty) {
		super();
		this.inGameRoomParty = inGameRoomParty;
	}
	
	
	public ArrayList<Socket> getPlayerSocket() {
		return playerSocket;
	}



	public void setPlayerSocket(ArrayList<Socket> playerSocket) {
		this.playerSocket = playerSocket;
	}



	public ObjectOutputStream getOos() {
		return oos;
	}



	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}



	public ObjectInputStream getOis() {
		return ois;
	}



	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}



	public UserVO[] getInGameRoomParty() {
		return inGameRoomParty;
	}
	public void setInGameRoomParty(UserVO[] inGameRoomParty) {
		this.inGameRoomParty = inGameRoomParty;
	}
	
	
}
