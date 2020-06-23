package othello;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import userDAO.UserDAO;
import userDAO.UserVO;

public class MainServer {
	ServerSocket ss;
	UserDAO udao;
	UserVO uvo;
	ArrayList<GameServer> list = new ArrayList<GameServer>(); // 대기방
	ArrayList<GameRoomVO> gameList = new ArrayList<GameRoomVO>();
	String ip;
	Object objInitial; // local objects for storing id, pw.
	LoginTemp lt;
	int cnt = 1;

	MainServer() {
		System.out.println("MainServer) ON");
		CSListener();
	}

	private void CSListener() {
		Socket client = null;
		try {
			ss = new ServerSocket(5000);

			while (true) {
				client = ss.accept();
				GameServer gs = new GameServer(client);
				list.add(gs);
				System.out.println("Connected \t" + client);
				gs.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// GAMESERVER THREAD -- DOES THE LISTENING FOR CLIENT CONNECTION ***************
	// <<<<<======= CLIENT CONNECTION LISTENER
	class GameServer extends Thread { // individual thread per user
		Socket client;
		ObjectOutputStream oos;
		ObjectInputStream ois;

		GameServer(Socket client) {
			this.client = client;
			ip = client.getInetAddress().getHostAddress(); // cs연결된훈 ip변수에 clientip저장

			try {
				oos = new ObjectOutputStream(client.getOutputStream()); // oos 생성
				ois = new ObjectInputStream((client.getInputStream())); // ois 생성

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			// UVO객체를 client로부터 받아온다 --> client는 server로 uvo를 로그인하면서 던저준다
			// uvo라는 객체의 .getUserID로 clientIP와 hashmap로 key,value식으로 만들어준다.
			// 던저준 패킷 받기
			System.out.println("client connected123"); // test printout

			int distinguish = 0;
			while (true) {
				try {
					objInitial = ois.readObject(); // object 상대방에게 넘겨줌
					distinguishObjects(objInitial, oos, ois, client);

				} catch (IOException e) {
					list.remove(this);
//					list.remove(list.indexOf(client));
					e.printStackTrace();
					break;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// DB랑 연결해서 Client로부터 받은 id와 pw 정보로 같은 유저프로필을 불러서 확인하기...
	private LoginTemp loginCheck(Object objId, Object objPw, Socket client) {
		boolean permission = false;
		UserDAO udao = new UserDAO();
		UserVO uvo = new UserVO();
		lt = new LoginTemp();

		uvo = udao.selectOne(objId.toString(), objPw.toString());

		if (uvo.getUserID().equals(objId.toString()) && uvo.getUserPW().equals(objPw.toString())) {
			permission = true;
			lt.setId(uvo.getUserID());
			lt.setPassword(uvo.getUserPW());
			lt.setGivePermission(permission);
			System.out.println("permission granted" + lt.getId());// test printout
		}

		return lt;
	}

	private UserVO registerUser(UserVO vo) {
		UserDAO dao = new UserDAO();
		UserVO voR = new UserVO();
		UserVO voR2 = new UserVO();

		voR.setUserID(vo.getUserID());
		voR.setUserPW(vo.getUserPW());
		voR.setLastName(vo.getLastName());
		voR.setFirstName(vo.getFirstName());
		voR.setuemail(vo.getuemail());
		voR.setWin(vo.getWin());
		voR.setLoss(vo.getLoss());
		voR.setWlRatio(vo.getWlRatio());
		voR.setRating(vo.getRating());

		System.out.println(voR.getUserID() + voR.getUserPW());

		dao.register(voR);
		voR2 = dao.selectOne(voR.getUserID(), voR.getUserPW());
		if (voR2.getUserID().equals(voR.getUserID()) && voR2.getUserPW().equals(voR.getUserPW())) {
			System.out.println("Register Successful");// test printout
			return voR;
		}
		return voR;
	}

	private int distinguishObjects(Object obj, ObjectOutputStream oos, ObjectInputStream ois, Socket client) {
		int type = -1;
		try {
			System.out.println("distinguishing...");
//**********************LOGGING IN DISTINGUISH***************************************************
			if (objInitial instanceof LoginTemp) {
				System.out.println("login attempt");
				// filter incoming objects depending on datatypes --> 여기서 메서드를 구분해서 사용해서 정보처리 할것
				System.out.println("reading......");// test printout
				lt = (LoginTemp) objInitial;
				try {
					// id
					System.out.println(lt.getId() + "");// test printout
					System.out.println("before writing...");// test printout

					// lt객체 새로 생성
					LoginTemp ltlogin = new LoginTemp();
					System.out.println(ltlogin + "created new lt object");// test printout
					// get return val of type lt from logincheck() method
					ltlogin = loginCheck(lt.getId(), lt.getPassword(), client); // lt객체에
					// 가지고오기 (lt클래스리턴값임)
					System.out.println(ltlogin.getId() + "haha");
					System.out.println("ran loginCheck method and brought lt object ");// test printout
					oos.writeObject(ltlogin.givePermission);
					oos.flush();
					oos.reset();
					
					//Ingame userid 출력을 위해 uvo 내보내기... login 하면서
					//UserVO값을 보내기
					UserDAO udao = new UserDAO();
					UserVO uvo = new UserVO();
					uvo = udao.selectOne(ltlogin.getId(), ltlogin.getPassword());
					oos.writeObject(uvo);
					oos.flush();
					oos.reset();
					
					System.out.println("sent givePermission object" + ltlogin.getId() + "hahahaahaha");// test printout

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return type;
//**********************REGISTER DISTINGUISH***************************************************
			} else if (objInitial instanceof UserVO) {
				System.out.println("register attempt");
				// registering!
				uvo = (UserVO) objInitial;

				UserVO voR = registerUser(uvo);

				if (voR.getUserID().equals(uvo.getUserID()) && voR.getUserPW().equals(uvo.getUserPW())) {
					System.out.println("Register successful!");
					oos.writeObject(voR);
					oos.flush();
					oos.reset();
					System.out.println("sent data voR!");
				}
//**********************CHAT DISTINGUISH***************************************************
			} else if (objInitial instanceof ChatLine) {
				System.out.println("inside of chatline distinguished elseif statement block");
				ChatLine cl = (ChatLine) objInitial;
				System.out.println(cl.getMsg() + cl.getUserID()); // check for existence of id and msg
				String message = cl.getMsg();
				System.out.println(cl.getUserID() + " : " + message);

				System.out.println("before seding msg object to all connected clients");
				for (GameServer x : list) {
					x.oos.writeObject(cl.getUserID() + " :  " + message);
					System.out.println("msg broadcasted");
					x.oos.flush();
					x.oos.reset();
//					x.oos.close();
				}
			} else if (objInitial instanceof Integer) {
				udao = new UserDAO();
				uvo = udao.selectOne(lt.getId(), lt.password);
				
				System.out.println("CHECKING IF uvo EXISTS and CONTAINS INFO");
				System.out.println("ID : " + uvo.getUserID());
				System.out.println("Rating : " + uvo.getRating());
				System.out.println("Wins : " + uvo.getWin());
				System.out.println("Losses : " + uvo.getLoss());
				
				oos.writeObject(uvo);
				oos.flush();
				oos.reset();

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return type;
	}

	public static void main(String[] args) {
		MainServer ms = new MainServer();
	}

//	private void gameServerSet() {
//		Socket client = null;
//		try {
//			ssGame = new ServerSocket(6000);
//
//			while (true) {
//				client = ssGame.accept();
//				InGameServer igs = new InGameServer(client);
//				inGameList.add(igs);
//				System.out.println("Connected to gameServer \t" + client);
//				igs.start();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	class InGameServer extends Thread { // individual thread per user
//		Socket client;
//		ObjectOutputStream oos2;
//		ObjectInputStream ois2;
//
//		InGameServer(Socket client) {
//			this.client = client;
//			ip = client.getInetAddress().getHostAddress(); // cs연결된훈 ip변수에 clientip저장
//
//			try {
//				oos2 = new ObjectOutputStream(client.getOutputStream()); // oos 생성
//				ois2 = new ObjectInputStream((client.getInputStream())); // ois 생성
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		@Override
//		public void run() {
//			// UVO객체를 client로부터 받아온다 --> client는 server로 uvo를 로그인하면서 던저준다
//			// uvo라는 객체의 .getUserID로 clientIP와 hashmap로 key,value식으로 만들어준다.
//			// 던저준 패킷 받기
//			System.out.println("In Game Server........"); // test printout
//
//			while (true) {
//				try {
//					objInitial = ois2.readObject(); // object 상대방에게 넘겨줌
//					distinguishInGameObjects(objInitial, oos2, ois2, client);
//
//				} catch (IOException e) {
//					list.remove(this);
////					list.remove(list.indexOf(client));
//					e.printStackTrace();
//					break;
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//
//		// QuickGame실행이 되고 client에서 받은 값으로 distinguish1단계하고 inGameServer SOCKET으로
//		// redirect해주기.
//		private void distinguishInGameObjects(Object objInitial, ObjectOutputStream oos2, ObjectInputStream ois2, Socket client2) {
//			try {
//				
//			
//			GameStatus gst = (GameStatus) objInitial;
//			System.out.println("Server) Flag :::: " + gst.getFlag() + " cnt ::: " +cnt);
//			if(gst.myturn==0) { // 차례 정해주기
//				if(cnt%2 == 1) {
//					System.out.println("SERVER) set gst to 1 as p1...");
//					gst.setMyturn(1); // 선공
//					cnt++;
//					System.out.println("Server) 선공입니다.");
//					System.out.println("SERVER) inGameList ADDED");
//				}
//				else if(cnt%2 == 0) {
//					System.out.println("SERVER) set gst to 1 as p2...");
//					gst.setMyturn(2); // 후공
//					cnt++;
//					System.out.println("Server) 후공입니다.");
//					System.out.println("SERVER) inGameList ADDED");
//				}
//			}
//			
//			for(InGameServer x : inGameList) { // gst를 broadcast
//				System.out.println("Sending GST....");
//				x.oos2.writeObject(gst);
//				x.oos2.flush();
//				x.oos2.reset();
//				System.out.println("SENT GST");
//			}
//			}catch(IOException e) {
//				inGameList.remove(this);
//				e.printStackTrace();
//				break;
//			}
//				
//			}
//			
//		}
}
