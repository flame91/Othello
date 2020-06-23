package othello;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import userDAO.UserVO;

public class GamePanel extends JFrame implements ActionListener, MouseListener, Runnable {

	Container con; // �����̳�

	// ---------------- �г�(Panel) ----------------
	JPanel jpGame = new JPanel(); // ���� ���� �г�
	JPanel jpChat = new JPanel(); // ������ ä�� �г�

	// ---------------- ��(Label) ----------------
	JLabel jlblCount[] = new JLabel[2]; // ī��Ʈ
	JLabel jlblBoard = new JLabel(); // ����
	JLabel jlblGrid = new JLabel(); // ����
	JLabel jlblGiveup = new JLabel(); // ���
	JLabel jlblExit = new JLabel(); // ������
	JLabel jlblBg = new JLabel();
	JLabel[][] jlblStone = new JLabel[8][8]; // ��
	JLabel[][] jlblAvail = new JLabel[8][8]; // ��

	// JButton[][] jbtnStone = new JButton[8][8];

	Toolkit tool = Toolkit.getDefaultToolkit();

	// ---------------- �̹���(image) ----------------
	ImageIcon imgBoard = new ImageIcon("src\\images\\board_basic.png"); // ���� �̹���
	ImageIcon imgBlack = new ImageIcon("src\\images\\black_basic.png"); // ������ �̹���
	ImageIcon imgWhite = new ImageIcon("src\\images\\white_basic.png"); // �� �̹���
	ImageIcon imgAvail = new ImageIcon("src\\images\\available.png"); // �� �̹���
	Image imgBg = tool.getImage("src\\images\\bg.gif"); // ��� �̹���

	Image imgGiveup = tool.getImage("src\\images\\giveup.gif"); // ���(�ѿ���) �̹���
	ImageIcon imgGiveupStop = new ImageIcon("src\\images\\giveup_stop.png"); // ���(����) �̹���
	Image imgExit = tool.getImage("src\\images\\exit.gif"); // ������(�ѿ���) �̹���
	ImageIcon imgExitStop = new ImageIcon("src\\images\\exit_stop.png"); // ������(����) �̹���

	ImageIcon imgBronze = new ImageIcon("src\\images\\tier\\bronze.png");
	ImageIcon imgstilver = new ImageIcon("src\\images\\tier\\silver.png");
	ImageIcon imgGold = new ImageIcon("src\\images\\tier\\gold.png");
	ImageIcon imgPlat = new ImageIcon("src\\images\\tier\\platinum.png");
	ImageIcon imgDiamond = new ImageIcon("src\\images\\tier\\diamond.png");

	// ---------------- UI ----------------
	int rightChat = 500; // ä��â ������
	int sizeBoard = 600; // ���� ������
	int sizeBtn = 150; // ��ư ������
	int padBtn = 100 / 2; // ��ư ����
	int sizeStone = 75; // �� ������

	int padLeft = (1600 - sizeBoard - rightChat) / 2; // â ���� ��-���� ���� ����
	int posBoardX = padLeft, posBoardY = 50; // ���� ���� ��ġ(�⺻ 150, 50)

	int cnt[] = new int[2]; // player 1,2�� �� ����

	int flag = 1; // 1=������, 2=�� ����
	int myturn = 0;

	int board[][] = new int[8][8]; // ����(0 : ����, 1 : ������, 2 : ��)
	/*
	 * int[][] board= { {1,1,1,1,1,1,1,1}, {1,1,1,1,1,1,1,1}, {1,2,1,1,1,1,1,1},
	 * {1,1,2,1,1,1,1,1}, {1,1,2,2,1,1,1,1}, {1,1,2,2,1,1,1,1}, {1,1,1,1,1,1,1,0},
	 * {1,1,1,1,1,1,2,0}};
	 */

	int board_avail[][] = new int[8][8]; // ���� ���� �� �ִ� �ڸ� ����
	int cntAvail;
	int cntAvailEnemy;

	// ---------------- �˻� ----------------
	Boolean isEnd = false;
	Boolean isStone = false; // Ŭ���� ���� �������� �˻�
	Boolean isAvail = false; // ���� �� �ִ� �ڸ��� �ľ��ߴ��� �˻�
	Boolean isState1 = true, isState2 = true;

	// ---------------- ��Ʈ ----------------
	Font fName = new Font("���� ���", Font.BOLD, 50); // �̸��� ��Ʈ
	Font fRating = new Font("FixedSys", Font.BOLD, 40); // �����ÿ� ��Ʈ
	Font fCount = new Font("Serif", Font.BOLD, 140); // ī��Ʈ�� ��Ʈ

	// ------------------------------ ���� ���� ------------------------------
	int win[] = new int[2]; // �¼�
	JLabel jlblWin[] = new JLabel[2];

	int loss[] = new int[2]; // �м�
	JLabel jlblLoss[] = new JLabel[2];

