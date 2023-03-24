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
import javax.servlet.http.HttpFilter;
import org.json.simple.JSONObject;

import common.ApplicationVariables;

public class AuthorizationFilterForAdmin extends HttpFilter implements Filter {
    
    private static final long serialVersionUID = 1L;

	public AuthorizationFilterForAdmin() {
        super();
    }

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		System.out.println("authori filter for admin called!");
		JSONObject responseObject = new JSONObject();
		String loggedInUser = (String) request.getAttribute("loggedInUserId");
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select Role from users where Email = ?");
			ps.setString(1, loggedInUser);
			ResultSet rs = ps.executeQuery();		
			rs.next();
			String role = rs.getString(1);	
			if(role.equals("Admin")) {
				chain.doFilter(request, response);
			}
			else {
				responseObject.put("statusCode", 400);
				responseObject.put("message", "You don't have enough permission");
				response.getWriter().append(responseObject.toString());
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
			responseObject.put("statusCode", 500);
			responseObject.put("message", "Unexpected error occured. Please contact system administrator.");
			response.getWriter().append(responseObject.toString());
		}	
		
	}
}
