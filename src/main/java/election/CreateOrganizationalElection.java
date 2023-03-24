package election;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import enums.ElectionType;
import enums.Status;


@WebServlet("/myapp/admin/CreateOrganizationalElection")
public class CreateOrganizationalElection extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public CreateOrganizationalElection() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("create org ele servlet called");
		
		JSONObject responseObject = new JSONObject();
		String jsonInput = (String) request.getAttribute("json");
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(jsonInput);
		} 
		catch (org.json.simple.parser.ParseException e1) {
			System.out.println(e1.getMessage());
			responseObject.put("StatusCode","500");
			responseObject.put("Message","Error accured!");
		}
		String path = (String)json.get("path");
		String name = json.get("electionName").toString();
		String desc = json.get("electionDesc").toString();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currDate = sdf.format(date);
		Date currentDate = null;
		try {
			currentDate = sdf.parse(currDate);
		} 
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		Date elecDate = null;
		try {
			elecDate = df.parse((String)json.get("electionDate"));
		} 
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		String startTime = json.get("startTime").toString();
		String endTime = json.get("endTime").toString();
		Date resultDate = null;
		try {
			resultDate = df.parse((String)json.get("resultDate"));
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		String resultTime = json.get("resultTime").toString();
		
		String organizationName = json.get("organization").toString();
		String organizer = json.get("organizer").toString();
		String contactNo = json.get("organizerPh").toString();
		
		OrganizationalElection election = new OrganizationalElection(0, name, desc, currentDate, elecDate, startTime,
				endTime, resultDate, resultTime, 0, ElectionType.Organizational, Status.Active,
				organizationName, organizer, contactNo);
		
		responseObject = election.createOrganizationalElection(path);
		response.getWriter().append(responseObject.toString());
	}
}
