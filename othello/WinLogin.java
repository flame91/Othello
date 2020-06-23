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
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WinLogin extends JDialog implements ActionListener, KeyListener {
	JPanel jp = new JPanel();
	JLabel lbID, lbPW, lbBackground;
	JTextField jtfID, jtfPW;
	JButton jbtnBack, jbtnPlay;
	Font f1 = new Font("consolas", Font.BOLD, 14);
	
	static boolean connected = false;
	LoginTemp lt;
	
	// server connection var list
	Socket s;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	// server connection var list
	String ip;

	int port = 5000;

	// login permission var
	Object objPermission = null;

public WinLogin(String ip) {
		this.ip=ip;
	
		jp = new JPanel();
		jtfID = new JTextField(30);
		jtfPW = new JTextField(30);
		jbtnBack = new JButton(new ImageIcon("src/images/btnexit.png"));
		jbtnPlay = new JButton(new ImageIcon("src/images/btnlogin.jpg"));
		lbID = new JLabel("ID");
		lbPW = new JLabel("PW");
		lbBackground = new JLabel(new ImageIcon("src/images/winlogin.gif"));
		
		jp.setBounds(100,100,480,360);
		lbBackground.setBounds(100, 100, 480, 360);
		
		jbtnPlay.setBounds(110, 230, 100, 50);
		jbtnBack.setBounds(250, 230, 100, 50);

		jtfID.setBounds(180, 100, 180, 25);
		jtfPW.setBounds(180, 150, 180, 25);

		lbID.setBounds(110, 100, 60, 25);
		lbPW.setBounds(110, 150, 60, 25);
		
		lbID.setFont(f1);
		lbPW.setFont(f1);
		lbID.setForeground(Color.white);
		lbPW.setForeground(Color.white);
		
		lbBackground.add(jtfID);
		lbBackground.add(jtfPW);
		lbBackground.add(jbtnBack);
		lbBackground.add(jbtnPlay);
		lbBackground.add(lbID);
		lbBackground.add(lbPW);
		jp.add(lbBackground);

		jbtnBack.addActionListener(this);
		jbtnPlay.addActionListener(this);
		jtfPW.addKeyListener(this);
		setTitle("Login");
		getContentPane().add(jp);

		this.setBounds(550, 200, 350, 360);
		this.setModal(true);
		this.setVisible(true);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		String id =  null;
		boolean allow = false;
		if (obj == jbtnBack) {
			this.setVisible(false);
		} else if (obj == jbtnPlay) {
			
			if (!jtfID.getText().isEmpty() && !jtfPW.getText().isEmpty()) {
				connectServer();
				System.out.println("while문 들어간다" + lt.getId());
				while (allow == false) {
					lt.setId(jtfID.getText());
					System.out.println("jtfID 세터 함." + lt.getId());
					lt.setPassword(jtfPW.getText());
					
					sendData(lt);
					objPermission = receiveData();
					if (objPermission.toString().equals("true")) {
						allow = true;
					}
					id = jtfID.getText();
				}
				if (objPermission.toString().equals("true")) {
					this.setVisible(false);
					WaitRoomFrame wrf = new WaitRoomFrame(oos, ois, lt, ip);
					wrf.setVisible(true);
				}

			}
		}

	}

	private void sendData(LoginTemp lt) {
		try {
			System.out.println("sending..." + lt);
			oos.writeObject(lt);
//			oos.writeObject(lt.getPassword());
			oos.flush();
			oos.reset();
//			oos.writeObject(lt.getS());
			System.out.println(lt.getId() + lt.getPassword());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Object receiveData() {
		Object permData = null;
		try {
			System.out.println("permData receiving");
			permData = ois.readObject();
			System.out.println("permData received");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return permData;
	}

	public Socket connectServer() {
		lt = new LoginTemp();
		// connection to server
		if (s == null) {
			try {
				System.out.println("connecting to server");
				// instantiating s with Socket ip and port of server
				s = new Socket(ip, port);
				lt.setS(s); // lt에 client socket 셋
				System.out.println("connected to server lt.getS() = " + lt.getS());
				
				ois = new ObjectInputStream(s.getInputStream());

				// outputstream에 object 보내기-> 서버로
				oos = new ObjectOutputStream(s.getOutputStream());

			} catch (UnknownHostException e) {
				System.out.println("Unknown HOST - WRONG IP or PORT or SERVER DOWN");
			} catch (IOException e) {
				System.out.println("Unknown network error");
			}
		}
		return s;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		String id =  null;
		boolean allow = false;
		if (key == KeyEvent.VK_ENTER) {
			if (!jtfID.getText().isEmpty() && !jtfPW.getText().isEmpty()) {
				connectServer();
				System.out.println("while문 들어간다" + lt.getId());
				while (allow == false) {
					lt.setId(jtfID.getText());
					System.out.println("jtfID 세터 함." + lt.getId());
					lt.setPassword(jtfPW.getText());
					
					sendData(lt);
					objPermission = receiveData();
					if (objPermission.toString().equals("true")) {
						allow = true;
					}
					id = jtfID.getText();
				}
				if (objPermission.toString().equals("true")) {
					this.setVisible(false);
					WaitRoomFrame wrf = new WaitRoomFrame(oos, ois, lt, ip);
					wrf.setVisible(true);
				}

			}
		}
		}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
