package filters;
import java.io.BufferedReader;
import org.apache.logging.log4j.LogManager;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SstDocument;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import common.ApplicationVariables;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;

public class ValidationForCandidate extends HttpFilter implements Filter {
 
    private static final long serialVersionUID = 1L;

	public ValidationForCandidate() {
        super();
    }
	
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		System.out.println("validation for add candidate servlet called");
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
		catch (ParseException e) {
			System.out.println(e.getMessage());
			responseObject.put("StatusCode", "500");
			responseObject.put("Message","Error accured!");
		}	
		
		String id = ""+json.get("electionId");
		int elecId = Integer.parseInt(id);
		String type = null;
		String votersList = null;
		String dob = (String) json.get("candidateDob");
		Date candidateDOB = Date.valueOf(dob);
		try {
			PreparedStatement ps = ApplicationVariables.dbConnection.prepareStatement("select Type, VotersList from election where Id = ?");
			ps.setInt(1, elecId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				type = rs.getString(1);
				votersList = rs.getString(2);
			}
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		Map<String, List<String>> voters = new HashMap<>();
		System.out.println(votersList);
        try (FileInputStream inputStream = new FileInputStream(votersList);
            Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
            	List<String> list = new ArrayList<>();
                for (Cell cell : row) {     
                	list.add(cell.getStringCellValue());
                    System.out.print(cell.getStringCellValue() + "\t");
                }
                voters.put(list.get(0), list);
                System.out.println();
            }
        }        
        catch (IOException e) {
            System.out.println(e.getMessage());
        } 
        catch (EncryptedDocumentException e) {
			System.out.println(e.getMessage());
		} 
    
        String candidateId = (String) json.get("id");
        String candidateName = (String) json.get("candidateName");
        boolean validVoter = false;
        System.out.println(voters);
        if(voters.containsKey(candidateId)) {
        	if(voters.get(candidateId).contains(candidateName)) {
        		validVoter = true;
        	}
        }
        if(validVoter) {
        	if(type.equals("Political")) {
        		long birthMillSec = candidateDOB.getTime();
        		long age = (( System.currentTimeMillis() - birthMillSec) / 86400000)/365;
        		
        		System.out.println(age);
        		if(age < 25 && age > 65) {
        			responseObject.put("StatusCode","400");
        			responseObject.put("Message","Candidate age was not worthy! It has must 25 to 65!");
        			response.getWriter().append(responseObject.toString());
        		}
        		else {
        			request.setAttribute("json", json.toString());
                	chain.doFilter(request, response);
        		}
        	}
        	else {
        		request.setAttribute("json", json.toString());
            	chain.doFilter(request, response);
        	}
        }
        else {
        	responseObject.put("StatusCode","400");
			responseObject.put("Message","This person was not a voter of this election!");
			response.getWriter().append(responseObject.toString());
        }      
	}
}
