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
import enums.ElectionOf;
import enums.ElectionType;
import enums.Status;

@WebServlet("/myapp/admin/CreatePoliticalElection")
public class CreatePoliticalElection extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public CreatePoliticalElection() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("create pol elec called!");	
		JSONObject responseObject = new JSONObject();	
		String jsonInput = (String)request.getAttribute("json");
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(jsonInput);
		} 
		catch (org.json.simple.parser.ParseException e2) {
			System.out.println(e2.getMessage());
			responseObject.put("StatusCode","500");
			responseObject.put("Message","Error accured!");
		}
		String path = json.get("path").toString();
		String name = json.get("name").toString();
		String desc = json.get("desc").toString();
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
		Date electionDate = null;
		try {
			electionDate = df.parse((String)json.get("electionDate"));
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
			System.out.println();
		}
		String resultTime = json.get("resultTime").toString();
		
		String eleOf = json.get("electionOf").toString();
		ElectionOf electionOf = null;
		if(eleOf.equals("State")) {
			electionOf = ElectionOf.State;
		}
		else {
			electionOf = ElectionOf.Taluk;
		}
		String taluk = null;
		if(json.get("taluk")!=null) {
			taluk = json.get("taluk").toString();
		}	 
		String state = json.get("state").toString();	
		PoliticalElection election = new PoliticalElection(0, name, desc, currentDate, electionDate, startTime, endTime, resultDate, resultTime, 0, ElectionType.Political, Status.Active, electionOf, state, taluk);
		responseObject = election.createPoliticalElection(path);
		response.getWriter().append(responseObject.toString());
	}
}

