package filters;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import org.json.simple.JSONObject;

public class ValidationFilterForSignup extends HttpFilter implements Filter {
  
    private static final long serialVersionUID = 1L;

	public ValidationFilterForSignup() {
        super();
    }

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String email = request.getParameter("email");

		JSONObject responseObject = new JSONObject();
		boolean validation = true;

		if(!userName.matches("^[^<>\\\\/]*$") || userName.equals("") || userName.equals(null)) {	
			validation = false;
			responseObject.put("Message", "Invalid User name!");	
		}
		if(!password.matches("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) {
			validation = false;
			responseObject.put("Message", "Invalid Password!");
		}
		if(!email.matches(".+@gmail\\.com")) {
			validation = false;
			responseObject.put("Message", "Invalid Email!");
		}
		
		if(!validation) {
			responseObject.put("StatusCode", 400);
			response.getWriter().append(responseObject.toString());
		}
		else {
			chain.doFilter(request, response);
		}	
	}
}
