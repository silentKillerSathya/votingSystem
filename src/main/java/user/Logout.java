package user;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import common.ApplicationVariables;

@WebServlet("/myapp/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public Logout() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("logout servlet called");
		
		JSONObject responseObject = new JSONObject();
		Cookie[] cookies = request.getCookies();
		String sessionId = null;
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("SessionId")) {
				sessionId = cookie.getValue();
				try {
					PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("delete from sessions where SessionId = ?");
					ps.setString(1, sessionId);
					ps.executeUpdate();		
					cookie.setValue("");
					responseObject.put("StatusCode", 200);
					responseObject.put("Message", "Successfully logout!!");
				} 
				catch (SQLException e) {
					System.out.println(e.getMessage());
					responseObject.put("StatusCode", 500);
					responseObject.put("Message", "SQL exception accured!");
				}
			}
		}	
		response.getWriter().append(responseObject.toString());
	}
}
