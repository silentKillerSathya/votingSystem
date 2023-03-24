package user;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import common.ApplicationVariables;

@WebServlet("/myapp/admin/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Admin() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("admin servlet called");
		Cookie[] cookies = request.getCookies();
		String sessionId = null;
		String userEmail = null;
		JSONObject responseObject = new JSONObject();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("SessionId")) {
				sessionId = cookie.getValue();
				break;
			}
		}
		System.out.println(sessionId);
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select User from sessions where SessionId = ?");
			ps.setString(1, sessionId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				userEmail = rs.getString(1);
			}
			System.out.println(userEmail);
			ps = ApplicationVariables.dbConnection.prepareStatement("select * from users where Email = ?");
			ps.setString(1, userEmail);
			rs = ps.executeQuery();
			if(rs.next()) {
				responseObject.put("name", rs.getString(1));
				responseObject.put("email", rs.getString(3));
			}
			System.out.println(responseObject);
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		response.getWriter().append(responseObject.toString());
	}
}
