package othello;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import userDAO.UserVO;

public class WaitRoomCreate extends JFrame implements ActionListener {

	JButton jbtnCreate, jbtnCancel;
	JLabel  jlbRT, jlbPW, jlbbackground;
	JTextField jtfRN, jtfPW;
	JPanel jpback ;
	LoginTemp lt;
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Socket s;
	
	Font f1 = new Font("consolas",Font.BOLD,15);
	
	public WaitRoomCreate(LoginTemp lt, ObjectOutputStream oos, ObjectInputStream ois){
		//버튼 140/50
		
		this.lt = lt;
		this.oos = oos;
		this.ois = ois;
		setLayout(null); 
		
		// 방생성 창 배경이미지
		jlbbackground = new JLabel(new ImageIcon("src/images/createRmback.jpeg"));
		jlbbackground.setBounds(0, 0, 480, 350);

		jbtnCreate = new JButton(new ImageIcon("src/images/btnback.png"));
		jbtnCancel = new JButton(new ImageIcon(""));
		jbtnCreate.setBounds(50, 230, 120, 60);
		jbtnCancel.setBounds(220, 230, 120, 60);
		
		jtfRN = new JTextField();
		jtfPW = new JTextField();
		jlbRT = new JLabel("Room Title");
		jlbPW = new JLabel("Room Password");
		jlbRT.setFont(f1);
		jlbPW.setFont(f1);
		
		
		jlbRT.setBounds(40,100,150,25);
		jlbPW.setBounds(40,150,150,25);
		jtfRN.setBounds(220, 100, 150, 25);
		jtfPW.setBounds(220, 145, 150, 25);
		
		add(jlbRT);
		add(jlbPW);
		add(jtfRN);
		add(jtfPW);
		add(jbtnCreate);
		add(jbtnCancel);
		add(jlbbackground);
		
		jbtnCreate.addActionListener(this);
		jbtnCancel.addActionListener(this);
		
		
		setBounds(500, 100, 408, 385);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		GameRoomVO grvo = new GameRoomVO();
		if(obj == jbtnCreate) {
			try {
				System.out.println(lt.getId() + lt.getS());
				grvo.setGameTitle(jtfRN.getText());
				grvo.setRmPW(jtfPW.getText());
				oos.writeObject(grvo);
				oos.writeObject(lt);
				oos.flush();
				oos.reset();
				GamePartyRm gpr = new GamePartyRm(grvo, oos, ois);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}//CLASS 