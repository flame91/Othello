package othello;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class WaitRoomFrame extends JFrame {
	
	Container content;
	WaitRoomPanel wrp;
	public BufferedImage backimg;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	LoginTemp lt;
	String ip;
	
	WaitRoomFrame(ObjectOutputStream oos, ObjectInputStream ois, LoginTemp lt, String ip){
		
		this.oos = oos;
		this.ois = ois;
		this.lt = lt;
		this.ip = ip;
		
		wrp = new WaitRoomPanel(oos, ois, lt, ip);
		content = getContentPane(); // 
		content.add(wrp);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1600,900);
		setVisible(true);
		
	}//CONSTRUCTOR END

// public static void main(String[] args) {
//	
//	 WaitRoomFrame wrf = new WaitRoomFrame(oos, ois);
//}
	
}//CLASS END


/*
 * roomlist 
 * 
 * 1. DB 연결
 * for 문 버튼도 배열 
 * 
 */