	int rating[] = new int[2]; // ������
	JLabel jlblRating[] = new JLabel[2];

	String userID[] = new String[2]; // ID
	JLabel jlbluserID[] = new JLabel[2];

	JLabel jlblTier[] = new JLabel[2]; // Ƽ��

	UserVO uvo, uvoP1, uvoP2;
	GameStatus gst = new GameStatus();
	LoginTemp lt;

	// game room number
	int gameRoomNumber = 0;

	// ------------------------------ ��Ʈ��ũ ------------------------------
	ObjectOutputStream oos2 = null;
	ObjectInputStream ois2 = null;
	Object objInitial;
	String ip;
	// --------------------------------------------------------------------

	GamePanel(UserVO uvo, ObjectInputStream ois2, ObjectOutputStream oos2, LoginTemp lt, String ip) {

		this.uvo = uvo;
		this.ois2 = ois2;
		this.oos2 = oos2;
		this.lt = lt;
		this.ip = ip;
		System.out.println(uvo.getUserID() + "CHECKING FOR UVO.GETID"); //
		System.out.println(lt.getId() + "CHECKING FOR LT.GETID"); //
		
		//PLAYER1 PROFILE SET
		uvoP1 = new UserVO();
		uvoP1.setUserID(uvo.getUserID());
		uvoP1.setRating(uvo.getRating());
		uvoP1.setWin(uvo.getWin());
		uvoP1.setLoss(uvo.getLoss());
//		uvoP1.setWlRatio(uvo.getWin()/uvo.getLoss());
		userID[0] = uvoP1.getUserID();
		rating[0] = uvoP1.getRating();
		win[0] = uvoP1.getWin();
		loss[0] = uvoP1.getLoss();
		
		// Layout : �����̳ʿ� �г� �߰�
		con = getContentPane();
		con.setLayout(new GridLayout(1, 2, (-800 + rightChat) * 2, 0)); // ���� ������ ������ �������� 400px��ŭ ����
		con.add(jpGame); // �����̳ʿ� jpGame �߰�
		con.add(jpChat); // �����̳ʿ� jpChat �߰�

		jpGame.setLayout(null);
		jpChat.setBackground(new Color(0, 0, 0));

		// �÷��̾� ���� �ҷ�����
		isEnd = false;
		getInfo(); // ������� ����(ID, ��, �̸�, ��/��, ������ �ҷ�����)
		initBgm();
		initInfo(); // ���� ���� �ʱ�ȭ
		initTier(); // Ƽ��
		initRating(); // ������
		initCount(); // ī��Ʈ
		initStone(); // �� �ʱ�ȭ
		initAvail(); // �� ���� �� �ִ� �ڸ� �ʱ�ȭ
		initGiveup(); // ��� ������
		initExit(); // ������ ������
		initBoard(); // ����

		// �� ��� 4x4 �ʱ�ȭ
		board[3][3] = 2;
		board[3][4] = 1;
		board[4][3] = 1;
		board[4][4] = 2;

		initBg(); // ���
		drawStone(); // �� �׸���
		checkAvail(); // �� ���� �� �ִ� �ڸ� �˻�
		drawAvail();
		countStone(); // ���� ���� �� ����

		// ���� �ʱ�ȭ(drawLine���� ��ü)
		// jpGame.add(jlblGrid);
		// jlblBoard.setBounds(posBoardX,posBoardY,sizeBoard,sizeBoard);
		// jlblBoard.setIcon(imgGrid);

		initListener(); // ������

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1600, 900); // �ػ� 1600x900
		setVisible(true);

