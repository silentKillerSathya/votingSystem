package election;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.json.simple.JSONObject;

import common.ApplicationVariables;
import enums.ElectionType;
import enums.Status;

public class OrganizationalElection extends Election {

	Organization organization;
	
	public OrganizationalElection(int id, String name, String description, Date creationDate, Date electionDate,
			String startTime, String endTime, Date resultDate, String resultTime, int candidatesCount,
			ElectionType type, Status status, String organizationName, String organizerName, String contactNo) {
		
		super(id, name, description, creationDate, electionDate, startTime, endTime, resultDate, resultTime,
				candidatesCount, type, status);
	
		this.organization = new Organization(organizationName, organizerName, contactNo);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized JSONObject createOrganizationalElection(String path) {
		System.out.println(path+" %%");
		boolean isElectionCreated = createElection(path);
		JSONObject responseObject = new JSONObject();
		if(isElectionCreated) {
			try {
				PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("insert into organization values (?, ?, ?)");
				ps.setString(1, organization.organizationName);
				ps.setString(2, organization.organizerName);
				ps.setString(3, organization.contactNo);
				ps.execute();
				
				ps = ApplicationVariables.dbConnection.prepareStatement("select max(Id) from election");
				ResultSet rs = ps.executeQuery();
				rs.next();
				id = rs.getInt(1);
				
				ps = ApplicationVariables.dbConnection.prepareStatement("insert into organizationalElection values (?, ?)");
				ps.setInt(1, id);
				ps.setString(2, organization.organizationName);
				ps.execute();
				
				responseObject.put("StatusCode", 200);
				responseObject.put("Message", "Successfully election created!!");
			} 
			catch (SQLException e) {
				System.out.println(e.getMessage());
				responseObject.put("StatusCode", 500);
				responseObject.put("Message", "SQL exception accured!");
			}
		}	
		else {
			responseObject.put("StatusCode", 500);
			responseObject.put("Message", "some error accured!");
		}
		return responseObject;
	}	
}
