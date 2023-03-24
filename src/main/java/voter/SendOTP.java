package voter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import common.ApplicationVariables;

@WebServlet("/myapp/user/SendOTP")
public class SendOTP extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public SendOTP() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("sendotp called");
		String voterId = request.getParameter("voterId");
		JSONObject responseObject = new JSONObject();
		String sessionId = null;
		String userEmail = null;
		Cookie[] cookies = request.getCookies();
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
				userEmail = rs.getString(1);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		String API_KEY = "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";
		Random rand = new Random();
	    int otp = rand.nextInt(900000) + 100000;
	    System.out.println(otp);
		  try {
		       URL url = new URL("https://api.sendinblue.com/v3/smtp/email");

		       HttpURLConnection con = (HttpURLConnection) url.openConnection();
		       con.setRequestMethod("POST");
		       con.setRequestProperty("Content-Type", "application/json");
		       con.setRequestProperty("api-key", API_KEY);
		       con.setDoOutput(true);

		       String payload = "{\"sender\":{\"email\":\"sathya@gmail.com\"},\"to\":[{\"email\":\""+userEmail+"\"}],\"subject\":\"Dear user, your OTP for vote is : \",\"textContent\":\""+otp+"\"}";

		       OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
		       writer.write(payload);
		       writer.flush();
		       writer.close();

		       int responseCode = con.getResponseCode();
		       System.out.println(responseCode);
		       responseObject.put("StatusCode", responseCode);          
		  } 
		  catch (Exception e) {
		      System.out.println(e.getMessage());
		  }
		  Vote.otpList.put(voterId, otp+"");
		  response.getWriter().append(responseObject.toString());
	}
}
