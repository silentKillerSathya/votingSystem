package voter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import common.ApplicationVariables;

@WebServlet("/myapp/user/ValidationForVoter")
public class ValidationForVoter extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public ValidationForVoter() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("ValidationForVoter servlet called!!");
		
		String id = request.getParameter("electionId");
		String voterId = request.getParameter("voterId");
		int electionId = Integer.parseInt(""+id);
		JSONObject responseObject = new JSONObject();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from votes where Voter = ? and ElectionId = ?");
			ps.setString(1, voterId);
			ps.setInt(2, electionId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println("uuuuuuu");
				responseObject.put("StatusCode", 400);
				responseObject.put("Message", "This voter was already voted in this election!");			
			}
			else {
				System.out.println("vvvvvvvv");
				Vote vote = new Vote();
				JSONArray jsonArray = vote.viewCandidatesForVote(electionId);
				responseObject.put("StatusCode", 200);
				responseObject.put("Message", "valid voter!!");
				responseObject.put("JsonArray", jsonArray.toString());
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		response.getWriter().append(responseObject.toString());
	}
}
