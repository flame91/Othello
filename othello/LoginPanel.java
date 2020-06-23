package othello;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoginPanel extends JPanel implements ActionListener, MouseListener {
	public JLabel background, logo;
	public static JButton jbtnlogin, jbtnRegister, jbtnExit, jbtnCredits;
	public ImageIcon backImage;
	public ImageIcon logImage = new ImageIcon("src/images/main_login_u.png");
	public ImageIcon registerImage = new ImageIcon("src/images/main_register_u.png");
	public ImageIcon creditsImage = new ImageIcon("src/images/main_credits_u.png");
	public ImageIcon exitImage = new ImageIcon("src/images/main_exit_u.png");
	public ImageIcon logoImage;
	String ip;

	public LoginPanel(String ip) {
		this.ip=ip;
		
		setLayout(null);
//		backimage = new ImageIcon("src/images/login.jpeg"); 
//		JLabel backlb = new JLabel();
//		backlb.setBounds(400, 200, 300, 400);
		JPanel jp = new JPanel();
		JLabel jl = new JLabel(new ImageIcon("src/images/logpageback.jpg"));
		jp.setBounds(0, 0, 1600, 900);
		
		
		jbtnlogin = new JButton(logImage); // login
		jbtnRegister = new JButton(registerImage);
		jbtnExit = new JButton(exitImage);
		jbtnCredits = new JButton(creditsImage);
		background = new JLabel(new ImageIcon("src/images/main_bw.png"));
		logo = new JLabel(new ImageIcon("src/images/Logo.png"));

		jbtnlogin.setBounds(90, 300, 220, 110);
		jbtnlogin.setBorderPainted(false); // 외곽선 없애기
		jbtnlogin.setFocusPainted(false); // 버튼 선택시 생기는 테두리
		jbtnlogin.setContentAreaFilled(false); // JBUTTON 내용영역 채우기 않함

		jbtnRegister.setBounds(90, 420, 220, 110);
		jbtnRegister.setBorderPainted(false);
		jbtnRegister.setFocusPainted(false);
		jbtnRegister.setContentAreaFilled(false);

		jbtnCredits.setBounds(400, 300, 220, 110);
		jbtnCredits.setBorderPainted(false);
		jbtnCredits.setFocusPainted(false);
		jbtnCredits.setContentAreaFilled(false);

		jbtnExit.setBounds(400, 420, 220, 110);
		jbtnExit.setBorderPainted(false);
		jbtnExit.setFocusPainted(true);
		jbtnExit.setContentAreaFilled(false);

		background.setBounds(850, 250, 500, 500);
		backImage = new ImageIcon("src/images/main_bw.png");
		int offset5 = background.getInsets().right;

		logo.setBounds(10, 50, 700, 200);
		logoImage = new ImageIcon("src/images/Logo.png");

		jbtnlogin.addActionListener(this);
		jbtnRegister.addActionListener(this);
		jbtnCredits.addActionListener(this);
		jbtnExit.addActionListener(this);

		jbtnlogin.addMouseListener(this);
		jbtnRegister.addMouseListener(this);
		jbtnCredits.addMouseListener(this);
		jbtnExit.addMouseListener(this);

		jl.add(jbtnlogin);
		jl.add(jbtnRegister);
		jl.add(jbtnCredits);
		jl.add(jbtnExit);
		jl.add(background);
		jl.add(logo);
//		jp.setBackground(Color.white);
		jp.add(jl);
		this.add(jp);
		setVisible(true);

	}


//	private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
//		Image img = icon.getImage();
//		Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, java.awt.Image.SCALE_SMOOTH);
//		return new ImageIcon(resizedImage);
//	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == jbtnlogin) {
			WinLogin wl = new WinLogin(ip);
			System.out.println("login");
		} else if (obj == jbtnRegister) {
			RegisterPage rp = new RegisterPage(ip);
		} else if (obj == jbtnCredits) {
			CreditsPage cp = new CreditsPage();
			Thread th = new Thread(cp);
			th.start();
		} else if (obj == jbtnExit) {
			System.out.println("exit");
			System.exit(0);
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Object obj = e.getSource();

		if (obj == jbtnlogin) {
			ImageIcon loginImg = new ImageIcon("src/images/main_login_s.png");
			jbtnlogin.setIcon(loginImg);
		}else if(obj == jbtnRegister) {
			ImageIcon registerImg = new ImageIcon("src/images/main_register_s.png");
			jbtnRegister.setIcon(registerImg);
		}else if(obj == jbtnCredits) {
			ImageIcon creditsImg = new ImageIcon("src/images/main_credits_s.png");
			jbtnCredits.setIcon(creditsImg);
		}else if(obj == jbtnExit) {
			ImageIcon exitImg = new ImageIcon("src/images/main_exit_s.png");
			jbtnExit.setIcon(exitImg);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Object obj = e.getSource();

		if (obj == jbtnlogin) {
			jbtnlogin.setIcon(logImage);
		}else if(obj == jbtnRegister) {
			jbtnRegister.setIcon(registerImage);
		}else if(obj == jbtnCredits) {
			jbtnCredits.setIcon(creditsImage);
		}else if(obj == jbtnExit) {
			jbtnExit.setIcon(exitImage);
		}
	}

}// CLASS
