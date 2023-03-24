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

@WebServlet("/FindRole")
public class FindRole extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public FindRole() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		String sessionId = null;
		String user = null;
		String role = null;
		JSONObject responseObject = new JSONObject();
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("SessionId")) {
				sessionId = cookie.getValue();
				break;
			}
		}
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select User from sessions where SessionId = ?");
			ps.setString(1, sessionId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				user = rs.getString(1);
			}
			ps = ApplicationVariables.dbConnection.prepareStatement("select Role from users where Email = ?");
			ps.setString(1, user);
			rs = ps.executeQuery();
			if(rs.next()) {
				role = rs.getString(1);
			}
			responseObject.put("StatusCode", 200);
			responseObject.put("Role", role);
		} 
		catch (SQLException e) {
			responseObject.put("StatusCode", 401);
			responseObject.put("Message", "UnAuthenticated user!!");
		}
		response.getWriter().append(responseObject.toString());
	}
}
