package othello;

import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import userDAO.UserVO;

public class RegisterPage extends JDialog implements ActionListener {

	JButton jbtnRegister, jbtnBack;
	JTextField jtfID, jtfPW, jtfLName, jtfFName, jtfEmail;
	JLabel lbId, lbPw, lbLn, lbFn, lbEmail, lbBack;

	// server 연결필요 변수 리스트
	static String ip;
	int port = 5000;
	Socket s;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	LoginTemp lt = new LoginTemp();

	RegisterPage(String ip) {
		this.ip=ip;
		
	    setTitle("Register Page");
	    setBounds(100,100,420,580);
	    setLayout(null);
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);


	    jbtnRegister = new JButton(new ImageIcon("src/images/btnregister.jpg"));
	    jbtnBack = new JButton(new ImageIcon("src/images/btnreback.jpg"));

	    Font f = new Font("맑은고딕", Font.BOLD, 15);
//	    jbtnRegister.setFont(f);
//	    jbtnBack.setFont(f);

	    jbtnRegister.setBounds(50, 400, 130, 50);
	    jbtnBack.setBounds(225, 400, 120, 50);

	    jbtnRegister.addActionListener(this);
	    jbtnBack.addActionListener(this);


	    lbId = new JLabel("ID");
	    lbPw = new JLabel("Password");
	    lbLn = new JLabel("Last Name");
	    lbFn = new JLabel("First Name");
	    lbEmail = new JLabel("Email");
	    lbBack = new JLabel(new ImageIcon ("src/images/registerback.jpg"));
	    
	    lbBack.add(jbtnRegister);
	    lbBack.add(jbtnBack);
	   
	    lbId.setFont(f);
	    lbPw.setFont(f);
	    lbLn.setFont(f);
	    lbFn.setFont(f);
	    lbEmail.setFont(f);
	    
	    lbId.setForeground(Color.white);
	    lbPw.setForeground(Color.white);
	    lbLn.setForeground(Color.white);
	    lbFn.setForeground(Color.white);
	    lbEmail.setForeground(Color.white);
	    
	    lbId.setBounds(75, 100, 50, 50);
	    lbPw.setBounds(50, 150, 75, 50);
	    lbLn.setBounds(50, 200, 75, 50);
	    lbFn.setBounds(50, 250, 100, 50);
	    lbEmail.setBounds(60, 300, 75, 50);
	   
	    lbBack.add(lbId);
	    lbBack.add(lbPw);
	    lbBack.add(lbLn);
	    lbBack.add(lbFn);
	    lbBack.add(lbEmail);
	    
	    jtfID = new JTextField(20);
	    jtfPW = new JTextField();
	    jtfLName = new JTextField(20);
	    jtfFName = new JTextField(20);
	    jtfEmail = new JTextField(20);

	    jtfID.setBounds(150, 115, 200, 25);
	    jtfPW.setBounds(150, 165, 200, 25);	
	    jtfLName.setBounds(150, 215, 200, 25);
	    jtfFName.setBounds(150, 265, 200, 25);
	    jtfEmail.setBounds(150, 315, 200, 25);

	    lbBack.add(jtfID);
	    lbBack.add(jtfPW);
	    lbBack.add(jtfLName);
	    lbBack.add(jtfFName);
	    lbBack.add(jtfEmail);
	
	    add(lbBack);
	    setContentPane(lbBack);

	    setVisible(true);

	}

	public static void main(String[] args) {
		RegisterPage rp = new RegisterPage(ip);

	}// main end

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		LoginTemp lt;

		if (obj == jbtnBack) {
			setVisible(false);

		} else if (obj == jbtnRegister) {
			UserVO uvo = new UserVO();
			UserVO voR = new UserVO();

			if (!jtfID.getText().isEmpty()) {

				// uvo 객체에 필요정보 set하기
				uvo.setUserID(jtfID.getText());
				uvo.setUserPW(jtfPW.getText());
				uvo.setLastName(jtfLName.getText());
				uvo.setFirstName(jtfFName.getText());
				uvo.setuemail(jtfEmail.getText());

				try {
					connectServer();

					// uvo객체 서버에 던저주기
					oos.writeObject(uvo);
					oos.flush();
					oos.reset();

					// 수신 받아오기... register가 성공적이였는지
					voR = (UserVO) ois.readObject();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (voR.getUserID().equals(uvo.getUserID()) && voR.getUserPW().equals(uvo.getUserPW())) {
					JOptionPane.showMessageDialog(this, "Register Successful!");
					jtfID.setText("");
					jtfPW.setText("");
					jtfLName.setText("");
					jtfFName.setText("");
					jtfEmail.setText("");
				} else {
					JOptionPane.showMessageDialog(this, "Register failed!");
				}
			} else if (jtfID.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "failed");
			}
		}
	}

	public void connectServer() {
		lt = new LoginTemp();
		// connection to server
		if (s == null) {
			try {
				System.out.println("connecting to server");
				// instantiating s with Socket ip and port of server
				s = new Socket(ip, port);
				lt.setS(s); // lt에 client socket 셋
				System.out.println("connected to server" + lt.getS().getLocalSocketAddress());

				ois = new ObjectInputStream(s.getInputStream());

				// outputstream에 object 보내기-> 서버로
				oos = new ObjectOutputStream(s.getOutputStream());

			} catch (UnknownHostException e) {
				System.out.println("Unknown HOST - WRONG IP or PORT or SERVER DOWN");
			} catch (IOException e) {
				System.out.println("Unknown network error");
			}
		}

	}

}// class end
