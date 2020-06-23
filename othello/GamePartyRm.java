package othello;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import userDAO.UserVO;

public class GamePartyRm extends JFrame implements ActionListener, KeyListener, Runnable {

	JPanel jpUser1, jpUser2, jpTextArea, jpTextField;
	JTextArea jta;
	JTextField jtf;
	JButton jbtnSend, jbtnReady, jbtnStart, jbtnExit;
	JLabel jlvs, jlUser1, jlUser2, jlbackground1, jlUser1Text, jlUser2Text;
	JScrollPane jsp;
	ImageIcon imgUser1, imgUser2, background1, imgUserText, imgStartButton, imgExitButton;
	
	GameRoomVO grvo;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Object objInitial;

	GamePartyRm(GameRoomVO grvo, ObjectOutputStream oos, ObjectInputStream ois) {
		
		setTitle("GamePanel");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 50, 1400, 800);
		
		setLayout(null);
		
		this.grvo = grvo;
		this.oos = oos;
		this.ois = ois;

		imgUser1 = new ImageIcon("src/images/userwhite.png");
		imgUser2 = new ImageIcon("src/images/userblack.png");
		background1 = new ImageIcon("src/images/background.jpg");
		imgUserText = new ImageIcon("src/images/jlUserText.jpg");
		
		
		jpUser1 = new JPanel();
		jpUser2 = new JPanel();
		jpTextArea = new JPanel();
		jpTextField = new JPanel();
		jta = new JTextArea();
		jtf = new JTextField(30);
		jbtnSend = new JButton("SEND");
		jbtnReady = new JButton("READY");
		jbtnStart = new JButton("START");
		jbtnExit = new JButton("EXIT");
		jlvs = new JLabel("VS");
		jlUser1 = new JLabel(imgUser1);
		jlUser2 = new JLabel(imgUser2);
		jlbackground1 = new JLabel(background1);
		jlUser1Text = new JLabel(imgUserText);
		jlUser2Text = new JLabel(imgUserText);
		
		
		
		jta.setEditable(false);
		jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		

		jpUser1.add(jlUser1Text);
		jpUser2.add(jlUser1Text);
		
		jlUser1.setBounds(100, 100, 300, 300);
		jlUser2.setBounds(500, 100, 300, 300);
		jlUser1Text.setBounds(100,400,300,150);
		jlUser2Text.setBounds(500,400,300,150);
		jlbackground1.setSize(1400,800);

		jpTextField.setBackground(Color.gray);
		jpTextField.add(jtf);
		jpTextField.add(jbtnSend);
		jta.setBackground(Color.WHITE);

		jpUser1.setBounds(100, 50, 300, 500);
		jpUser1.setBackground(Color.BLACK);
		jpUser2.setBounds(500, 50, 300, 500);
		jpUser2.setBackground(Color.white);
		jlvs.setBounds(440, 250, 50, 50);
		Font f = new Font("맑은고딕", Font.BOLD, 20);
		jlvs.setFont(f);

		jsp.setBounds(900, 50, 430, 450);
		jpTextField.setBounds(900, 500, 430, 50);

		add(jlUser1Text);
		add(jlUser2Text);
		add(jlUser1);
		add(jlUser2);
		add(jpUser1);
		add(jpUser2);
		add(jsp);
		add(jpTextField);
		add(jlvs);
		


		jbtnStart.setBounds(900, 600, 200, 100);
		jbtnExit.setBounds(1130, 600, 200, 100);
		add(jbtnStart);
		add(jbtnExit);
		add(jlbackground1);

		jbtnStart.addActionListener(this);
		jbtnExit.addActionListener(this);
		jbtnSend.addActionListener(this);
		jtf.addKeyListener(this);

		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbtnStart) {
			GamePanel gp = new GamePanel();
		} else if (e.getSource() == jbtnExit) {
			setVisible(false);
		} else if (e.getSource() == jbtnSend) {
			send();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			send();
		}
	}

	private void send() {
		try {
			boolean roomFull = false;
			if(grvo.getCap().length == 2) {
				roomFull = true;
				oos.writeObject(roomFull);
				oos.flush();
				oos.reset();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void run() {
		try {
			objInitial = ois.readObject();
			int[] cap = new int[2];
			if(objInitial instanceof UserVO) {
				UserVO uvo = (UserVO) objInitial;
				jlUser1.setText(uvo.getUserID()); //jluser1text가 userimage label아래에 있는 거... profile들어갈 곳..
				grvo.setCap(cap);
			}
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		GamePartyRm gpr = new GamePartyRm(null, null, null);
	}

}
