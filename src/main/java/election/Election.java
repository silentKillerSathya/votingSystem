package election;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import common.ApplicationVariables;
import enums.ElectionType;
import enums.Status;

public class Election {

	int id;
	String name;
	String description;
	Date creationDate;
	Date electionDate;
	Time startTime;
	Time endTime;
	Date resultDate;
	Time resultTime;
	int candidatesCount;
	ElectionType type;
	Status status;
	String votersListFilePath;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getElectionDate() {
		return electionDate;
	}

	public void setElectionDate(Date electionDate) {
		this.electionDate = electionDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public Date getResultDate() {
		return resultDate;
	}

	public void setResultDate(Date resultDate) {
		this.resultDate = resultDate;
	}

	public Time getResultTime() {
		return resultTime;
	}

	public void setResultTime(Time resultTime) {
		this.resultTime = resultTime;
	}

	public int getCandidatesCount() {
		return candidatesCount;
	}

	public void setCandidatesCount(int candidatesCount) {
		this.candidatesCount = candidatesCount;
	}

	public ElectionType getType() {
		return type;
	}

	public void setType(ElectionType type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getVotersListFilePath() {
		return votersListFilePath;
	}

	public void setVotersListFilePath(String votersListFilePath) {
		this.votersListFilePath = votersListFilePath;
	}

	Election(){}
	
	public Election(int id, String name, String description, Date creationDate, Date electionDate, String startTime,
			String endTime, Date resultDate, String resultTime, int candidatesCount, ElectionType type, Status status) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		
		Date stTime = null;
		Date eTime = null;
		Date rTime = null;
		try {
			stTime = dateFormat.parse(startTime);
			eTime = dateFormat.parse(endTime);
	        rTime = dateFormat.parse(resultTime);
		} 
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}       
        
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.electionDate = electionDate;
		this.startTime = new Time(stTime.getTime());
		this.endTime = new Time(eTime.getTime());
		this.resultDate = resultDate;
		this.resultTime = new Time(rTime.getTime());
		this.candidatesCount = candidatesCount;
		this.type = type;
		this.status = status;
	}
	
