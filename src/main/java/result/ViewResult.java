package result;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import common.ApplicationVariables;

@WebServlet(urlPatterns = {"/myapp/admin/ViewResult", "/myapp/user/ViewResult"})
public class ViewResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public ViewResult() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("view result servlet called");
		
		String id = request.getParameter("electionId");
		int electionId = Integer.parseInt(""+id);
		List<JSONObject> list = new ArrayList<>();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from candidate where ElectionId = ?");
			ps.setInt(1, electionId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JSONObject json = new JSONObject();
				String candidateId = rs.getString(3);
				json.put("candidateName", rs.getString(1));
				json.put("photo", rs.getString(4));
				json.put("party", rs.getString(6));
				json.put("symbol", rs.getString(7));
				ps = ApplicationVariables.dbConnection.prepareStatement("select * from votes where ElectionId = ? and CandidateId = ?");
				ps.setInt(1, electionId);
				ps.setString(2, candidateId);
				ResultSet rs2 = ps.executeQuery();
				int votes = 0;
				if(rs2.next()) {
					votes = 1;
					while(rs2.next()) {
						votes++;
					}
				}
				json.put("votes", votes);
				list.add(json);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		Comparator<JSONObject> comparator = null;
		comparator = (JSONObject json1, JSONObject json2) ->  {
		    String count1 = json1.get("votes").toString();
		    int votes1 = Integer.parseInt(count1);
		    String count2 = json2.get("votes").toString();
		    int votes2 = Integer.parseInt(count2);
		    return (votes1-votes2);
		};
		Collections.sort(list, comparator);
		JSONArray jsonArray = new JSONArray();
		for(int i=list.size()-1; i>=0; i--) {
			jsonArray.add(list.get(i));
		}
		System.out.println(jsonArray);
		response.getWriter().append(jsonArray.toString());
	}
}
