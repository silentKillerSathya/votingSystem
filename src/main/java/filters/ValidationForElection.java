package filters;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ValidationForElection extends HttpFilter implements Filter {
 
    private static final long serialVersionUID = 1L;

	public ValidationForElection() {
        super();
    }

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	
		System.out.println("valid for elec called");			
		String input = "";
		String jsonInput = "";
		BufferedReader reader = request.getReader();
		JSONObject responseObject = new JSONObject();
		while((input=reader.readLine())!=null) {
			jsonInput += input;
		}
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
		String eDate = json.get("electionDate").toString();	
		long now = System.currentTimeMillis();
		Date today = new Date(now);
		Date electionDate = Date.valueOf(eDate);
		String startTime = json.get("startTime").toString();
		String endTime = json.get("endTime").toString();
        Time currentTime = new Time(now);
		Time start = Time.valueOf(startTime+":00");
		Time end = Time.valueOf(endTime+":00");		
        String resultDate = json.get("resultDate").toString();     
        String rTime = json.get("resultTime").toString();
        Time resultTime = Time.valueOf(rTime+":00");	
		Date result = Date.valueOf(resultDate);
		boolean validation = true;

		if(Date.valueOf(String.valueOf(today)).compareTo(electionDate)==1) {
			validation = false;
			responseObject.put("Message", "Election date is smaller than current date!");
		}
		else if(electionDate.compareTo(result)==0) {
			if(end.compareTo(resultTime)!=-1) {
				validation = false;
				responseObject.put("Message", "result time is lesser than end time");
			}
			else if(Date.valueOf(String.valueOf(today)).compareTo(electionDate)==0) {
	            if(currentTime.compareTo(start)!=1) {
	            	validation = false;
	            	responseObject.put("Message", "start time is smaller than current time!");
				}
	            else {
	            	if(start.compareTo(end)!=-1) {
	        			validation = false;
	        			responseObject.put("Message", "end time is lesser than start time");
	        		}
	        		else if(electionDate.compareTo(result)==1) {	
	        			validation = false;
	            		responseObject.put("Message", "result date is smaller than election date!");
	        		}	
	            }
			}
		}
		if(validation) {		
			responseObject.put("StatusCode", 200);
			request.setAttribute("json", json.toString());
			chain.doFilter(request, response);
		}
		else {
			responseObject.put("StatusCode", 400);
			response.getWriter().append(responseObject.toString());
		}
	}

}
