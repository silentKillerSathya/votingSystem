package voter;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import candidate.Candidate;
import common.ApplicationVariables;
import election.Election;
import enums.ElectionType;

public class Vote {

	String voterId;
	Election election;
	Candidate candidate;
	
	static Map<String, String> otpList = new HashMap<>();
	
	public Vote(){}
	
	public Vote(String voterId, Election election, Candidate candidate) {
		this.voterId = voterId;
		this.election = election;
		this.candidate = candidate;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray checkValidVoter(ElectionType type, String voterId) {
		
		System.out.println("checkValidVoter method called");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		long millis = System.currentTimeMillis();
		Date currentDate = new Date(millis);
		currentDate = Date.valueOf(String.valueOf(currentDate));
		JSONArray jsonArray = new JSONArray();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select * from election where Type = ? and ElectionDate = ? and CURRENT_TIME BETWEEN StartTime and EndTime and Status = ?");
			ps.setString(1, type.toString());
			ps.setDate(2, currentDate);
			ps.setString(3, "Active");
			ResultSet rs = ps.executeQuery();
			String votersList = null;
			
			while(rs.next()) {
				votersList = rs.getString(13);
				JSONObject jsonObj = new JSONObject();
				try (FileInputStream inputStream = new FileInputStream(votersList);
			            Workbook workbook = WorkbookFactory.create(inputStream)) {
			            Sheet sheet = workbook.getSheetAt(0);
			            out:
			            for (Row row : sheet) {
			                for (Cell cell : row) {
			                	if(cell.getStringCellValue().equals(voterId)) {
			                		jsonObj.put("electionId", rs.getInt(1));
			                		jsonObj.put("name", rs.getString(2));
			                		jsonObj.put("description", rs.getString(3));
			                		jsonObj.put("creationDate", sdf.format(rs.getDate(4)));
			                		jsonObj.put("electionDate", sdf.format(rs.getDate(5)));
			                		jsonObj.put("startTime", rs.getTime(6).toString());
			                		jsonObj.put("endTime", rs.getTime(7).toString());
			                		jsonObj.put("resultDate", sdf.format(rs.getDate(8)));
			                		jsonObj.put("resultTime", rs.getTime(9).toString());
			                		jsonObj.put("candidatesCount", rs.getInt(10));
			                		jsonObj.put("electionType", rs.getString(11));
			                		jsonObj.put("status", rs.getString(12));
			                		jsonArray.add(jsonObj);
			                		break out;
			                	}     
			                }
			            }
			        }        
			        catch (IOException e) {
			            System.out.println(e.getMessage());
			        } 
			        catch (EncryptedDocumentException e) {
						System.out.println(e.getMessage());
					} 
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray viewCandidatesForVote(int electionId) {
		System.out.println("view candidates for vote method called");
		JSONArray jsonArray = new JSONArray();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select Name, Id, Photo, PartyName, Symbol from candidate where ElectionId = ?");
			ps.setInt(1, electionId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				JSONObject json = new JSONObject();
				json.put("candidateName", rs.getString(1));
				json.put("candidateId", rs.getString(2));
				json.put("candidateImg", rs.getString(3));
				json.put("party", rs.getString(4));
				json.put("symbol", rs.getString(5));
				jsonArray.add(json);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject vote() {
		System.out.println("vote method called");
		JSONObject responseObject = new JSONObject();
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("insert into votes values( ?, ?, ? )");
			ps.setString(1, this.voterId);
			ps.setInt(2, this.election.getId());
			ps.setString(3, this.candidate.getId());
			ps.execute();
			responseObject.put("StatusCode", 200);
			responseObject.put("Message", "Successfully voted!!");
		} 
		catch (SQLException e) {
			responseObject.put("StatusCode", 400);
			responseObject.put("Message", "Duplicate voter! You already vote this election!!");
			System.out.println(e.getMessage());
		}
		return responseObject;
	}
}


