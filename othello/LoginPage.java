package othello;

import java.awt.Container;

import javax.swing.JFrame;

public class LoginPage extends JFrame {

	Container content;
	LoginPanel imgP;
	String ip="192.168.0.17";
	
	public LoginPage() {
		
		super();
		imgP = new LoginPanel(ip);
		content = getContentPane(); //
		content.add(imgP);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		setSize(1600, 900);
		setVisible(true);
		
	}

	public static void main(String[] args) {
		LoginPage lp = new LoginPage();
	}

}// CLASS