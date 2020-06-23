package othello;

import java.io.Serializable;

import userDAO.UserVO;

// ���� Ŭ�� �ְ�ް� �ϴµ� ��Ŷ�� ����(obj - ObjectStream)

// getter, setter
public class GameStatus implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int flag=1; // 1=������, 2=�� ����
	int board[][] = {{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,2,1,0,0,0},
			{0,0,0,1,2,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0},
			
	}; // ����(0 : ����, 1 : ������, 2 : ��)
	int cntAvail; // player 1,2�� �� ����
	int myturn = 0;
	int gameNumber = 0;
	UserVO uvo;
	String userID;
	int roomNumber;
	int winner = 0; // 1: ���� �̱�, 2: ���� �̱�
	
	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public UserVO getUvo() {
		return uvo;
	}

	public void setUvo(UserVO uvo) {
		this.uvo = uvo;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getGameNumber() {
		return gameNumber;
	}

	public void setGameNumber(int gameNumber) {
		this.gameNumber = gameNumber;
	}

	public int getMyturn() {
		return myturn;
	}

	public void setMyturn(int myturn) {
		this.myturn = myturn;
	}

	public GameStatus() {
	}
	
	public GameStatus(int flag, int[][] board, int cntAvail) {
		this.flag = flag;
		this.board = board;
		this.cntAvail = cntAvail;
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int[][] getBoard() {
		return board;
	}

	public void setBoard(int[][] board) {
		this.board = board;
	}

	
	public int getCntAvail() {
		return cntAvail;
	}
	
	public void setCntAvail(int cntAvail) {
		this.cntAvail = cntAvail;
	}

	
	public int getWinner() {
		return winner;
	}

	public void setWinner(int winner) {
		this.winner = winner;
	}

}
