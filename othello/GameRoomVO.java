package othello;

import java.io.Serializable;

import userDAO.UserVO;

public class GameRoomVO implements Serializable{
	private String gameTitle;
	private String rmPW;
	private int[] cap;
	private UserVO[] uvolist = new UserVO[2];
	
	public GameRoomVO() {
		super();
	}



	public GameRoomVO(String gameTitle, String rmPW, int[] cap, UserVO[] uvolist) {
		super();
		this.gameTitle = gameTitle;
		this.rmPW = rmPW;
		this.cap = cap;
		this.uvolist = uvolist;
	}



	public String getGameTitle() {
		return gameTitle;
	}



	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}



	public String getRmPW() {
		return rmPW;
	}



	public void setRmPW(String rmPW) {
		this.rmPW = rmPW;
	}



	public int[] getCap() {
		return cap;
	}



	public void setCap(int[] cap) {
		this.cap = cap;
	}



	public UserVO[] getUvolist() {
		return uvolist;
	}



	public int setUvolist(UserVO uvo) {
		for (int i = 0; i < uvolist.length; i++) {
			if(uvolist[i] == null) {
				this.uvolist[i] = uvo;
			}
		}
		return -1;
	}
	
	
	
}
