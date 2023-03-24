package voter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

@WebServlet("/myapp/user/CheckOTP")
public class CheckOTP extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("checkotp called");
		JSONObject responseObject = new JSONObject();
		String otp = request.getParameter("otp");
		String voterId = request.getParameter("voterId");
		boolean correctOTP = false;
		for(String voter : Vote.otpList.keySet()) {
			System.out.println(voter+" "+voterId);
			if(voter.equals(voterId)) {
				System.out.println(Vote.otpList.get(voter)+" "+otp);
				if(Vote.otpList.get(voter).equals(otp)) {
					correctOTP = true;
				}
			}
		}
		Vote.otpList.remove(voterId, otp);
		if(correctOTP) {
			responseObject.put("StatusCode", 200);
			responseObject.put("Message", "Voted successfully!!");
			response.getWriter().append(responseObject.toString());
		}
		else {
			responseObject.put("StatusCode", 400);
			responseObject.put("Message", "incorrect otp!");
			response.getWriter().append(responseObject.toString());
		}
	}
}