		Thread th = new Thread(this);
		th.start();

	} // EO GamePanel()

	public void initBgm() {
		try {  
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File("src/othello/1.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	public void initListener() { // ������ �ʱ�ȭ
		// ������ ������ �ο�
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				jlblStone[i][j].addMouseListener(this);
			}
		}

		// ���, ������ �󺧿� Listener �ο�
		jlblGiveup.addMouseListener(this);
		jlblExit.addMouseListener(this);
	} // EO initLIstener

	public void initBoard() { // ���� �ʱ�ȭ
		jpGame.add(jlblBoard);
		jlblBoard.setBounds(posBoardX, posBoardY, sizeBoard, sizeBoard); // 150,50 600,600
		jlblBoard.setIcon(imgBoard);
	} // EO initBoard

	public void initBg() {
		jpGame.add(jlblBg);
		jlblBg.setBounds(0, 0, 1500, 900);
		jlblBg.setIcon(new ImageIcon(imgBg));
	}

	public void initGiveup() { // ��� �� �ʱ�ȭ
		jpGame.add(jlblGiveup);
		jlblGiveup.setBounds(padLeft + sizeBoard / 2 + padBtn - sizeBtn - padBtn, sizeBoard + 100, sizeBtn, 84);
		jlblGiveup.setIcon(imgGiveupStop);
	} // EO initGiveup

	public void initExit() { // ���� �� �ʱ�ȭ
		jpGame.add(jlblExit);
		jlblExit.setBounds(padLeft + sizeBoard / 2 + padBtn, sizeBoard + 100, sizeBtn, 84);
		jlblExit.setIcon(imgExitStop);
	} // EO initExit

	public void initStone() { // �� �� �ʱ�ȭ

		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				jlblStone[i][j] = new JLabel();
				jlblStone[i][j].setBounds(posBoardX + j * sizeStone, posBoardY + i * sizeStone, sizeStone, sizeStone);
				jlblStone[i][j].setBorder(new BevelBorder(BevelBorder.RAISED));
				jpGame.add(jlblStone[i][j]);
			}
		}

	} // EO initStone

	public void initAvail() { // �� �� �ʱ�ȭ

		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				jlblAvail[i][j] = new JLabel();
				jlblAvail[i][j].setBounds(posBoardX + j * sizeStone + (sizeStone - imgAvail.getIconWidth()) / 2,
						posBoardY + i * sizeStone, sizeStone, sizeStone); // imgAvail�� 71x71
				jpGame.add(jlblAvail[i][j]);
			}
		}

	} // EO initAvail

	public void initTier() { // Ƽ�� �� �ʱ�ȭ(�����ú��� Ƽ�� �̹��� �ҷ�����)

		for (int i = 0; i < 2; i++) {
			jlblTier[i] = new JLabel();
			if (uvo.getRating() < 800) {// bronze
				jlblTier[i].setIcon(imgBronze);
			} else if (uvo.getRating() < 1200) {// silver
				jlblTier[i].setIcon(imgstilver);
			} else if (uvo.getRating() < 1400) {// gold
				jlblTier[i].setIcon(imgGold);
			} else if (uvo.getRating() < 1600) {// platinum
				jlblTier[i].setIcon(imgPlat);
			} else if (uvo.getRating() < 2000) {// diamond
				jlblTier[i].setIcon(imgDiamond);
			}
			jpGame.add(jlblTier[i]);
		}

		jlblTier[0].setBounds(30, 120, 40, 40);
		jlblTier[1].setBounds(padLeft + sizeBoard + 30, 120, 40, 40);
	} // EO initTier

	public void initRating() { // ������ �� �ʱ�ȭ

		uvo.getRating();

		for (int i = 0; i < 2; i++) {
			jlblRating[i] = new JLabel();
			jlblRating[i].setText(rating[i] + "   ");
			jlblRating[i].setHorizontalAlignment(SwingConstants.RIGHT); // ��� ����
			jlblRating[i].setFont(fRating);
			jpGame.add(jlblRating[i]);
		}
		jlblRating[0].setBounds(0, 120, padLeft, 40);
		jlblRating[1].setBounds(padLeft + sizeBoard, 120, padLeft, 40);
	} // EO initRating

	public void getInfo() { //�������� �������� �޼���... �����ͼ� �� �迭�� ����.

//		uvoP1 = new UserVO();
		uvoP2 = new UserVO();
//		uvoP1 = uvo;
//		userID[0] = uvoP1.getUserID();
//		rating[0] = uvoP1.getRating();
//		win[0] = uvoP1.getWin();
//		loss[0] = uvoP1.getLoss();
		
		userID[1] = uvoP2.getUserID();
		rating[1] = uvoP2.getRating();
		win[1] = uvoP2.getWin();
		loss[1] = uvoP2.getLoss();
	}

	public void initInfo() { // ID, �̸�, ������ �ʱ�ȭ

		for (int i = 0; i < 2; i++) {
			jlbluserID[i] = new JLabel();
			jlbluserID[i].setText(userID[i]); // �� ���̵� ���
			jlbluserID[i].setHorizontalAlignment(SwingConstants.CENTER); // ��� ����
			jlbluserID[i].setFont(fName);
			jpGame.add(jlbluserID[i]);
		}

		jlbluserID[0].setBounds(0, 50, padLeft, 50);
		jlbluserID[1].setBounds(padLeft + sizeBoard, 50, padLeft, 50);
	} // EO initID

	public void initCount() { // (�� ����)ī��Ʈ �� �ʱ�ȭ

		for (int i = 0; i < 2; i++) {
			jlblCount[i] = new JLabel();
			jlblCount[i].setHorizontalAlignment(SwingConstants.CENTER); // ��� ���� �÷��̾�1
			jlblCount[i].setFont(fCount);
			jpGame.add(jlblCount[i]);
		}

		jlblCount[0].setBounds(0, 200, padLeft, 200);
		jlblCount[1].setBounds(padLeft + sizeBoard, 200, padLeft, 200);

	} // EO initCount

	public void drawStone() { // �� ���� �� �׸���
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				if (board[i][j] == 1) {
					jlblStone[i][j].setIcon(imgBlack);
				}

				else if (board[i][j] == 2) {
					jlblStone[i][j].setIcon(imgWhite);
				}
			}
		}

		gst.setBoard(board); // �� �׷��� ����
	} // EO drawStone

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String ip = "";
		int port = 5000;
		othello.LoginTemp lt2 = new othello.LoginTemp();
		lt2.setId(lt.getId());
		System.out.println("lt2 userID EXISTENCE CHECK : " + lt2.getId());
		Socket s;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		// ------------------------------������------------------------------
		if (e.getSource() == jlblExit) {
			
			// connection to server
				try {
					System.out.println("connecting to server");
					// instantiating s with Socket ip and port of server
					s = new Socket(ip, port);
					System.out.println("connected to server lt.getS() = " + lt2.getS());
					ois = new ObjectInputStream(s.getInputStream());

					// outputstream�� object ������-> ������
					oos = new ObjectOutputStream(s.getOutputStream());
					WaitRoomPanel wrp = new WaitRoomPanel(oos, ois, lt2, ip);

				} catch (UnknownHostException unknown) {
					System.out.println("Unknown HOST - WRONG IP or PORT or SERVER DOWN");
				} catch (IOException ioe) {
					System.out.println("Unknown network error");
				}
			System.exit(0);
		}

		// ------------------------------���------------------------------
		else if (e.getSource() == jlblGiveup) {

		}

		else {
			// ------------------------------�� ���� ������------------------------------
			// ���� �� �ִ� �ڸ��� ���� ���� ����
			for (int i = 0; i <= 7; i++) { // y����
				for (int j = 0; j <= 7; j++) { // x����
					if (e.getSource() == jlblStone[i][j]) { // ���� Ŭ���Ǿ�����
						if (flag == myturn) { // �� ������ ����
							if (board[i][j] == 0) // �ش� ĭ�� ����ִ� ��츸 ����
							{
								// ���� �� �ִ� �ڸ����� �˻� (���� �� ��ġ�� j(x��ǥ),i(y��ǥ))
								// ---------------------------�������� �˻�-----------------------------
								// west(���� ��ǥ���� ����-2ĭ���� �� ���ʱ��� ��ĭ�� ������ �� �˻�)
								if (j >= 2) { // ���� ��ġ�� ���� ������ �ƴϰ�
									if (board[i][j - 1] != 0 && board[i][j - 1] != flag) { // ���� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
										for (int x = j - 2; x >= 0; x--) { // ���� �������� Ž�� ����
											if (board[i][x] == 0)
												break;
											if (board[i][x] == flag) { // ���� ���ݰ� ���� �� ���̸�
												for (int l = x + 1; l <= j - 1; l++) { // ��� �ٸ� ���� ���ʿ��� �������� ������ �ְ�
													board[i][l] = flag; // �� ������
												}
												isStone = true; // ���� ����
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											}
										}
									}
								}

								// east(���� ��ǥ���� ������+2ĭ���� �� �����ʱ��� ��ĭ�� ������ �� �˻�)
								if (j <= 5) { // ���� ��ġ�� ���� ������ �ƴϰ�
									if (board[i][j + 1] != 0 && board[i][j + 1] != flag) { // ������ ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
										for (int x = j + 2; x <= 7; x++) { // ���� �������� Ž�� ����
											if (board[i][x] == 0)
												break;
											if (board[i][x] == flag) { // ���� �� ���̸�
												for (int l = x - 1; l >= j + 1; l--) { // ��� �ٸ� ���� ���ʿ��� �������� ������ �ְ�
													board[i][l] = flag; // �� ������
												}
												isStone = true; // ���� ����
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											}
										}
									}
								}

								// north(���� ��ǥ���� �������� ��ĭ�� �ٸ��� �� �˻�)
								if (i >= 2) { // ���� ��ġ�� ���� ������ �ƴϰ�
									if (board[i - 1][j] != 0 && board[i - 1][j] != flag) { // ���� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
										for (int y = i - 2; y >= 0; y--) { // ���� �������� Ž�� ����
											if (board[y][j] == 0)
												break;
											if (board[y][j] == flag) { // ���� �� ���̸�
												for (int l = y + 1; l <= i - 1; l++) { // ��� �ٸ� ���� ���ʿ��� �������� ������ �ְ�
													board[l][j] = flag; // �� ������
												}
												isStone = true; // ���� ����
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											}
										}
									}
								}

								// south(���� ��ǥ���� �Ʒ������� ��ĭ �� �ٸ��� �� �˻�)
								if (i <= 5) { // ���� ��ġ�� ���� ������ �ƴϰ�
									if (board[i + 1][j] != 0 && board[i + 1][j] != flag) { // �Ʒ��� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
										for (int y = i + 2; y <= 7; y++) { // ���� �������� Ž�� ����
											if (board[y][j] == 0)
												break;
											if (board[y][j] == flag) { // ���� �� ���̸�
												for (int l = y - 1; l >= i + 1; l--) { // ���� �ٸ� ���� ���ʿ��� ��������
													board[l][j] = flag; // �� ������
												}
												isStone = true; // ���� ����
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											}
										}
									}
								}
								// ---------------------------�밢�� ���� �˻�-----------------------------
								// ��
								if (j >= 2 && i >= 2) {
									if (board[i - 1][j - 1] != 0 && board[i - 1][j - 1] != flag) { // �� ���� (0�� �ƴϰ�) ����
																									// �ٸ� �����̸�
										int y = i - 2, x = j - 2; // �� ������������ ��
										while (y >= 0 && x >= 0) { // �� ��ġ�� 0 �̻��̸� �� ����
											if (board[y][x] == 0)
												break;
											if (board[y][x] == flag) { // ���� �� ���̸�
												while (y < i && x < j) { // (�ٲٴ� ���� ��ġ�� ���� ��ġ���� �ظ�) �� ���� �ٸ� �� �� �� ��������
													board[y][x] = flag; // �� ������
													y++;
													x++; // �� ����
												}
												isStone = true; // ���� �ٲ�
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											} else
												y--;
											x--; // ���� ��ġ��
										}
									}
								}

								// ��
								if (j <= 5 && i <= 5) {
									if (board[i + 1][j + 1] != 0 && board[i + 1][j + 1] != flag) { // �� ���� (0�� �ƴϰ�) ����
																									// �ٸ� �����̸�
										int y = i + 2, x = j + 2; // �� ������������ ��
										while (y <= 7 && x <= 7) { // �� ��ġ�� 7 ���ϸ� Ž�� ����
											if (board[y][x] == 0)
												break;// ��ĭ�� ������ ����������
											if (board[y][x] == flag) { // ���� �� ���̸�
												while (y > i && x > j) { // (�ٲٴ� ���� ��ġ�� ���� ��ġ���� �ٸ�) �� ���� �ٸ� �� �� �� ��������
													board[y][x] = flag; // �� ������
													y--;
													x--; // �� ����
												}
												isStone = true; // ���� ����
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											} else
												y++;
											x++; // ���� ��ġ��
										}
									}
								}

								// ��
								if (j <= 5 && i >= 2) {
									if (board[i - 1][j + 1] != 0 && board[i - 1][j + 1] != flag) { // �� ���� (0�� �ƴϰ�) ����
																									// �ٸ� �����̸�
										int y = i - 2, x = j + 2; // �� ������������ ��
										while (y >= 0 && x <= 7) {
											if (board[y][x] == 0)
												break;// ��ĭ�� ������ ����������
											if (board[y][x] == flag) { // ���� �� ���̸�
												while (y < i && x > j) { // (�ٲٴ� ���� ��ġ�� ���� ��ġ���� �ָ�) �� ���� �ٸ� �� �� �� ��������
													board[y][x] = flag; // �� ������
													y++;
													x--; // �� ����
												}
												isStone = true; // ���� ����
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											} else
												y--;
											x++; // ���� ��ġ��
										}
									}
								}

								// ��
								if (j >= 2 && i <= 5) {
									if (board[i + 1][j - 1] != 0 && board[i + 1][j - 1] != flag) { // �� ���� (0�� �ƴϰ�) ����
																									// �ٸ� �����̸�
										int y = i + 2, x = j - 2; // �� ������������ ��
										while (y <= 7 && x >= 0) {
											if (board[y][x] == 0)
												break;// ��ĭ�� ������ ����������
											if (board[y][x] == flag) { // ���� �� ���̸�
												while (y > i && x < j) { // (�ٲٴ� ���� ��ġ�� ���� ��ġ���� �׸�) �� ���� �ٸ� �� �� �� ��������
																			// �ٲٱ�
													board[y][x] = flag; // �� ������
													y--;
													x++; // �� ����
												}
												isStone = true; // ���� ����
												break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
											} else
												y++;
											x--; // ���� ��ġ��
										}
									}
								}

								// �� ���ǿ� �ϳ��� �ش��ϸ� ��� �� ����
								if (isStone == true) {
									if (flag == 1)
										board[i][j] = 1; // ������
									else if (flag == 2)
										board[i][j] = 2; // ��

									isStone = false; // ���� �������� isStone �ʱ�ȭ
									drawStone(); // ���� �� �׸���
									countStone(); // ���� ���� �� ���� ī��Ʈ
									turnover(); // �� �ѱ�鼭 �������� üũ�ϰ� �ѱ� �� avail �׷���
								}

							}
						}
					}
				}
			}

		} // EO flipOver

	}// EO mouseClicked
	
	//�� ���� ������ sendBoard()�޼��带 �ҷ��� ����
	public void turnover() {
		System.out.println("turnover methods called");
		if (flag == 1) { // ��(����)�� ���ʸ�
		flag = 2; // �� ���ʷ�
			gst.setFlag(2);
		} else if (flag == 2) { // ��(�İ�)�� ���ʸ�
			flag = 1; // �� ���ʷ�
			gst.setFlag(1);
		}

		
		checkEnd(); // [����]
		sendBoard(); // ���濡�� oos2
		
	} // EO turnover

	public void checkEnd() { // ���� ��á����?
		// ���� : ���� 64���� ��
		if (cnt[0] + cnt[1] >= 64) {
			isEnd = true;
		}

		if (cntAvail == 0 && cntAvailEnemy == 0) { // [����] �� ��� getCntAvail() ���� 0�ϱ�?
			isEnd = true;
		}
		
		gst.setCntAvail(cntAvail-1);

		if (isEnd == true) {
			endGame(); // ���� ����
		}
	} // EO checkEnd
	
	//OOS�� ����Ǵ� �޼��� --> sendBoard
	public void sendBoard() {
		try {
			System.out.println("GamePanel - oos.writeObject(gst); [SENDING]");
			oos2.writeObject(gst); // flag(��), board[][](����), cnt[](����) ������
			System.out.println("GamePanel - oos.writeObject(gst); [SENT]");
			undrawAvail(); // ���� ���� ���� �� �ִ� ǥ�� ����
			oos2.flush();
			oos2.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void endGame() { // [����]���� ����
		if (cnt[0] > cnt[1]) { // 1P�� ���� �� ������
			JOptionPane.showMessageDialog(this, "���� �¸�");
			// ������ ������
			// P1 Win++, P2 Lose--, P1 Rating +=20, P2 Rating -=20;
			System.out.println("Sent the result // P1 Win++, P2 Lose--, P1 Rating +=20, P2 Rating -=20");
			gst.setWinner(1);
		} else if (cnt[0] < cnt[1]) { // 2P�� ���� �� ������
			JOptionPane.showMessageDialog(this, "���� �¸�");
			// ������ ������
			// P1 Lose++, P2 Win--, P1 Rating -=20, P2 Rating +=20;
			System.out.println("Sent the result // P1 Lose++, P2 Win--, P1 Rating -=20, P2 Rating +=20");
			gst.setWinner(2);
		} else
			JOptionPane.showMessageDialog(this, "���º�");
	
	} // EO endGame

	public int checkAvail() { // ���� �� �ִ� ��� �ڸ� �˻�

		// �˻��� �ʱ�ȭ
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				board_avail[i][j] = 0;
			}
		}

		cntAvail = 0;

		for (int i = 0; i <= 7; i++) { // y����
			for (int j = 0; j <= 7; j++) { // x����
				if (board[i][j] == 0) // �ش� ĭ�� ����ִ� ��츸 ����
				{
					isAvail = false;
					// ���� �� ��ġ�� j(x��ǥ),i(y��ǥ)
					// ---------------------------�������� �˻�-----------------------------
					// west(���� ��ǥ���� ����-2ĭ���� �� ���ʱ��� ��ĭ�� ������ �� �˻�)
					if (!isAvail) { // ���� ���� �� �ִ� �ڸ��� �ƴϰ�
						if (j >= 2) { // ���� ��ġ�� ���� ������ �ƴϰ�
							if (board[i][j - 1] != 0 && board[i][j - 1] != flag) { // ���� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
								for (int x = j - 2; x >= 0; x--) { // ���� �������� Ž�� ����
									if (board[i][x] == 0) { // Ž���ϴٰ� �� ���� ������ ����
										break;
									} else if (board[i][x] == flag) { // ���� ���ݰ� ���� �� ���� ������
										board_avail[i][j] = flag;// ��ġ ����
										isAvail = true; // ���� ���� �� ����
										cntAvail++;
										break; // ���� ���� �� �ִ� �ڸ���� ���� �˾����Ƿ� Ż��
									}
								}
							}
						}
					}

					// east(���� ��ǥ���� ������+2ĭ���� �� �����ʱ��� ��ĭ�� ������ �� �˻�)
					if (!isAvail) {
						if (j <= 5) { // ���� ��ġ�� ���� ������ �ƴϰ�
							if (board[i][j + 1] != 0 && board[i][j + 1] != flag) { // ������ ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
								for (int x = j + 2; x <= 7; x++) { // ���� �������� Ž�� ����
									if (board[i][x] == 0) { // Ž���ϴٰ� �� ���� ������ ����
										break;
									} else if (board[i][x] == flag) { // ���� �� ���̸�
										board_avail[i][j] = flag;// ��ġ ����
										isAvail = true;
										cntAvail++;
										break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
									}
								}
							}
						}
					}

					// north(���� ��ǥ���� �������� ��ĭ�� �ٸ��� �� �˻�)
					if (!isAvail) {
						if (i >= 2) { // ���� ��ġ�� ���� ������ �ƴϰ�
							if (board[i - 1][j] != 0 && board[i - 1][j] != flag) { // ���� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
								for (int y = i - 2; y >= 0; y--) { // ���� �������� Ž�� ����
									if (board[y][j] == 0) // Ž���ϴٰ� �� ���� ������ ����
										break;
									else if (board[y][j] == flag) { // ���� �� ���̸�
										board_avail[i][j] = flag;// ��ġ ����
										isAvail = true; // ���� ���� �� ����
										cntAvail++;
										break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
									}
								}
							}
						}
					}

					// south(���� ��ǥ���� �Ʒ������� ��ĭ �� �ٸ��� �� �˻�)
					if (!isAvail)
						if (i <= 5) { // ���� ��ġ�� ���� ������ �ƴϰ�
							if (board[i + 1][j] != 0 && board[i + 1][j] != flag) { // �Ʒ��� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
								for (int y = i + 2; y <= 7; y++) { // ���� �������� Ž�� ����
									if (board[y][j] == 0) { // Ž���ϴٰ� �� ���� ������ ����
										break;
									} else if (board[y][j] == flag) { // ���� �� ���̸�
										board_avail[i][j] = flag;// ��ġ ����
										isAvail = true; // ���� ���� �� ����
										cntAvail++;
										break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
									}
								}
							}
						}
				}
				// ---------------------------�밢�� ���� �˻�-----------------------------
				// ��
				if (!isAvail) {
					if (j >= 2 && i >= 2) {
						if (board[i - 1][j - 1] != 0 && board[i - 1][j - 1] != flag) { // �� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
							int y = i - 2, x = j - 2; // �� ������������ ��
							while (y >= 0 && x >= 0) { // �� ��ġ�� 0 �̻��̸� �� ����
								if (board[y][x] == 0) { // Ž���ϴٰ� �� ���� ������ ����
									break;
								} else if (board[y][x] == flag) { // ���� �� ���̸�
									board_avail[i][j] = flag;// ��ġ ����
									isAvail = true; // ���� ���� �� ����
									cntAvail++;
									break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
								} else
									y--;
								x--; // ���� ��ġ��
							}
						}
					}
				}

				// ��
				if (!isAvail) {
					if (j <= 5 && i <= 5) {
						if (board[i + 1][j + 1] != 0 && board[i + 1][j + 1] != flag) { // �� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
							int y = i + 2, x = j + 2; // �� ������������ ��
							while (y <= 7 && x <= 7) { // �� ��ġ�� 7 ���ϸ� Ž�� ����
								if (board[y][x] == 0) { // Ž���ϴٰ� �� ���� ������ ����
									break;
								} else if (board[y][x] == flag) { // ���� �� ���̸�
									board_avail[i][j] = flag;// ��ġ ����
									isAvail = true; // ���� ���� �� ����
									cntAvail++;
									break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
								} else
									y++;
								x++; // ���� ��ġ��
							}
						}
					}
				}

				// ��
				if (!isAvail) {
					if (j <= 5 && i >= 2) {
						if (board[i - 1][j + 1] != 0 && board[i - 1][j + 1] != flag) { // �� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
							int y = i - 2, x = j + 2; // �� ������������ ��
							while (y >= 0 && x <= 7) {
								if (board[y][x] == 0) { // Ž���ϴٰ� �� ���� ������ ����
									break;
								} else if (board[y][x] == flag) { // ���� �� ���̸�
									board_avail[i][j] = flag;// ��ġ ����
									isAvail = true; // ���� ���� �� ����
									cntAvail++;
									break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
								} else
									y--;
								x++; // ���� ��ġ��
							}
						}
					}
				}

				// ��
				if (!isAvail) {
					if (j >= 2 && i <= 5) {
						if (board[i + 1][j - 1] != 0 && board[i + 1][j - 1] != flag) { // �� ���� (0�� �ƴϰ�) ���� �ٸ� �����̸�
							int y = i + 2, x = j - 2; // �� ������������ ��
							while (y <= 7 && x >= 0) {
								if (board[y][x] == 0) { // Ž���ϴٰ� �� ���� ������ ����
									break;
								} else if (board[y][x] == flag) { // ���� �� ���̸�
									board_avail[i][j] = flag;// ��ġ ����
									isAvail = true; // ���� ���� �� ����
									cntAvail++;
									break; // �� �������δ� �� �̻� �˻��� �ʿ䰡 �����Ƿ� Ż��
								} else
									y++;
								x--; // ���� ��ġ��
							}
						}
					}
				}
			}
		}

		// ���� ����
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				if (board[i][j] != 0) {
					if (board_avail[i][j] != 0) {
						board_avail[i][j] = 0;
						cntAvail--;
					}
				}
			}
		}
		return cntAvail;
	} // EO checkAvail

	public void drawAvail() {
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				if (board_avail[i][j] == flag)
					jlblAvail[i][j].setIcon(imgAvail);
			}
		}
	} // EO drawAvail

	public void undrawAvail() {
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				jlblAvail[i][j].setIcon(null);
			}
		}
	} // EO undrawAvail

	public void countStone() {
		// �� ���� �ʱ�ȭ
		cnt[0] = 0;
		cnt[1] = 0;

		// �� ���� ����
		for (int k = 0; k <= 7; k++) {
			for (int l = 0; l <= 7; l++) {
				if (board[k][l] == 1)
					cnt[0]++;
				else if (board[k][l] == 2)
					cnt[1]++;
			}
		}

		// ī��Ʈ �� ����
		jlblCount[0].setText(cnt[0] + "");
		jlblCount[1].setText(cnt[1] + "");
	} // EO countStone

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() == jlblGiveup) { // ��� �ѿ���
			jlblGiveup.setIcon(new ImageIcon(imgGiveup.getScaledInstance(150, 84, imgGiveup.SCALE_FAST))); // gif
																											// 200,112��
																											// ��������
		}

		else if (e.getSource() == jlblExit) { // ������ �ѿ���
			jlblExit.setIcon(new ImageIcon(imgExit.getScaledInstance(150, 108, imgExit.SCALE_FAST))); // gif 200,112�� //
																										// ��������
		}

		// Ŀ���� ����ٴϴ� ��
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				if (e.getSource() == jlblStone[i][j]) { // Ŀ���� �� ���� ������
					if (board[i][j] == 0) { // �ش� ��ġ�� ���� �ȳ����� �ִ� ��� �� ǥ��
						if (flag == 1 && flag == myturn)
							jlblStone[i][j].setIcon(imgBlack);
						else if (flag == 2 && flag == myturn)
							jlblStone[i][j].setIcon(imgWhite);
					}
				}
			}
		}
	} // EO mouseEntered

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() == jlblGiveup) // ��� �Ѿƿ�
			jlblGiveup.setIcon(imgGiveupStop);
		else if (e.getSource() == jlblExit) // ������ �Ѿƿ�
			jlblExit.setIcon(imgExitStop);

		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				if (e.getSource() == jlblStone[i][j]) { // Ŀ���� �� ���� ������
					if (board[i][j] == 0) { // ���� �ȳ����� �ִ� �ڸ���� ������ �����
						if (flag == 1)
							jlblStone[i][j].setIcon(null);
						else
							jlblStone[i][j].setIcon(null);
					}
				}
			}
		}
	} // EO mouseExited
		// main method

	@Override
	public void run() {
		System.out.println("Client) Gamepanel - run()");
		try {
			System.out.println("receiving........");
			
//			myturn == 0 || myturn == 1 || myturn == 2
			while (!isEnd) {
				objInitial = ois2.readObject(); // �����κ��� ������ ��ٸ�
				System.out.println("Client) Received from the server");

				if (objInitial instanceof GameStatus) {
					System.out.println("CLIENT) ������ ?");
					gst = (GameStatus) objInitial;
					
					if(gst.getUvo() != null)
					uvoP2 = gst.getUvo(); //���� gst�� ������� ���� gst�ϰ��̴�... at least after second player enters the room
					
					board = gst.getBoard();
					cntAvailEnemy = gst.getCntAvail();
					flag = gst.getFlag();
					gameRoomNumber = gst.getGameNumber();

					if (myturn == 0) {
						myturn = gst.getMyturn();
						if (myturn == 1)
							JOptionPane.showMessageDialog(this, "�����Դϴ�");
						else if (myturn == 2)
							JOptionPane.showMessageDialog(this, "�İ��Դϴ�");
					}

					if (flag == myturn) {
						this.requestFocus(); // ���� �޾��� ���(���� ���� or ������ �� ����) �۾�ǥ���� ������
					}

					if (checkAvail() == 0 && gst.getWinner() == 0) // [����] ���� ���� ������, ���ڰ� �������� �ʾ��� ���� ������
					{
						if (flag == 1) {
							JOptionPane.showMessageDialog(this, "���� ���� ���� �ڸ��� �����ϴ�.");
						} else if (flag == 2) {
							JOptionPane.showMessageDialog(this, "���� ���� ���� �ڸ��� �����ϴ�.");
						}

						turnover();
					}

					drawStone();
					countStone();
					checkAvail();
					undrawAvail();
					if(flag==myturn) drawAvail(); // �� ������ ���� ���� �� �ִ� �ڸ� �����ֱ�

				} else if (objInitial instanceof String) {
					// jpChat�� append��
				} else if (objInitial instanceof UserVO) {
					uvo = (UserVO) objInitial;
					jlbluserID[0].setText(uvo.getUserID());
				} else {
					System.out.println("gst �ƴ�(ä�� �� �ٸ� object��)");
				}

			}

		} catch (IOException | ClassNotFoundException e) {
			try {
				oos2.close();
				ois2.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		}

	} // EO run
} // EO class
