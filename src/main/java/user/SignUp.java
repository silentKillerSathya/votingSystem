package user;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public SignUp() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("signup servlet called");
		
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		
		User user = new User(userName, password, email);
		JSONObject responseObject = user.addUser();
		
		if((Integer)responseObject.get("StatusCode")==(Integer)200) {
			String sessionId = (String) responseObject.get("SessionId");
			Cookie cookie = new Cookie("SessionId", sessionId);
			response.addCookie(cookie);	
		}	
		response.getWriter().append(responseObject.toString());
	}
}
