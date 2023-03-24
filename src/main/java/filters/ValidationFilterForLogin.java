package filters;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import org.json.simple.JSONObject;

public class ValidationFilterForLogin extends HttpFilter implements Filter {
   
    private static final long serialVersionUID = 1L;

	public ValidationFilterForLogin() {
        super();
    }

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("login validation filter called");
	    
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		JSONObject responseObject = new JSONObject();
		
		if(!email.matches(".+@gmail\\.com")) {
			responseObject.put("StatusCode", 400);
			responseObject.put("Message", "Invalid Email!");
			response.getWriter().append(responseObject.toString());
		}
		if(!password.matches("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")){
			responseObject.put("StatusCode", 400);
			responseObject.put("Message", "Invalid Password!");
			response.getWriter().append(responseObject.toString());
		}
		else {
			chain.doFilter(request, response);
		}		
	}
}
