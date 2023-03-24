package user;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.json.simple.JSONObject;
import common.ApplicationVariables;

public class User {
	
	String username;
	String email;
	String password;
	
	User (String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject addUser() {
		
		System.out.println("signup method called");
		
		JSONObject responseObject = new JSONObject();		
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("insert into users values(?, ?, ?, ?)");
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setString(3, email);
			ps.setString(4, "User");
			ps.execute();
			
			UUID sessionId = UUID.randomUUID();
			ps = ApplicationVariables.dbConnection.prepareStatement("insert into sessions values(?, ?)");
			ps.setString(1, email);
			ps.setString(2, sessionId.toString());
			ps.executeUpdate();		
	
			responseObject.put("StatusCode", 200);
			responseObject.put("SessionId", sessionId.toString());
			responseObject.put("Message", "User added successfully!!");		
		} 
		catch (SQLException e) {
			responseObject.put("StatusCode", 500);
			responseObject.put("Message", "SQL Exception accured!");	
			System.out.println(e.getMessage());
		}		
		return responseObject;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject login() {
		
		System.out.println("login method called");	
		JSONObject responseObject = new JSONObject();
		
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select Role from users where Email = ? and Password = ?");
			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				String role = rs.getString(1);
				UUID sessionId = UUID.randomUUID();
				ps = ApplicationVariables.dbConnection.prepareStatement("insert into sessions values(?, ?)");
				ps.setString(1, email);
				ps.setString(2, sessionId.toString());
				ps.executeUpdate();		
				
				responseObject.put("StatusCode", 200);
				responseObject.put("Message", "Success! Correct User!!");
				responseObject.put("Role", role);
				responseObject.put("sessionId", sessionId.toString());
			}
			else {
				responseObject.put("StatusCode", 400);
				responseObject.put("Message", "Such an user is not found!");
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
			responseObject.put("StatusCode", 500);
			responseObject.put("Message", "SQL Exception accured!");
		}		
		return responseObject;
	}
}
