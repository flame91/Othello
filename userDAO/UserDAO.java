package userDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO {
	//0. ���� ���� -> �⺻�����ڿ��� Connection ��ü ����
	
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@orcl.cstvm8b2wjsr.ap-northeast-2.rds.amazonaws.com:1521:orcl";
		String user = "scott";
		String password = "TigerTigerr!";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		
		public UserDAO() { //connection������ �����ڸ� ���� �������.
			//1. JDBC ����̹� �ε�
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, user, password);
				System.out.println("conn : " + conn);
			} catch (ClassNotFoundException e) {
				System.out.println("����̹� �ε�����");
			} catch (SQLException e) {
				System.out.println("DB���� ����");
				e.printStackTrace();
			}
			
		}//Constructor END
		
		
		//select * from userdata table�� ���� �޼���
		public ArrayList<UserVO> selectAll(){ //����Ÿ���� generic type (UserVO)�� array list --> list ��������
			
			ArrayList<UserVO> list = new ArrayList<UserVO>();
			
			//3. DB�� ����� SQL���� ��ü
			sb.setLength(0);
			sb.append("SELECT * FROM USERDATA");
			
			try {
				//4. ���� ��ü ����
				pstmt = conn.prepareStatement(sb.toString());
				//5. ����: ���������� -> ResultSet�̶�� ��. -->SELECT���� ResultSet���� �޾ƿ��� DML�� �׷��� �ʾƵ� ��.
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					UserVO uvo = new UserVO();
					uvo.setUserID(rs.getString("userID"));
					uvo.setUserPW(rs.getString("userPW"));
					uvo.setLastName(rs.getString("LastName"));
					uvo.setFirstName(rs.getString("FirstName"));
					uvo.setuemail(rs.getString("EMAIL"));
					uvo.setWin(rs.getInt("Wins"));
					uvo.setLoss(rs.getInt("Losses"));
					uvo.setRating(rs.getInt("RATING"));
					//UserVO generic type �� array list�� ��� �� �����ϱ�
					list.add(uvo);
					
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return list;
		}
		
		
		//Select One profile
		public UserVO selectOne(String userID, String userPW) {
			UserVO vo = null;
			//reset sb
			sb.setLength(0);
			sb.append("SELECT * FROM USERDATA WHERE USERID = ? AND USERPW = ?");
			
			try {
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, userID);
				pstmt.setString(2, userPW);
				rs = pstmt.executeQuery();
				
				rs.next();
				vo = new UserVO();
				
				vo.setUserID(rs.getString("USERID"));
				vo.setUserPW(rs.getString("USERPW"));
				vo.setLastName(rs.getString("LASTNAME"));
				vo.setFirstName(rs.getString("FIRSTNAME"));
				vo.setuemail(rs.getString("email"));
				vo.setWin(rs.getInt("WINS"));
				vo.setLoss(rs.getInt("LOSSES"));
				vo.setRating(rs.getInt("RATING"));
				System.out.println(vo.getUserID() + vo.getUserPW());
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return vo;
		}
		
		
		//Create new player profile
		
		public void register(UserVO vo) {
			sb.setLength(0);
			sb.append("INSERT INTO userdata ");
			sb.append("VALUES (? , ?, ?, ?, ?, ?, ?, ?)");
			
			try {
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, vo.getUserID());
				pstmt.setString(2, vo.getUserPW());
				pstmt.setString(3, vo.getLastName());
				pstmt.setString(4, vo.getFirstName());
				pstmt.setString(5, vo.getuemail());
				pstmt.setInt(6, vo.getWin());
				pstmt.setInt(7, vo.getLoss());
				pstmt.setInt(8, vo.getRating());
				
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
}
