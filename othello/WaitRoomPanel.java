package othello;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import userDAO.UserVO;

public class WaitRoomPanel extends JPanel implements ActionListener, KeyListener, Runnable {

	JScrollPane jspRoomList, jspFriendList, jspPlayList, jspGlobalChat;
	JButton jbtnquick, jbtncreate, jbtnExit, jbtnGloSend;
	JLabel lbBg1, lbBg2, lbRmList, lbFrList, lbPlList, lbGlList, lbLogo, jlUserProfile1, jlUserProfile2, jlUserProfile3,
			jlUserProfile4, jpleftback;
	Font f1 = new Font("consolas", Font.BOLD, 20);
	JPanel jpGloPanel1, jpGloPanel2;
	JTextField jtfGloChat;
	JTextArea jtaGloChat, jtaRoomList;
	LoginTemp lt;
	Object objInitial;
	String ip;

	// server client IO
	ObjectOutputStream oos, oos2;
	ObjectInputStream ois, ois2;
	ChatLine cl;

	// gameStart var
	GameStatus gst;
	
	//userProfile var
	UserVO uvo;

	// connect to gameServer socket
	Socket s;
	int port = 6000;

	public WaitRoomPanel(ObjectOutputStream oos, ObjectInputStream ois, LoginTemp lt, String ip) {

		setLayout(null);
		this.oos = oos;
		this.ois = ois;
		this.lt = lt;
		this.ip = ip;
		// 화면 가운데 이미지 두개 라벨에 담고 setBounds 함
		lbBg1 = new JLabel(new ImageIcon("src/images/background.jpeg"));
		lbBg1.setBounds(0, 0, 1550, 600);
		lbBg2 = new JLabel(new ImageIcon("src/images/play.jpeg"));
		lbBg2.setBounds(0, 0, 1550, 1200);

		// List(JScrollpane 객체)에 각각의 이미지를 넣기 위해서 라벨에 이미지 넣기
		lbRmList = new JLabel(new ImageIcon(""));
		lbFrList = new JLabel(new ImageIcon(""));
		lbPlList = new JLabel(new ImageIcon(""));
		lbGlList = new JLabel(new ImageIcon(""));
		lbLogo = new JLabel(new ImageIcon("src/images/logo2.jpeg"));

//		임시로 디비에서 오는 정보 끔 절대 지우면 안됨 
//		uvo = udao.selectOne(WinLogin.lt.getId(), WinLogin.lt.getPassword());

		// Global Chatting setup
		jpGloPanel1 = new JPanel(new BorderLayout()); // jscrollpane 위 jtextarea 생
		jpGloPanel1.setBounds(1000, 150, 500, 500);

		jpGloPanel2 = new JPanel(); // Send button, textfield 붙일 panel
		jtfGloChat = new JTextField(30);
		jbtnGloSend = new JButton("send");
		jtaGloChat = new JTextArea();
		jtaRoomList = new JTextArea();

		jpGloPanel2.add(jtfGloChat); // add button, textfield to panel2
		jpGloPanel2.add(jbtnGloSend);

		jspGlobalChat = new JScrollPane(jtaGloChat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jtaGloChat.setLineWrap(true); // [성재] 채팅이 긴 경우 jta 영역 자동줄바꿈

		jpGloPanel1.add(jspGlobalChat, "Center");
		jpGloPanel1.add(jpGloPanel2, "South");

		// 오른쪽 상 위에 사용자의 정보를 표시하기 위해 label에 정보를 붙여 넣음
		jlUserProfile1 = new JLabel();

		jlUserProfile2 = new JLabel();

		jlUserProfile3 = new JLabel();

		jlUserProfile4 = new JLabel();

		jlUserProfile1.setFont(f1);
		jlUserProfile2.setFont(f1);
		jlUserProfile3.setFont(f1);
		jlUserProfile4.setFont(f1);

		// Roomlist, FriendList, Playlist를 하나의 페널에 붙여 넣음

		JPanel jpleft = new JPanel();
		jpleft.setLayout(null);
		jpleft.setBounds(60, 180, 500, 600);

		// background 이미지 붙
		jpleftback = new JLabel(new ImageIcon("src/images/test.gif"));
		jpleftback.setBounds(0, 0, 500, 600);

		jspRoomList = new JScrollPane(lbRmList);
		jspFriendList = new JScrollPane(lbFrList);
		jspPlayList = new JScrollPane(lbPlList);

		jpleft.add(jspRoomList);
		jpleft.add(jspFriendList);
		jpleft.add(jspPlayList);
		jpleft.add(jpleftback); // 먼저 보이고 싶은 이미지의 코드를 앞에 써야한다.

		// 왼쪽 상단에 logo 및 Jscrollpane list들 setbounds 시
		lbLogo.setBounds(30, 30, 500, 120);
		jspRoomList.setBounds(50, 40, 400, 150);
		jspFriendList.setBounds(50, 230, 400, 150);
		jspPlayList.setBounds(50, 420, 400, 150);
		jlUserProfile1.setBounds(1060, 25, 200, 110);
		jlUserProfile2.setBounds(1060, 55, 250, 110);
		jlUserProfile3.setBounds(1300, 25, 200, 110);
		jlUserProfile4.setBounds(1300, 55, 200, 110);

		// 오른쪽 하단 button 이미지 생성 및 위치 조정

		jbtnquick = new JButton(new ImageIcon("src/images/quickstart.png"));
		jbtncreate = new JButton(new ImageIcon("src/images/createroom.jpeg"));
		jbtnExit = new JButton(new ImageIcon("src/images/waitexit.jpeg"));

		jbtnquick.setBounds(960, 660, 150, 150);
		jbtncreate.setBounds(1185, 660, 150, 150);
		jbtnExit.setBounds(1385, 660, 150, 150);

		jspRoomList.add(jtaRoomList);
		add(lbLogo);
		add(jpleft);
		add(jpGloPanel1);
		add(jbtnquick);
		add(jbtncreate);
		add(jbtnExit);
		add(jlUserProfile1);
		add(jlUserProfile2);
		add(jlUserProfile3);
		add(jlUserProfile4);
		add(lbBg1);
		add(lbBg2);

		// 각각의 우측 하단 버튼 Actionlistener 생성

		jbtncreate.addActionListener(this);
		jbtnExit.addActionListener(this);
		jbtnquick.addActionListener(this);

		// Global Chatting send-button keylistener
		jtfGloChat.addKeyListener(this);
		jbtnGloSend.addActionListener(this); // [성재] send 버튼 listener

		this.setBackground(Color.white);
		this.setVisible(true);
		cl = new ChatLine();
		Thread th = new Thread(this);
		th.start();

	}// CONSTRUCTOR END

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == jbtncreate) {
			WaitRoomCreate wrc = new WaitRoomCreate(lt, oos, ois);
		} else if (obj == jbtnquick) {
			
//			UserVO uvo = new UserVO();
//			uvo.setUserID("flame91");
//			uvo.setWin(3);
//			uvo.setLoss(1);
//			uvo.setRating(1231);
			gst = new GameStatus();
			try {
				oos.writeObject(9);
				oos.flush();
				oos.reset();
				System.out.println("gst sent" + uvo.getRating() + uvo.getUserID());
				
				//Enter GAME
				connectGameServer();
				gst.setUvo(uvo);
//				System.out.println(uvo.getUserID() + "WRP) SENDING GST WITH UVO SET");
				System.out.println(gst.getGameNumber() + "1111111111111111111");
				GamePanel gp = new GamePanel(uvo, ois2, oos2, lt, ip);
				oos2.writeObject(gst);
				oos2.flush();
				oos2.reset();
				System.out.println("quick button pressed");
				setVisible(false);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// oos.writeChar('q');
			// oos.flush();
			// oos.reset();

			// 빈 방있는지 리스트에서 체크 받아서

		} else if (obj == jbtnExit) {
			System.exit(0);
		}else if (obj == jbtnGloSend) {  // [성재] Send버튼 클릭시 상대방에게 채팅 보내기
			sendMsg();
		}

	}

