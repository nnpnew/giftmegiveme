import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class Login extends HttpServlet {

	private final DatabaseConnection databaseConnection = new DatabaseConnection();

	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

	HttpSession session = request.getSession();
	RequestDispatcher dispatcher;

	Connection connection = null;
	PreparedStatement accountUserprepareStatement = null;

	try{
		connection = databaseConnection.getDatabaseConnection();
		accountUserprepareStatement = connection.prepareStatement(
			"SELECT * FROM user, member WHERE user_username = ? AND user_password = ? AND user.user_id = member.user_id");
		accountUserprepareStatement.setString(1, request.getParameter("username"));
		accountUserprepareStatement.setString(2, EncryptionPassword.encrypt(request.getParameter("password")));
		ResultSet resultSet = accountUserprepareStatement.executeQuery();

		if (resultSet.next()) {
			User user = new User();
			user.setUserId(resultSet.getInt("user_id"));
			user.setUsername(resultSet.getString("user_username"));
			user.setPassword(resultSet.getString("user_password"));
			session.setAttribute("user", user);

			Member member = new Member();
			member.setIdCard(resultSet.getString("member_id"));
			member.setFirstName(resultSet.getString("member_firstname"));
			member.setLastName(resultSet.getString("member_lastname"));
			member.setGender(resultSet.getString("member_gender"));
			member.setAddress(resultSet.getString("member_address"));
			member.setPhoneNumber(resultSet.getString("member_mobile"));
			member.setEmail(resultSet.getString("member_email"));
			member.setPicturePath(resultSet.getString("member_picture"));
			member.setLineId(resultSet.getString("member_idLine"));
			session.setAttribute("member", member);

			dispatcher = request.getRequestDispatcher("/home");
			dispatcher.forward(request, response);
		} else {
			dispatcher = request.getRequestDispatcher("help.jsp");
			dispatcher.forward(request, response);
		}
	} catch(SQLException se) {
      se.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
