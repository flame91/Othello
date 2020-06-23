package userDAO;

import java.io.Serializable;

public class UserVO implements Serializable{
	String userID, userPW, lastName, firstName, uemail;
	int win = 0;
	int loss = 0;
	float wlRatio = 0.00f;
	int rating = 1000;
	
	//기본 생성자
	public UserVO() {
	}

	//필드변수를 매개변수로 가진 생성자
	public UserVO(String userID, String userPW, String lastName, String firstName, String uemail, int win, int loss, float wlRatio, int rating) {
		
		super();
		this.userID = userID;
		this.userPW = userPW;
		this.lastName = lastName;
		this.firstName = firstName;
		this.uemail = uemail;
		this.win = win;
		this.loss = loss;
		this.wlRatio = wlRatio;
		this.rating = rating;
	}
	
	
	

	public UserVO(String userID, String userPW, String lastName, String firstName, String uemail) {
		super();
		this.userID = userID;
		this.userPW = userPW;
		this.lastName = lastName;
		this.firstName = firstName;
		this.uemail = uemail;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserPW() {
		return userPW;
	}

	public void setUserPW(String userPW) {
		this.userPW = userPW;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getuemail() {
		return uemail;
	}

	public void setuemail(String uemail) {
		this.uemail = uemail;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLoss() {
		return loss;
	}

	public void setLoss(int loss) {
		this.loss = loss;
	}

	public float getWlRatio() {
		return wlRatio;
	}

	public void setWlRatio(float wlRatio) {
		this.wlRatio = wlRatio;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	
	
}
