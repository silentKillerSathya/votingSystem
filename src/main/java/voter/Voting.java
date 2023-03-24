package voter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import candidate.Candidate;
import common.ApplicationVariables;
import election.Election;
import enums.ElectionType;
import enums.Status;

@WebServlet("/myapp/user/Voting")
public class Voting extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public Voting() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("voting servlet called");
		String voterId = request.getParameter("voterId");
		String candidateId = request.getParameter("candidateId");
		String eId = request.getParameter("electionId");
		int electionId = Integer.parseInt(eId+"");
	
		String name = null;
		String description = null;
		Date creationDate = null;
		Date electionDate = null;
		Time startTime = null;
		Time endTime = null;
		Date resultDate = null;
		Time resultTime = null;
		int candidatesCount = 0;
		ElectionType type = null;
		Status status = null;
		
		String candiName = null;
		Date dob = null;
		String candidatePhoto = null;
		String partyName = null;
		String info = null;
		String symbol = null;
		
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from election where Id = ?");
			ps.setInt(1, electionId);
			ResultSet rs =  ps.executeQuery();
			if(rs.next()) {
				name = rs.getString(2);
				description = rs.getString(3);
				creationDate = rs.getDate(4);
				electionDate = rs.getDate(5);
				startTime = rs.getTime(6);
				endTime = rs.getTime(7);
				resultDate = rs.getDate(8);
				resultTime = rs.getTime(9);
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
			ps = ApplicationVariables.dbConnection.prepareStatement("select * from candidate where Id = ? and ElectionId = ?");
			ps.setString(1, candidateId);
			ps.setInt(2, electionId);
			rs = ps.executeQuery();
			if(rs.next()) {
				candiName = rs.getString(1);
				dob = rs.getDate(2);
				candidatePhoto = rs.getString(4);
				info = rs.getString(5);
				partyName = rs.getString(6);
				symbol = rs.getString(7);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		Election election = new Election(electionId, name, description, creationDate, electionDate, startTime.toString(), endTime.toString(), resultDate, resultTime.toString(), candidatesCount, type, status);
		Candidate candidate = new Candidate(candiName, new java.sql.Date(dob.getTime()), candidatePhoto, candidateId, info, partyName, symbol, election);
		Vote vote = new Vote(voterId, election, candidate);
		
		JSONObject responseObject = vote.vote();
		
		response.getWriter().append(responseObject.toString());
	}
}
