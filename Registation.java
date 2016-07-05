import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

import java.sql.PreparedStatement;
import java.io.*;

public class Registation extends HttpServlet {

	private final DatabaseConnection openConnection = new DatabaseConnection();

		public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{

		Connection connect = null;
		PreparedStatement statement = null;
		InputStream inputStream = null;
		int checkInsertMember = 0;
		int checkInsertUser = 0;
		int user_id = 0;

		Part filePart = request.getPart("photo");

		if (filePart != null) {
		  System.out.println(filePart.getName());
		  System.out.println(filePart.getSize());
		  System.out.println(filePart.getContentType());

		  inputStream = filePart.getInputStream();
		}

		try {
			connect = openConnection.getDatabaseConnection();
			statement = connect.prepareStatement(
				"INSERT INTO User(user_username, user_password) VALUES (?, ?)");
			statement.setString(1,request.getParameter("username"));
			statement.setString(2,EncryptionPassword.encrypt(request.getParameter("password")));
			checkInsertUser = statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
		  connect = openConnection.getDatabaseConnection();
		  statement = connect.prepareStatement("SELECT * FROM user WHERE user_username=?");
			statement.setString(1,request.getParameter("username"));
			ResultSet result = statement.executeQuery();

			while (result.next()) {

				user_id = result.getInt("user_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	catch (Exception e) {
				e.printStackTrace();
		}

		try {
		  connect = openConnection.getDatabaseConnection();
		  statement = connect.prepareStatement(
			  "INSERT INTO Member(member_id,member_firstname,member_lastname,member_gender,member_address,member_mobile,member_email,member_picture,member_idLine,user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		  if (inputStream!=null) {
			statement.setString(1,request.getParameter("IDcard"));
			statement.setString(2,request.getParameter("firstname"));
			statement.setString(3,request.getParameter("lastname"));
			statement.setString(4,request.getParameter("gender"));
			statement.setString(5,request.getParameter("address"));
			statement.setString(6,request.getParameter("telephone"));
			statement.setString(7,request.getParameter("email"));
			statement.setBinaryStream(8, inputStream, (int) filePart.getSize());
			statement.setString(9,request.getParameter("lineID"));
			statement.setInt(10,user_id);
			checkInsertMember = statement.executeUpdate();
		  }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}



		if (checkInsertMember == 1 && checkInsertUser == 1) {
		  Member member = new Member();
		  member.setIdCard(request.getParameter("IDcard"));
		  member.setFirstName(request.getParameter("firstname"));
		  member.setLastName(request.getParameter("lastname"));
		  member.setEmail(request.getParameter("email"));
		  member.setPhoneNumber(request.getParameter("telephone"));
		  member.setAddress(request.getParameter("address"));
		  member.setLineId(request.getParameter("lineID"));
		  member.setGender(request.getParameter("gender"));

		  HttpSession session = request.getSession();
		  session.setAttribute("member",member);

		  RequestDispatcher rd = request.getRequestDispatcher("activate.jsp");
		  rd.forward(request, response);
		}
	}
}
