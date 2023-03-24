package election;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.json.simple.JSONObject;

import common.ApplicationVariables;
import enums.ElectionOf;
import enums.ElectionType;
import enums.Status;

public class PoliticalElection extends Election {

	ElectionOf electionOf;
	String state;
	String taluk;
	
	public PoliticalElection(int id, String name, String description, Date creationDate, Date electionDate,
			String startTime, String endTime, Date resultDate, String resultTime, int candidatesCount,
			ElectionType type, Status status, ElectionOf electionOf, String state, String taluk) {
		
		super(id, name, description, creationDate, electionDate, startTime, endTime, resultDate, resultTime,
				candidatesCount, type, status);

		this.electionOf = electionOf;
		this.state = state;
		this.taluk = taluk;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized JSONObject createPoliticalElection(String filePath) {		
		boolean isElectionCreated = createElection(filePath);
		JSONObject responseObject = new JSONObject();
		if(isElectionCreated) {
			try {
				
				PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select max(Id) from election");
				ResultSet rs = ps.executeQuery();
				rs.next();
				id = rs.getInt(1);
				
				ps = ApplicationVariables.dbConnection.prepareStatement("insert into politicalElection values (?, ?, ?, ?)");
				ps.setInt(1, id);
				ps.setString(2, electionOf.toString());
				ps.setString(3, state);
				ps.setString(4, taluk);
				ps.execute();
				responseObject.put("StatusCode", 200);
				responseObject.put("Message", "successfully election created!!");
			} 
			catch (SQLException e) {
				System.out.println(e.getMessage());
				responseObject.put("StatusCode", 500);
				responseObject.put("Message", "SQL exception accured!");
			}
		}
		else {
			responseObject.put("StatusCode", 500);
			responseObject.put("Message", "Some error accured!");
		}
		return responseObject;
	}
}
