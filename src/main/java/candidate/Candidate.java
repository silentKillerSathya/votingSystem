package candidate;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import common.ApplicationVariables;
import election.Election;

public class Candidate {

	String name;
	Date dob;
	String id;
	String candidatePhoto;
	String partyName;
	String info;
	Election election;
	String symbol;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCandidatePhoto() {
		return candidatePhoto;
	}

	public void setCandidatePhoto(String candidatePhoto) {
		this.candidatePhoto = candidatePhoto;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Election getElection() {
		return election;
	}

	public void setElection(Election election) {
		this.election = election;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	Candidate(){}
	
	public Candidate(String name, Date dob, String candidatePhoto, String id, String info, String partyName, String symbol, Election election){
		this.name = name;
		this.dob = dob;
		this.id = id;
		this.candidatePhoto = candidatePhoto;
		this.info = info;
		this.partyName = partyName;
		this.symbol = symbol;
		this.election = election;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject addCandidate() {	
		System.out.println("add candidate method called");
		JSONObject responseObject = new JSONObject();	
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("insert into candidate values (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, name);
			ps.setDate(2, dob);
			ps.setString(3, id);
			ps.setString(4, candidatePhoto);
			ps.setString(5, info);
			ps.setString(6, partyName);
			ps.setString(7, symbol);
			ps.setInt(8, election.getId());
			ps.execute();
			
			ps = ApplicationVariables.dbConnection.prepareStatement("select CandidatesCount from election where Id = ?");
			ps.setInt(1, election.getId());
			ResultSet rs = ps.executeQuery();
			int candidatesCount = 0;
			if(rs.next()) {
				candidatesCount = rs.getInt(1);
			} 
			
			ps = ApplicationVariables.dbConnection.prepareStatement("update election set CandidatesCount = ? where Id = ?");
			ps.setInt(1, (candidatesCount+1));
			ps.setInt(2, election.getId());
			ps.executeUpdate();
			
			responseObject.put("StatusCode", 200);
			responseObject.put("Message", "Candidate added successfully!!");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
			responseObject.put("StatusCode", 500);
			responseObject.put("Message", "This candidate already exist in this election!");
		}
		return responseObject;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray viewCandidates(int electionId) {
		
		System.out.println("viewCandi method called");
		JSONArray jsonArray = new JSONArray();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from candidate where ElectionId = ?");
			ps.setInt(1, electionId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JSONObject json = new JSONObject();
				json.put("candidateName", rs.getString(1));
				json.put("dob", rs.getDate(2).toString());
				json.put("uniqId", rs.getString(3));
				json.put("photo", rs.getString(4));
				json.put("info", rs.getString(5));
				json.put("party", rs.getString(6));
				json.put("symbol", rs.getString(7));
				jsonArray.add(json);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject removeCandidate(String candidateId, int electionId) {
		JSONObject responseObject = new JSONObject();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("delete from candidate where Id = ?");
			ps.setString(1, candidateId);
			ps.executeUpdate();
			
			ps = ApplicationVariables.dbConnection.prepareStatement("select CandidatesCount from election where Id = ?");
			ps.setInt(1, electionId);
			ResultSet rs = ps.executeQuery();
			int candidatesCount = 0;
			if(rs.next()) {
				candidatesCount = rs.getInt(1);
			}
			
			ps = ApplicationVariables.dbConnection.prepareStatement("update election set CandidatesCount = ? where Id = ?");
			ps.setInt(1, (candidatesCount-1));
			ps.setInt(2, electionId);
			ps.executeUpdate();
			responseObject.put("StatusCode", 200);
			responseObject.put("Message", "Candidate removed successfully!!");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
			responseObject.put("StatusCode", 500);
			responseObject.put("Message", "As this voter already has votes in this election, he cannot be removed from the election!!");
		}
		return responseObject;
	}
}
