package filters;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import common.ApplicationVariables;

public class AuthenticationFilter extends HttpFilter implements Filter {

    private static final long serialVersionUID = 1L;

	public AuthenticationFilter() {
        super();
    }

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		System.out.println("authentication filter called");
		
		JSONObject responseObject = new JSONObject();
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		Cookie[] cookies = httpRequest.getCookies();
		boolean validated = false;
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("SessionId")) {
				String sessionId = cookie.getValue();
				try {
					PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from sessions where SessionId = ?");
					ps.setString(1, sessionId);
					ResultSet rs = ps.executeQuery();
					if(rs.next()) {
						request.setAttribute("loggedInUserId", rs.getString(1));
						ps = ApplicationVariables.dbConnection.prepareStatement("select Role from users where Email = ?");
						ps.setString(1, rs.getString(1));
						ResultSet rs2 = ps.executeQuery();
						if(rs2.next()) {
							Cookie c = new Cookie("Role", rs2.getString(1));
							httpResponse.addCookie(c);
						}
						validated = true;
						break;
					}
					else {
						responseObject.put("StatusCode", 401);
						responseObject.put("Message", "UnAuthenticated user!");
					}
				} 
				catch (SQLException e) {
					responseObject.put("StatusCode", 500);
					responseObject.put("Message", "SQL Exception accurred!");
					System.out.println(e.getMessage()); 
				}
				break;
			}
			else {
				responseObject.put("StatusCode", 401);
				responseObject.put("Message", "UnAuthenticated user!");
			}
		}
		if(validated) {
			chain.doFilter(request, httpResponse);
		}
		else {
			httpResponse.getWriter().append(responseObject.toString());
		}		
	}
}
