package othello;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import userDAO.UserDAO;
import userDAO.UserVO;

public class GameServer {
	ServerSocket ssGame;

	int cnt = 1;
	int inGameRoomCnt = 0;

	ArrayList<InGameServer> inGameList = new ArrayList<InGameServer>();
	ArrayList<InGamePlayerLlist> inGameRoomList = new ArrayList<InGamePlayerLlist>();
	InGamePlayerLlist igpl;
	ArrayList<Socket> enteringSocket = new ArrayList<Socket>();
	UserVO[] inGameRoomParty = new UserVO[2];

	// random number ������ (room number��)
	Random roomNumber = new Random();
	int rndGameRoomNumber = 0; // �� ��ȣ�� (�������� ��������)
	int rndGameNumberGiven = 0; // ��� rndnumber�� ������� counter
	GameStatus gst;

	Object objInitial;
	String ip;

	GameServer() {
		System.out.println("GameServer) ON");
		gameServerSet();
	}

	private void gameServerSet() {
		Socket client = null;
		try {
			ssGame = new ServerSocket(6000);
			while (true) {
				client = ssGame.accept();
				if (rndGameRoomNumber == 0) {
					rndGameRoomNumber = roomNumber.nextInt(10000000);
				}
				InGameServer igs = new InGameServer(client, rndGameRoomNumber);
				inGameList.add(igs);
				System.out.println("SOCKETLIST INDEX : " + enteringSocket.indexOf(client));
				System.out.println("Connected to gameServer \t" + client);
				rndGameNumberGiven++;
				if (rndGameNumberGiven == 2) {
					rndGameRoomNumber = 0;
				}
				igs.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class InGameServer extends Thread {
		Socket client;
		ObjectOutputStream oos2;
		ObjectInputStream ois2;
		UserDAO udao;
		UserVO uvo;
		int rndGameRoomNumber;
		GameStatus gst2 = new GameStatus();

		InGameServer(Socket client, int rndGameRoomNumber) {
			this.client = client;
//			rndGameNumber = rndGameRoomNum;
			this.rndGameRoomNumber = rndGameRoomNumber;
			ip = client.getInetAddress().getHostAddress(); // cs������� ip������ clientip����

			try {
				oos2 = new ObjectOutputStream(client.getOutputStream()); // oos ����
				ois2 = new ObjectInputStream((client.getInputStream())); // ois ����
				gst2.setGameNumber(rndGameRoomNumber);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			// UVO��ü�� client�κ��� �޾ƿ´� --> client�� server�� uvo�� �α����ϸ鼭 �����ش�
			// uvo��� ��ü�� .getUserID�� clientIP�� hashmap�� key,value������ ������ش�.
			// ������ ��Ŷ �ޱ�
			System.out.println("In Game Server........"); // test printout

			while (true) {
				try {
					objInitial = ois2.readObject(); // object ���濡�� �Ѱ���
					distinguishInGameObjects(objInitial, oos2, ois2, client, gst2);

				} catch (IOException e) {
					inGameList.remove(this);
					e.printStackTrace();
					break;
				} catch (ClassNotFoundException e) {
					inGameList.remove(this);
					e.printStackTrace();
				}
			}
		}
	}

	// QuickGame������ �ǰ� client���� ���� ������ distinguish1�ܰ��ϰ� inGameServer SOCKET����
	// redirect���ֱ�.
	private void distinguishInGameObjects(Object objInitial, ObjectOutputStream oos2, ObjectInputStream ois2,
			Socket client2, GameStatus gst2) {

		try {
			if (objInitial instanceof GameStatus) {
				gst = (GameStatus) objInitial;
				System.out.println("GAME SERVER) GST2 ROOMNUMBER : " + gst2.gameNumber);
				System.out.println("Server) Flag :::: " + gst.getFlag() + " cnt ::: " + cnt);
				if (gst.myturn == 0) { // ���� �����ֱ�
					if (cnt % 2 == 1) {
						System.out.println("SERVER) set gst to 1 as p1...");
						gst.setMyturn(1); // ����
						cnt++;
						System.out.println("Server) �����Դϴ�.");
						System.out.println("SERVER) inGameList ADDED");
						System.out.println("INGAMEROOMCOUNT NUMBER : " + inGameRoomCnt);
						gst.setGameNumber(gst2.getGameNumber());
						System.out.println("GAME SERVER) P1 ROOM NUMBER :" + gst.getGameNumber());
						System.out.println("Player1 : " + inGameRoomParty[0]);
					} else if (cnt % 2 == 0) {
						System.out.println("SERVER) set gst to 1 as p2...");
						gst.setMyturn(2); // �İ�
						cnt++;
						System.out.println("Server) �İ��Դϴ�.");
						System.out.println("SERVER) inGameList ADDED");
						gst.setGameNumber(gst2.getGameNumber());
						System.out.println("GAME SERVER) P2 ROOM NUMBER :" + gst.getGameNumber());
						enteringSocket.clear();
						System.out.println("Count of running gamepanel" + inGameRoomList.size());
					}
					for (InGameServer x : inGameList) {// gst�� broadcast
						if (x.rndGameRoomNumber == gst.gameNumber) {
							System.out.println("Sending GST....");
							System.out.println(gst.getBoard());
							System.out.println(gst.getCntAvail());
							System.out.println("Flag : " + gst.getFlag());
							System.out.println("Myturn : " + gst.getMyturn());
							System.out.println("UVO : " + gst.getUvo().getUserID());
							x.oos2.writeObject(gst);
							x.oos2.flush();
							x.oos2.reset();
							System.out.println("SENT GST");
						}
					}

				}
				System.out.println("GAMESERVER) USERID : " + gst.uvo.getUserID());

				if (gst.myturn != 0) {
					for (InGameServer x : inGameList) {
						if (x.rndGameRoomNumber == gst.gameNumber) {
							System.out.println("Sending GST....");
							System.out.println(gst.getBoard());
							System.out.println(gst.getCntAvail());
							System.out.println("Flag : " + gst.getFlag());
							System.out.println("Myturn : " + gst.getMyturn());
							System.out.println("UVO : " + gst.getUvo().getUserID());
							x.oos2.writeObject(gst);
							x.oos2.flush();
							x.oos2.reset();
							System.out.println("SENT GST");
						}

					}
				}

			}

			else if (objInitial instanceof LoginTemp) {
				LoginTemp lt = (LoginTemp) objInitial;
				UserDAO udao = new UserDAO();

				UserVO uvo = udao.selectOne(lt.getId(), lt.getPassword());

				for (InGameServer x : inGameList) {
					x.oos2.writeObject(uvo);
					x.oos2.flush();
					x.oos2.reset();
				}
			}
		} catch (IOException e) {
			inGameList.remove(this);
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		GameServer gs = new GameServer();
	}

}
