package candidate;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import common.ApplicationVariables;
import election.Election;
import enums.ElectionType;
import enums.Status;

@WebServlet("/myapp/admin/AddCandidate")
public class AddCandidate extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public AddCandidate() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("add candidate servlet called");
		
		JSONObject responseObject = new JSONObject();
        String json = (String) request.getAttribute("json");
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = null;
        try {
			jsonObj = (JSONObject) parser.parse(json);
		} 
        catch (ParseException e) {
			System.out.println(e.getMessage());
		}
        catch(Exception ex) {
        	System.out.println(ex.getMessage());
        }
    
        String name = (String) jsonObj.get("candidateName");
        String dob = (String) jsonObj.get("candidateDob");
        String id = (String) jsonObj.get("id");
        String info = (String) jsonObj.get("info");
        String imgpath = (String) jsonObj.get("candidateImg"); 
        String party = (String) jsonObj.get("party");
        String symbolPath = (String) jsonObj.get("symbolImg");
        String eId = ""+jsonObj.get("electionId");
        int electionId = Integer.parseInt(eId);
     
        Date CandidateDOB = Date.valueOf(dob);
		
        int eleId = 0;
    	String eleName = null;
    	String description = null;
    	Date creationDate = null;
    	Date electionDate = null;
    	String startTime = null;
    	String endTime = null;
    	Date resultDate = null;
    	String resultTime = null;
    	int candidatesCount = 0;
    	ElectionType type = null;
    	Status status = null;
    	
        try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from election where Id = ?");
			ps.setInt(1, electionId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				eleId = rs.getInt(1);
				eleName = rs.getString(2);
				description = rs.getString(3);
				creationDate = rs.getDate(4);
				electionDate = rs.getDate(5);
				startTime = rs.getTime(6).toString();
				endTime = rs.getTime(7).toString();
				resultDate = rs.getDate(8);
				resultTime = rs.getTime(9).toString();
				candidatesCount = rs.getInt(10);
				if(rs.getString(11).equals("Political")) {
					type = ElectionType.Political;
				}
				else {
					type = ElectionType.Organizational;
				}
				if(rs.getString(12).equals("Active")) {
					status = Status.Active;
				}
				else {
					status = Status.Archived;
				}
			}
		} 
        catch (SQLException e) {
			System.out.println(e.getMessage());
		}
        
        Election election = new Election(eleId, eleName, description, creationDate, electionDate, startTime, endTime, resultDate, resultTime, candidatesCount, type, status);
        
        Candidate candidate = new Candidate(name, CandidateDOB, imgpath, id, info, party, symbolPath, election);
        
        responseObject = candidate.addCandidate();
        
        response.getWriter().append(responseObject.toString());
	}
}