	public boolean createElection(String path) {		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String currDate = sdf.format(date);
		Date currentDate = null;
		
		try {
			currentDate = (Date) sdf.parse(currDate);
		} 
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("insert into election (Name, Description, CreationDate, ElectionDate, StartTime, EndTime, ResultDate, ResultTime, CandidatesCount, Type, Status, VotersList) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, name);
			ps.setString(2, description);
			ps.setDate(3, new java.sql.Date(currentDate.getTime()));
			ps.setDate(4, new java.sql.Date(electionDate.getTime()));
			ps.setTime(5, startTime);
			ps.setTime(6, endTime);
			ps.setDate(7, new java.sql.Date(resultDate.getTime()));
			ps.setTime(8, resultTime);
			ps.setInt(9, candidatesCount);
			ps.setString(10, type.toString());
			ps.setString(11, status.toString());
			ps.setString(12, path);
			ps.execute();	
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray sortElections(List<JSONObject> jsonList) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Comparator<JSONObject> comparator = null;
		comparator = (JSONObject json1, JSONObject json2) ->  {
		    String status1 = json1.get("status").toString();
		    String status2 = json2.get("status").toString();
		    if (status1.equals("Active") && status2.equals("Active")) {
		        Date date1 = null;
		        Date date2 = null;
		        try {
		            date1 = sdf.parse((String) json1.get("electionDate"));
		            date2 = sdf.parse((String) json2.get("electionDate"));
		        } catch (ParseException e) {
		            System.out.println(e.getMessage());
		        }
		        return date2.compareTo(date1);
		    } 
		    else {
		        return 1;
		    }
		};
		Collections.sort(jsonList, comparator);
		JSONArray jsonArray = new JSONArray();
		for(int i=jsonList.size()-1; i>=0; i--) {
			jsonArray.add(jsonList.get(i));
		}
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray showAllElections() {
		
		List<JSONObject> jsonList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Statement st = ApplicationVariables.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("select * from election");

			while(rs.next()) {
			      JSONObject json = new JSONObject();
			      json.put("electionId", rs.getInt(1));
			      json.put("name", rs.getString(2));
			      json.put("description", rs.getString(3));
			      json.put("creationDate", sdf.format(rs.getDate(4)));
			      json.put("electionDate", sdf.format(rs.getDate(5)));
			      json.put("startTime", rs.getTime(6).toString());
			      json.put("endTime", rs.getTime(7).toString());
			      json.put("resultDate", sdf.format(rs.getDate(8)));
			      json.put("resultTime", rs.getTime(9).toString());
			      json.put("candidatesCount", rs.getInt(10));
			      json.put("electionType", rs.getString(11));
			      json.put("status", rs.getString(12));
			      jsonList.add(json);
			}	
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		JSONArray jsonArray = sortElections(jsonList);
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray showOnlyPoliticalElections() {
		List<JSONObject> jsonList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from election where Type = ?");
			ps.setString(1, ElectionType.Political.toString());
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JSONObject json = new JSONObject();
			    json.put("electionId", rs.getInt(1));
			    json.put("name", rs.getString(2));
			    json.put("description", rs.getString(3));
			    json.put("creationDate", sdf.format(rs.getDate(4)));
			    json.put("electionDate", sdf.format(rs.getDate(5)));
			    json.put("startTime", rs.getTime(6).toString());
			    json.put("endTime", rs.getTime(7).toString());
			    json.put("resultDate", sdf.format(rs.getDate(8)));
			    json.put("resultTime", rs.getTime(9).toString());
			    json.put("candidatesCount", rs.getInt(10));
			    json.put("electionType", rs.getString(11));
			    json.put("status", rs.getString(12));
			    jsonList.add(json);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		JSONArray jsonArray = sortElections(jsonList);
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray showOnlyOrganizationalElections() {
		List<JSONObject> jsonList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from election where Type = ?");
			ps.setString(1, ElectionType.Organizational.toString());
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JSONObject json = new JSONObject();
			    json.put("electionId", rs.getInt(1));
			    json.put("name", rs.getString(2));
			    json.put("description", rs.getString(3));
			    json.put("creationDate", sdf.format(rs.getDate(4)));
			    json.put("electionDate", sdf.format(rs.getDate(5)));
			    json.put("startTime", rs.getTime(6).toString());
			    json.put("endTime", rs.getTime(7).toString());
			    json.put("resultDate", sdf.format(rs.getDate(8)));
			    json.put("resultTime", rs.getTime(9).toString());
			    json.put("candidatesCount", rs.getInt(10));
			    json.put("electionType", rs.getString(11));
			    json.put("status", rs.getString(12));
			    jsonList.add(json);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		JSONArray jsonArray = sortElections(jsonList);
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray showOnlyActiveElections() {
		
		System.out.println("active elec called");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<JSONObject> jsonList = new ArrayList<>();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from election where Status = ?");
			ps.setString(1, "Active");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JSONObject json = new JSONObject();
			    json.put("electionId", rs.getInt(1));
			    json.put("name", rs.getString(2));
			    json.put("description", rs.getString(3));
			    json.put("creationDate", sdf.format(rs.getDate(4)));
			    json.put("electionDate", sdf.format(rs.getDate(5)));
			    json.put("startTime", rs.getTime(6).toString());
			    json.put("endTime", rs.getTime(7).toString());
			    json.put("resultDate", sdf.format(rs.getDate(8)));
			    json.put("resultTime", rs.getTime(9).toString());
			    json.put("candidatesCount", rs.getInt(10));
			    json.put("electionType", rs.getString(11));
			    json.put("status", rs.getString(12));
			    jsonList.add(json);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		JSONArray jsonArray = sortElections(jsonList);
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray showOnlyArchivedElections() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		JSONArray jsonArray = new JSONArray();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from election where Status = ?");
			ps.setString(1, "Archived");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JSONObject json = new JSONObject();
			    json.put("electionId", rs.getInt(1));
			    json.put("name", rs.getString(2));
			    json.put("description", rs.getString(3));
			    json.put("creationDate", sdf.format(rs.getDate(4)));
			    json.put("electionDate", sdf.format(rs.getDate(5)));
			    json.put("startTime", rs.getTime(6).toString());
			    json.put("endTime", rs.getTime(7).toString());
			    json.put("resultDate", sdf.format(rs.getDate(8)));
			    json.put("resultTime", rs.getTime(9).toString());
			    json.put("candidatesCount", rs.getInt(10));
			    json.put("electionType", rs.getString(11));
			    json.put("status", rs.getString(12));
			    jsonArray.add(json);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return jsonArray;
	}
	
	@SuppressWarnings({ "unchecked" })
	public JSONObject getElectionDetails(int electionId, ElectionType type) {
		
		JSONObject responseObject = new JSONObject();
		try {
			if(type.equals(ElectionType.Political)){
				PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from politicalElection where Id = ?");
				ps.setInt(1, electionId);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					responseObject.put("electionOf", rs.getString(2));
					responseObject.put("state", rs.getString(3));
					responseObject.put("taluk", rs.getString(4));
				}
			}
			else if(type.equals(ElectionType.Organizational)) {
				PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select Organization from organizationalElection where Id = ?");
				ps.setInt(1, electionId);
				ResultSet rs = ps.executeQuery();
				String organization = null;
				if(rs.next()) {
					organization = rs.getString(1);
				}
				
				ps = ApplicationVariables.dbConnection.prepareStatement("select * from organization where Name = ?");
				ps.setString(1, organization);
				rs = ps.executeQuery();
				if(rs.next()) {
					responseObject.put("organizationName", rs.getString(1));
					responseObject.put("organizer", rs.getString(2));
					responseObject.put("contactNo", rs.getString(3));
				}
			}	
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return responseObject;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject endElection(int id) {
		JSONObject responseObject = new JSONObject();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("update election set Status = ? where Id = ?");
			ps.setString(1, "Archived");
			ps.setInt(2, id);
			ps.execute();
			responseObject.put("StatusCode", 200);
			responseObject.put("Message", "Successfully election ended!!");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
			responseObject.put("StatusCode", 500);
			responseObject.put("Message", "SQL exception accured!");
		}
	    return responseObject;
	}
	
	public void updateElections() {
		
		System.out.println("update elec method called");
		long now = System.currentTimeMillis();
		java.sql.Date currDate = new java.sql.Date(now);
		java.sql.Date curr = java.sql.Date.valueOf(String.valueOf(currDate));
		java.sql.Time currTime = new java.sql.Time(now);
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("update election set Status = ? where ElectionDate < ?");
			ps.setString(1, "Archived");
			ps.setDate(2, curr);
			ps.executeUpdate();
			
			ps = ApplicationVariables.dbConnection.prepareStatement("update election set Status = ? where ElectionDate = ? and EndTime < ?");
			ps.setString(1, "Archived");
			ps.setDate(2, curr);
			ps.setTime(3, currTime);
			ps.executeUpdate();
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
