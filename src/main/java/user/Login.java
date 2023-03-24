package user;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public Login() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("login servlet called");
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		User user = new User(null, email, password);		
		JSONObject responseObject = user.login();
		
		if((responseObject.get("StatusCode")).equals(200)) {
			String sessionId = (String) responseObject.get("sessionId");
			Cookie cookie = new Cookie("SessionId", sessionId);
			response.addCookie(cookie);
		}
		response.getWriter().append(responseObject.toString());
	}
}