	public void connectGameServer() {
		try {
			System.out.println("connecting to GameServer");
			// instantiating s with Socket ip and port of server
			s = new Socket(ip, port);
			lt.setS(s); // lt에 client socket 셋
			System.out.println("connected to GameServer lt.getS() = " + lt.getS());

			ois2 = new ObjectInputStream(s.getInputStream());

			// outputstream에 object 보내기-> 서버로
			oos2 = new ObjectOutputStream(s.getOutputStream());

		} catch (UnknownHostException e) {
			System.out.println("Unknown HOST - WRONG IP or PORT or SERVER DOWN");
		} catch (IOException e) {
			System.out.println("Unknown network error");
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			sendMsg();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// Global Chatting send method
	private void sendMsg() {

		try {
			System.out.println("chatline cl created");
			cl.setMsg(jtfGloChat.getText());
			cl.setUserID(lt.getId());
			System.out.println(cl.getUserID());

			System.out.println("wrinting ready.....");
			oos.writeObject(cl);
			oos.reset();
			jtfGloChat.setText("");
			System.out.println("wrote string");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			String data = "";
			System.out.println("readobject false false falseeeeeeeeeeeeeeeeeee");
			boolean completeReadObj = false;
			while (!completeReadObj) {
				objInitial = ois.readObject();
				System.out.println("recieved object");
				if (objInitial instanceof String) {
					System.out.println("inside of instanceof String");
					data = (String) objInitial;
					jtaGloChat.append(data + "\n");
					System.out.println("chatLine object read successful");
					JScrollBar sb = jspGlobalChat.getVerticalScrollBar();
					int position = sb.getMaximum();
					sb.setValue(position);
				} else if (objInitial instanceof GameRoomVO) {
					GameRoomVO grvo = (GameRoomVO) objInitial;
					UserVO[] uvo = grvo.getUvolist();
					String userid = uvo[0].getUserID();
					int win = uvo[0].getWin();
					int loss = uvo[0].getLoss();
					int rating = uvo[0].getRating();

					jlUserProfile1.setText("UserID : " + userid);
					jlUserProfile2.setText(win + " W / " + loss + " L");
					jlUserProfile3.setText("Rating : " + rating);

					if (rating < 800) {// bronze
						jlUserProfile4.setText("Tier : BRONZE");
					} else if (rating < 1200) {// silver
						jlUserProfile4.setText("Tier : SILVER");
					} else if (rating < 1400) {// gold
						jlUserProfile4.setText("Tier : GOLD");
					} else if (rating < 1600) {// platinum
						jlUserProfile4.setText("Tier : PLATINUM");
					} else if (rating < 2000) {// diamond
						jlUserProfile4.setText("Tier : DIAMOND");
					}

				} else if (objInitial instanceof Integer) {
					System.out.println("ingame instance started");
					int gameStartFlag = (int) objInitial;

					// close oos and ois
					oos.close();
					ois.close();

					completeReadObj = true;
//					break;
				}else if (objInitial instanceof UserVO) {
					uvo = (UserVO) objInitial;
					System.out.println(uvo.getUserID() + "CLIENT) SERVER로부터 가져옴");
					
				
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}// CLASS END
