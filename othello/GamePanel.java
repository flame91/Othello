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

	Container con; // 컨테이너

	// ---------------- 패널(Panel) ----------------
	JPanel jpGame = new JPanel(); // 왼쪽 게임 패널
	JPanel jpChat = new JPanel(); // 오른쪽 채팅 패널

	// ---------------- 라벨(Label) ----------------
	JLabel jlblCount[] = new JLabel[2]; // 카운트
	JLabel jlblBoard = new JLabel(); // 말판
	JLabel jlblGrid = new JLabel(); // 격자
	JLabel jlblGiveup = new JLabel(); // 기권
	JLabel jlblExit = new JLabel(); // 나가기
	JLabel jlblBg = new JLabel();
	JLabel[][] jlblStone = new JLabel[8][8]; // 돌
	JLabel[][] jlblAvail = new JLabel[8][8]; // 돌

	// JButton[][] jbtnStone = new JButton[8][8];

	Toolkit tool = Toolkit.getDefaultToolkit();

	// ---------------- 이미지(image) ----------------
	ImageIcon imgBoard = new ImageIcon("src\\images\\board_basic.png"); // 말판 이미지
	ImageIcon imgBlack = new ImageIcon("src\\images\\black_basic.png"); // 검은돌 이미지
	ImageIcon imgWhite = new ImageIcon("src\\images\\white_basic.png"); // 흰돌 이미지
	ImageIcon imgAvail = new ImageIcon("src\\images\\available.png"); // 흰돌 이미지
	Image imgBg = tool.getImage("src\\images\\bg.gif"); // 배경 이미지

	Image imgGiveup = tool.getImage("src\\images\\giveup.gif"); // 기권(롤오버) 이미지
	ImageIcon imgGiveupStop = new ImageIcon("src\\images\\giveup_stop.png"); // 기권(정지) 이미지
	Image imgExit = tool.getImage("src\\images\\exit.gif"); // 나가기(롤오버) 이미지
	ImageIcon imgExitStop = new ImageIcon("src\\images\\exit_stop.png"); // 나가기(정지) 이미지

	ImageIcon imgBronze = new ImageIcon("src\\images\\tier\\bronze.png");
	ImageIcon imgstilver = new ImageIcon("src\\images\\tier\\silver.png");
	ImageIcon imgGold = new ImageIcon("src\\images\\tier\\gold.png");
	ImageIcon imgPlat = new ImageIcon("src\\images\\tier\\platinum.png");
	ImageIcon imgDiamond = new ImageIcon("src\\images\\tier\\diamond.png");

	// ---------------- UI ----------------
	int rightChat = 500; // 채팅창 사이즈
	int sizeBoard = 600; // 말판 사이즈
	int sizeBtn = 150; // 버튼 사이즈
	int padBtn = 100 / 2; // 버튼 간격
	int sizeStone = 75; // 돌 사이즈

	int padLeft = (1600 - sizeBoard - rightChat) / 2; // 창 왼쪽 벽-말판 사이 간격
	int posBoardX = padLeft, posBoardY = 50; // 말판 시작 위치(기본 150, 50)

	int cnt[] = new int[2]; // player 1,2의 돌 숫자

	int flag = 1; // 1=검정돌, 2=흰돌 차례
	int myturn = 0;

	int board[][] = new int[8][8]; // 말판(0 : 없음, 1 : 검은돌, 2 : 흰돌)
	/*
	 * int[][] board= { {1,1,1,1,1,1,1,1}, {1,1,1,1,1,1,1,1}, {1,2,1,1,1,1,1,1},
	 * {1,1,2,1,1,1,1,1}, {1,1,2,2,1,1,1,1}, {1,1,2,2,1,1,1,1}, {1,1,1,1,1,1,1,0},
	 * {1,1,1,1,1,1,2,0}};
	 */

	int board_avail[][] = new int[8][8]; // 돌을 놓을 수 있는 자리 저장
	int cntAvail;
	int cntAvailEnemy;

	// ---------------- 검사 ----------------
	Boolean isEnd = false;
	Boolean isStone = false; // 클릭시 돌이 놓였는지 검사
	Boolean isAvail = false; // 놓을 수 있는 자리를 파악했는지 검사
	Boolean isState1 = true, isState2 = true;

	// ---------------- 폰트 ----------------
	Font fName = new Font("맑은 고딕", Font.BOLD, 50); // 이름용 폰트
	Font fRating = new Font("FixedSys", Font.BOLD, 40); // 레이팅용 폰트
	Font fCount = new Font("Serif", Font.BOLD, 140); // 카운트용 폰트

	// ------------------------------ 유저 정보 ------------------------------
	int win[] = new int[2]; // 승수
	JLabel jlblWin[] = new JLabel[2];

	int loss[] = new int[2]; // 패수
	JLabel jlblLoss[] = new JLabel[2];

	int rating[] = new int[2]; // 레이팅
	JLabel jlblRating[] = new JLabel[2];

	String userID[] = new String[2]; // ID
	JLabel jlbluserID[] = new JLabel[2];

	JLabel jlblTier[] = new JLabel[2]; // 티어

	UserVO uvo, uvoP1, uvoP2;
	GameStatus gst = new GameStatus();
	LoginTemp lt;

	// game room number
	int gameRoomNumber = 0;

	// ------------------------------ 네트워크 ------------------------------
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
		
		// Layout : 컨테이너에 패널 추가
		con = getContentPane();
		con.setLayout(new GridLayout(1, 2, (-800 + rightChat) * 2, 0)); // 왼쪽 영역이 오른쪽 영역보다 400px만큼 많음
		con.add(jpGame); // 컨테이너에 jpGame 추가
		con.add(jpChat); // 컨테이너에 jpChat 추가

		jpGame.setLayout(null);
		jpChat.setBackground(new Color(0, 0, 0));

		// 플레이어 정보 불러오기
		isEnd = false;
		getInfo(); // 상대유저 정보(ID, 성, 이름, 승/패, 레이팅 불러오기)
		initBgm();
		initInfo(); // 유저 정보 초기화
		initTier(); // 티어
		initRating(); // 레이팅
		initCount(); // 카운트
		initStone(); // 돌 초기화
		initAvail(); // 돌 놓을 수 있는 자리 초기화
		initGiveup(); // 기권 아이콘
		initExit(); // 나가기 아이콘
		initBoard(); // 말판

		// 돌 가운데 4x4 초기화
		board[3][3] = 2;
		board[3][4] = 1;
		board[4][3] = 1;
		board[4][4] = 2;

		initBg(); // 배경
		drawStone(); // 돌 그리기
		checkAvail(); // 돌 놓을 수 있는 자리 검사
		drawAvail();
		countStone(); // 각자 가진 돌 세기

		// 격자 초기화(drawLine으로 대체)
		// jpGame.add(jlblGrid);
		// jlblBoard.setBounds(posBoardX,posBoardY,sizeBoard,sizeBoard);
		// jlblBoard.setIcon(imgGrid);

		initListener(); // 리스너

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1600, 900); // 해상도 1600x900
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
	
	public void initListener() { // 리스너 초기화
		// 돌마다 리스너 부여
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				jlblStone[i][j].addMouseListener(this);
			}
		}

		// 기권, 나가기 라벨에 Listener 부여
		jlblGiveup.addMouseListener(this);
		jlblExit.addMouseListener(this);
	} // EO initLIstener

	public void initBoard() { // 말판 초기화
		jpGame.add(jlblBoard);
		jlblBoard.setBounds(posBoardX, posBoardY, sizeBoard, sizeBoard); // 150,50 600,600
		jlblBoard.setIcon(imgBoard);
	} // EO initBoard

	public void initBg() {
		jpGame.add(jlblBg);
		jlblBg.setBounds(0, 0, 1500, 900);
		jlblBg.setIcon(new ImageIcon(imgBg));
	}

	public void initGiveup() { // 기권 라벨 초기화
		jpGame.add(jlblGiveup);
		jlblGiveup.setBounds(padLeft + sizeBoard / 2 + padBtn - sizeBtn - padBtn, sizeBoard + 100, sizeBtn, 84);
		jlblGiveup.setIcon(imgGiveupStop);
	} // EO initGiveup

	public void initExit() { // 종료 라벨 초기화
		jpGame.add(jlblExit);
		jlblExit.setBounds(padLeft + sizeBoard / 2 + padBtn, sizeBoard + 100, sizeBtn, 84);
		jlblExit.setIcon(imgExitStop);
	} // EO initExit

	public void initStone() { // 돌 라벨 초기화

		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				jlblStone[i][j] = new JLabel();
				jlblStone[i][j].setBounds(posBoardX + j * sizeStone, posBoardY + i * sizeStone, sizeStone, sizeStone);
				jlblStone[i][j].setBorder(new BevelBorder(BevelBorder.RAISED));
				jpGame.add(jlblStone[i][j]);
			}
		}

	} // EO initStone

	public void initAvail() { // 돌 라벨 초기화

		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				jlblAvail[i][j] = new JLabel();
				jlblAvail[i][j].setBounds(posBoardX + j * sizeStone + (sizeStone - imgAvail.getIconWidth()) / 2,
						posBoardY + i * sizeStone, sizeStone, sizeStone); // imgAvail은 71x71
				jpGame.add(jlblAvail[i][j]);
			}
		}

	} // EO initAvail

	public void initTier() { // 티어 라벨 초기화(레이팅별로 티어 이미지 불러오기)

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

	public void initRating() { // 레이팅 라벨 초기화

		uvo.getRating();

		for (int i = 0; i < 2; i++) {
			jlblRating[i] = new JLabel();
			jlblRating[i].setText(rating[i] + "   ");
			jlblRating[i].setHorizontalAlignment(SwingConstants.RIGHT); // 가운데 정렬
			jlblRating[i].setFont(fRating);
			jpGame.add(jlblRating[i]);
		}
		jlblRating[0].setBounds(0, 120, padLeft, 40);
		jlblRating[1].setBounds(padLeft + sizeBoard, 120, padLeft, 40);
	} // EO initRating

	public void getInfo() { //유저정보 가져오는 메서드... 가져와서 각 배열에 저장.

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

	public void initInfo() { // ID, 이름, 레이팅 초기화

		for (int i = 0; i < 2; i++) {
			jlbluserID[i] = new JLabel();
			jlbluserID[i].setText(userID[i]); // 내 아이디 출력
			jlbluserID[i].setHorizontalAlignment(SwingConstants.CENTER); // 가운데 정렬
			jlbluserID[i].setFont(fName);
			jpGame.add(jlbluserID[i]);
		}

		jlbluserID[0].setBounds(0, 50, padLeft, 50);
		jlbluserID[1].setBounds(padLeft + sizeBoard, 50, padLeft, 50);
	} // EO initID

	public void initCount() { // (돌 개수)카운트 라벨 초기화

		for (int i = 0; i < 2; i++) {
			jlblCount[i] = new JLabel();
			jlblCount[i].setHorizontalAlignment(SwingConstants.CENTER); // 가운데 정렬 플레이어1
			jlblCount[i].setFont(fCount);
			jpGame.add(jlblCount[i]);
		}

		jlblCount[0].setBounds(0, 200, padLeft, 200);
		jlblCount[1].setBounds(padLeft + sizeBoard, 200, padLeft, 200);

	} // EO initCount

	public void drawStone() { // 판 위에 돌 그리기
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

		gst.setBoard(board); // 다 그려진 말판
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
		// ------------------------------나가기------------------------------
		if (e.getSource() == jlblExit) {
			
			// connection to server
				try {
					System.out.println("connecting to server");
					// instantiating s with Socket ip and port of server
					s = new Socket(ip, port);
					System.out.println("connected to server lt.getS() = " + lt2.getS());
					ois = new ObjectInputStream(s.getInputStream());

					// outputstream에 object 보내기-> 서버로
					oos = new ObjectOutputStream(s.getOutputStream());
					WaitRoomPanel wrp = new WaitRoomPanel(oos, ois, lt2, ip);

				} catch (UnknownHostException unknown) {
					System.out.println("Unknown HOST - WRONG IP or PORT or SERVER DOWN");
				} catch (IOException ioe) {
					System.out.println("Unknown network error");
				}
			System.exit(0);
		}

		// ------------------------------기권------------------------------
		else if (e.getSource() == jlblGiveup) {

		}

		else {
			// ------------------------------돌 놓고 뒤집기------------------------------
			// 놓을 수 있는 자리가 있을 때만 실행
			for (int i = 0; i <= 7; i++) { // y방향
				for (int j = 0; j <= 7; j++) { // x방향
					if (e.getSource() == jlblStone[i][j]) { // 판이 클릭되었으면
						if (flag == myturn) { // 내 차례일 때만
							if (board[i][j] == 0) // 해당 칸이 비어있는 경우만 실행
							{
								// 놓을 수 있는 자리인지 검사 (현재 돌 위치는 j(x좌표),i(y좌표))
								// ---------------------------동서남북 검사-----------------------------
								// west(현재 좌표에서 왼쪽-2칸부터 맨 왼쪽까지 한칸씩 같은색 돌 검색)
								if (j >= 2) { // 현재 위치가 가장 서쪽이 아니고
									if (board[i][j - 1] != 0 && board[i][j - 1] != flag) { // 왼쪽 돌이 (0이 아니고) 나와 다른 색깔이면
										for (int x = j - 2; x >= 0; x--) { // 서쪽 방향으로 탐색 시작
											if (board[i][x] == 0)
												break;
											if (board[i][x] == flag) { // 현재 공격과 같은 색 돌이면
												for (int l = x + 1; l <= j - 1; l++) { // 가운데 다른 색돌 서쪽에서 동쪽으로 뒤집어 주고
													board[i][l] = flag; // 돌 뒤집기
												}
												isStone = true; // 돌이 놓임
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											}
										}
									}
								}

								// east(현재 좌표에서 오른쪽+2칸부터 맨 오른쪽까지 한칸씩 같은색 돌 검색)
								if (j <= 5) { // 현재 위치가 가장 동쪽이 아니고
									if (board[i][j + 1] != 0 && board[i][j + 1] != flag) { // 오른쪽 돌이 (0이 아니고) 나와 다른 색깔이면
										for (int x = j + 2; x <= 7; x++) { // 동쪽 방향으로 탐색 시작
											if (board[i][x] == 0)
												break;
											if (board[i][x] == flag) { // 같은 색 돌이면
												for (int l = x - 1; l >= j + 1; l--) { // 가운데 다른 색돌 동쪽에서 서쪽으로 뒤집어 주고
													board[i][l] = flag; // 돌 뒤집기
												}
												isStone = true; // 돌이 놓임
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											}
										}
									}
								}

								// north(현재 좌표에서 위쪽으로 한칸씩 다른색 돌 검색)
								if (i >= 2) { // 현재 위치가 가장 북쪽이 아니고
									if (board[i - 1][j] != 0 && board[i - 1][j] != flag) { // 위쪽 돌이 (0이 아니고) 나와 다른 색깔이면
										for (int y = i - 2; y >= 0; y--) { // 북쪽 방향으로 탐색 시작
											if (board[y][j] == 0)
												break;
											if (board[y][j] == flag) { // 같은 색 돌이면
												for (int l = y + 1; l <= i - 1; l++) { // 가운데 다른 색돌 북쪽에서 남쪽으로 뒤집어 주고
													board[l][j] = flag; // 돌 뒤집기
												}
												isStone = true; // 돌이 놓임
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											}
										}
									}
								}

								// south(현재 좌표에서 아래쪽으로 한칸 씩 다른색 돌 검색)
								if (i <= 5) { // 현재 위치가 가장 남쪽이 아니고
									if (board[i + 1][j] != 0 && board[i + 1][j] != flag) { // 아래쪽 돌이 (0이 아니고) 나와 다른 색깔이면
										for (int y = i + 2; y <= 7; y++) { // 남쪽 방향으로 탐색 시작
											if (board[y][j] == 0)
												break;
											if (board[y][j] == flag) { // 같은 색 돌이면
												for (int l = y - 1; l >= i + 1; l--) { // 사이 다른 색돌 남쪽에서 북쪽으로
													board[l][j] = flag; // 돌 뒤집기
												}
												isStone = true; // 돌이 놓임
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											}
										}
									}
								}
								// ---------------------------대각선 방향 검사-----------------------------
								// ↖
								if (j >= 2 && i >= 2) {
									if (board[i - 1][j - 1] != 0 && board[i - 1][j - 1] != flag) { // ↖ 돌이 (0이 아니고) 나와
																									// 다른 색깔이면
										int y = i - 2, x = j - 2; // ↖ 다음다음부터 비교
										while (y >= 0 && x >= 0) { // 비교 위치가 0 이상이면 비교 시작
											if (board[y][x] == 0)
												break;
											if (board[y][x] == flag) { // 같은 색 돌이면
												while (y < i && x < j) { // (바꾸는 돌의 위치가 현재 위치보다 ↖면) 그 사이 다른 색 돌 ↘ 방향으로
													board[y][x] = flag; // 돌 뒤집기
													y++;
													x++; // ↘ 방향
												}
												isStone = true; // 돌이 바뀜
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											} else
												y--;
											x--; // 다음 위치로
										}
									}
								}

								// ↘
								if (j <= 5 && i <= 5) {
									if (board[i + 1][j + 1] != 0 && board[i + 1][j + 1] != flag) { // ↘ 돌이 (0이 아니고) 나와
																									// 다른 색깔이면
										int y = i + 2, x = j + 2; // ↘ 다음다음부터 비교
										while (y <= 7 && x <= 7) { // 비교 위치가 7 이하면 탐색 시작
											if (board[y][x] == 0)
												break;// 빈칸을 만나면 빠져나가기
											if (board[y][x] == flag) { // 같은 색 돌이면
												while (y > i && x > j) { // (바꾸는 돌의 위치가 현재 위치보다 ↘면) 그 사이 다른 색 돌 ↖ 방향으로
													board[y][x] = flag; // 돌 뒤집기
													y--;
													x--; // ↖ 방향
												}
												isStone = true; // 돌이 놓임
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											} else
												y++;
											x++; // 다음 위치로
										}
									}
								}

								// ↗
								if (j <= 5 && i >= 2) {
									if (board[i - 1][j + 1] != 0 && board[i - 1][j + 1] != flag) { // ↗ 돌이 (0이 아니고) 나와
																									// 다른 색깔이면
										int y = i - 2, x = j + 2; // ↗ 다음다음부터 비교
										while (y >= 0 && x <= 7) {
											if (board[y][x] == 0)
												break;// 빈칸을 만나면 빠져나가기
											if (board[y][x] == flag) { // 같은 색 돌이면
												while (y < i && x > j) { // (바꾸는 돌의 위치가 현재 위치보다 ↗면) 그 사이 다른 색 돌 ↙ 방향으로
													board[y][x] = flag; // 돌 뒤집기
													y++;
													x--; // ↙ 방향
												}
												isStone = true; // 돌이 놓임
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											} else
												y--;
											x++; // 다음 위치로
										}
									}
								}

								// ↙
								if (j >= 2 && i <= 5) {
									if (board[i + 1][j - 1] != 0 && board[i + 1][j - 1] != flag) { // ↙ 돌이 (0이 아니고) 나와
																									// 다른 색깔이면
										int y = i + 2, x = j - 2; // ↙ 다음다음부터 비교
										while (y <= 7 && x >= 0) {
											if (board[y][x] == 0)
												break;// 빈칸을 만나면 빠져나가기
											if (board[y][x] == flag) { // 같은 색 돌이면
												while (y > i && x < j) { // (바꾸는 돌의 위치가 현재 위치보다 ↙면) 그 사이 다른 색 돌 ↗ 방향으로
																			// 바꾸기
													board[y][x] = flag; // 돌 뒤집기
													y--;
													x++; // ↗ 방향
												}
												isStone = true; // 돌이 놓임
												break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
											} else
												y++;
											x--; // 다음 위치로
										}
									}
								}

								// 위 조건에 하나라도 해당하면 가운데 돌 놓기
								if (isStone == true) {
									if (flag == 1)
										board[i][j] = 1; // 검은돌
									else if (flag == 2)
										board[i][j] = 2; // 흰돌

									isStone = false; // 돌이 놓였으니 isStone 초기화
									drawStone(); // 놓인 돌 그리기
									countStone(); // 각자 가진 돌 개수 카운트
									turnover(); // 턴 넘기면서 종료조건 체크하고 넘긴 후 avail 그려줌
								}

							}
						}
					}
				}
			}

		} // EO flipOver

	}// EO mouseClicked
	
	//내 턴이 끝나면 sendBoard()메서드를 불러서 실행
	public void turnover() {
		System.out.println("turnover methods called");
		if (flag == 1) { // 흑(선공)의 차례면
		flag = 2; // 백 차례로
			gst.setFlag(2);
		} else if (flag == 2) { // 백(후공)의 차례면
			flag = 1; // 흑 차례로
			gst.setFlag(1);
		}

		
		checkEnd(); // [성재]
		sendBoard(); // 상대방에게 oos2
		
	} // EO turnover

	public void checkEnd() { // 맵이 꽉찼는지?
		// 종료 : 말이 64개가 됨
		if (cnt[0] + cnt[1] >= 64) {
			isEnd = true;
		}

		if (cntAvail == 0 && cntAvailEnemy == 0) { // [성재] 왜 계속 getCntAvail() 값이 0일까?
			isEnd = true;
		}
		
		gst.setCntAvail(cntAvail-1);

		if (isEnd == true) {
			endGame(); // 게임 종료
		}
	} // EO checkEnd
	
	//OOS가 실행되는 메서드 --> sendBoard
	public void sendBoard() {
		try {
			System.out.println("GamePanel - oos.writeObject(gst); [SENDING]");
			oos2.writeObject(gst); // flag(턴), board[][](말판), cnt[](갯수) 던지기
			System.out.println("GamePanel - oos.writeObject(gst); [SENT]");
			undrawAvail(); // 보낸 순간 놓을 수 있는 표시 지움
			oos2.flush();
			oos2.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void endGame() { // [성재]게임 종료
		if (cnt[0] > cnt[1]) { // 1P의 돌이 더 많으면
			JOptionPane.showMessageDialog(this, "흑의 승리");
			// 서버로 보내기
			// P1 Win++, P2 Lose--, P1 Rating +=20, P2 Rating -=20;
			System.out.println("Sent the result // P1 Win++, P2 Lose--, P1 Rating +=20, P2 Rating -=20");
			gst.setWinner(1);
		} else if (cnt[0] < cnt[1]) { // 2P의 돌이 더 많으면
			JOptionPane.showMessageDialog(this, "백의 승리");
			// 서버로 보내기
			// P1 Lose++, P2 Win--, P1 Rating -=20, P2 Rating +=20;
			System.out.println("Sent the result // P1 Lose++, P2 Win--, P1 Rating -=20, P2 Rating +=20");
			gst.setWinner(2);
		} else
			JOptionPane.showMessageDialog(this, "무승부");
	
	} // EO endGame

	public int checkAvail() { // 놓을 수 있는 모든 자리 검사

		// 검사판 초기화
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				board_avail[i][j] = 0;
			}
		}

		cntAvail = 0;

		for (int i = 0; i <= 7; i++) { // y방향
			for (int j = 0; j <= 7; j++) { // x방향
				if (board[i][j] == 0) // 해당 칸이 비어있는 경우만 실행
				{
					isAvail = false;
					// 현재 돌 위치는 j(x좌표),i(y좌표)
					// ---------------------------동서남북 검사-----------------------------
					// west(현재 좌표에서 왼쪽-2칸부터 맨 왼쪽까지 한칸씩 같은색 돌 검색)
					if (!isAvail) { // 아직 놓을 수 있는 자리가 아니고
						if (j >= 2) { // 현재 위치가 가장 서쪽이 아니고
							if (board[i][j - 1] != 0 && board[i][j - 1] != flag) { // 왼쪽 돌이 (0이 아니고) 나와 다른 색깔이면
								for (int x = j - 2; x >= 0; x--) { // 서쪽 방향으로 탐색 시작
									if (board[i][x] == 0) { // 탐색하다가 빈 공간 나오면 종료
										break;
									} else if (board[i][x] == flag) { // 현재 공격과 같은 색 돌이 있으면
										board_avail[i][j] = flag;// 위치 저장
										isAvail = true; // 돌을 놓을 수 있음
										cntAvail++;
										break; // 돌을 놓을 수 있는 자리라는 것을 알았으므로 탈출
									}
								}
							}
						}
					}

					// east(현재 좌표에서 오른쪽+2칸부터 맨 오른쪽까지 한칸씩 같은색 돌 검색)
					if (!isAvail) {
						if (j <= 5) { // 현재 위치가 가장 동쪽이 아니고
							if (board[i][j + 1] != 0 && board[i][j + 1] != flag) { // 오른쪽 돌이 (0이 아니고) 나와 다른 색깔이면
								for (int x = j + 2; x <= 7; x++) { // 동쪽 방향으로 탐색 시작
									if (board[i][x] == 0) { // 탐색하다가 빈 공간 나오면 종료
										break;
									} else if (board[i][x] == flag) { // 같은 색 돌이면
										board_avail[i][j] = flag;// 위치 저장
										isAvail = true;
										cntAvail++;
										break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
									}
								}
							}
						}
					}

					// north(현재 좌표에서 위쪽으로 한칸씩 다른색 돌 검색)
					if (!isAvail) {
						if (i >= 2) { // 현재 위치가 가장 북쪽이 아니고
							if (board[i - 1][j] != 0 && board[i - 1][j] != flag) { // 위쪽 돌이 (0이 아니고) 나와 다른 색깔이면
								for (int y = i - 2; y >= 0; y--) { // 북쪽 방향으로 탐색 시작
									if (board[y][j] == 0) // 탐색하다가 빈 공간 나오면 종료
										break;
									else if (board[y][j] == flag) { // 같은 색 돌이면
										board_avail[i][j] = flag;// 위치 저장
										isAvail = true; // 돌을 놓을 수 있음
										cntAvail++;
										break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
									}
								}
							}
						}
					}

					// south(현재 좌표에서 아래쪽으로 한칸 씩 다른색 돌 검색)
					if (!isAvail)
						if (i <= 5) { // 현재 위치가 가장 남쪽이 아니고
							if (board[i + 1][j] != 0 && board[i + 1][j] != flag) { // 아래쪽 돌이 (0이 아니고) 나와 다른 색깔이면
								for (int y = i + 2; y <= 7; y++) { // 남쪽 방향으로 탐색 시작
									if (board[y][j] == 0) { // 탐색하다가 빈 공간 나오면 종료
										break;
									} else if (board[y][j] == flag) { // 같은 색 돌이면
										board_avail[i][j] = flag;// 위치 저장
										isAvail = true; // 돌을 놓을 수 있음
										cntAvail++;
										break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
									}
								}
							}
						}
				}
				// ---------------------------대각선 방향 검사-----------------------------
				// ↖
				if (!isAvail) {
					if (j >= 2 && i >= 2) {
						if (board[i - 1][j - 1] != 0 && board[i - 1][j - 1] != flag) { // ↖ 돌이 (0이 아니고) 나와 다른 색깔이면
							int y = i - 2, x = j - 2; // ↖ 다음다음부터 비교
							while (y >= 0 && x >= 0) { // 비교 위치가 0 이상이면 비교 시작
								if (board[y][x] == 0) { // 탐색하다가 빈 공간 나오면 종료
									break;
								} else if (board[y][x] == flag) { // 같은 색 돌이면
									board_avail[i][j] = flag;// 위치 저장
									isAvail = true; // 돌을 놓을 수 있음
									cntAvail++;
									break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
								} else
									y--;
								x--; // 다음 위치로
							}
						}
					}
				}

				// ↘
				if (!isAvail) {
					if (j <= 5 && i <= 5) {
						if (board[i + 1][j + 1] != 0 && board[i + 1][j + 1] != flag) { // ↘ 돌이 (0이 아니고) 나와 다른 색깔이면
							int y = i + 2, x = j + 2; // ↘ 다음다음부터 비교
							while (y <= 7 && x <= 7) { // 비교 위치가 7 이하면 탐색 시작
								if (board[y][x] == 0) { // 탐색하다가 빈 공간 나오면 종료
									break;
								} else if (board[y][x] == flag) { // 같은 색 돌이면
									board_avail[i][j] = flag;// 위치 저장
									isAvail = true; // 돌을 놓을 수 있음
									cntAvail++;
									break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
								} else
									y++;
								x++; // 다음 위치로
							}
						}
					}
				}

				// ↗
				if (!isAvail) {
					if (j <= 5 && i >= 2) {
						if (board[i - 1][j + 1] != 0 && board[i - 1][j + 1] != flag) { // ↗ 돌이 (0이 아니고) 나와 다른 색깔이면
							int y = i - 2, x = j + 2; // ↗ 다음다음부터 비교
							while (y >= 0 && x <= 7) {
								if (board[y][x] == 0) { // 탐색하다가 빈 공간 나오면 종료
									break;
								} else if (board[y][x] == flag) { // 같은 색 돌이면
									board_avail[i][j] = flag;// 위치 저장
									isAvail = true; // 돌을 놓을 수 있음
									cntAvail++;
									break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
								} else
									y--;
								x++; // 다음 위치로
							}
						}
					}
				}

				// ↙
				if (!isAvail) {
					if (j >= 2 && i <= 5) {
						if (board[i + 1][j - 1] != 0 && board[i + 1][j - 1] != flag) { // ↙ 돌이 (0이 아니고) 나와 다른 색깔이면
							int y = i + 2, x = j - 2; // ↙ 다음다음부터 비교
							while (y <= 7 && x >= 0) {
								if (board[y][x] == 0) { // 탐색하다가 빈 공간 나오면 종료
									break;
								} else if (board[y][x] == flag) { // 같은 색 돌이면
									board_avail[i][j] = flag;// 위치 저장
									isAvail = true; // 돌을 놓을 수 있음
									cntAvail++;
									break; // 이 방향으로는 더 이상 검사할 필요가 없으므로 탈출
								} else
									y++;
								x--; // 다음 위치로
							}
						}
					}
				}
			}
		}

		// 버그 수정
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
		// 돌 개수 초기화
		cnt[0] = 0;
		cnt[1] = 0;

		// 돌 개수 세기
		for (int k = 0; k <= 7; k++) {
			for (int l = 0; l <= 7; l++) {
				if (board[k][l] == 1)
					cnt[0]++;
				else if (board[k][l] == 2)
					cnt[1]++;
			}
		}

		// 카운트 라벨 갱신
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
		if (e.getSource() == jlblGiveup) { // 기권 롤오버
			jlblGiveup.setIcon(new ImageIcon(imgGiveup.getScaledInstance(150, 84, imgGiveup.SCALE_FAST))); // gif
																											// 200,112로
																											// 리사이즈
		}

		else if (e.getSource() == jlblExit) { // 나가기 롤오버
			jlblExit.setIcon(new ImageIcon(imgExit.getScaledInstance(150, 108, imgExit.SCALE_FAST))); // gif 200,112로 //
																										// 리사이즈
		}

		// 커서를 따라다니는 돌
		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				if (e.getSource() == jlblStone[i][j]) { // 커서가 판 위에 있으면
					if (board[i][j] == 0) { // 해당 위치에 돌이 안놓여져 있는 경우 돌 표시
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
		if (e.getSource() == jlblGiveup) // 기권 롤아웃
			jlblGiveup.setIcon(imgGiveupStop);
		else if (e.getSource() == jlblExit) // 나가기 롤아웃
			jlblExit.setIcon(imgExitStop);

		for (int i = 0; i <= 7; i++) {
			for (int j = 0; j <= 7; j++) {
				if (e.getSource() == jlblStone[i][j]) { // 커서가 돌 위에 있으면
					if (board[i][j] == 0) { // 돌이 안놓여져 있던 자리라면 아이콘 지우기
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
				objInitial = ois2.readObject(); // 서버로부터 응답을 기다림
				System.out.println("Client) Received from the server");

				if (objInitial instanceof GameStatus) {
					System.out.println("CLIENT) 진입함 ?");
					gst = (GameStatus) objInitial;
					
					if(gst.getUvo() != null)
					uvoP2 = gst.getUvo(); //받은 gst는 상대편에서 받은 gst일것이다... at least after second player enters the room
					
					board = gst.getBoard();
					cntAvailEnemy = gst.getCntAvail();
					flag = gst.getFlag();
					gameRoomNumber = gst.getGameNumber();

					if (myturn == 0) {
						myturn = gst.getMyturn();
						if (myturn == 1)
							JOptionPane.showMessageDialog(this, "선공입니다");
						else if (myturn == 2)
							JOptionPane.showMessageDialog(this, "후공입니다");
					}

					if (flag == myturn) {
						this.requestFocus(); // 응답 받았을 경우(상대방 접속 or 상대방이 돌 놓음) 작업표시줄 깜빡임
					}

					if (checkAvail() == 0 && gst.getWinner() == 0) // [성재] 놓을 곳이 없으면, 승자가 정해지지 않았을 때만 보여줌
					{
						if (flag == 1) {
							JOptionPane.showMessageDialog(this, "흑이 돌을 놓을 자리가 없습니다.");
						} else if (flag == 2) {
							JOptionPane.showMessageDialog(this, "백이 돌을 놓을 자리가 없습니다.");
						}

						turnover();
					}

					drawStone();
					countStone();
					checkAvail();
					undrawAvail();
					if(flag==myturn) drawAvail(); // 내 차례일 떄만 놓을 수 있는 자리 보여주기

				} else if (objInitial instanceof String) {
					// jpChat에 append하
				} else if (objInitial instanceof UserVO) {
					uvo = (UserVO) objInitial;
					jlbluserID[0].setText(uvo.getUserID());
				} else {
					System.out.println("gst 아님(채팅 등 다른 object임)");
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
