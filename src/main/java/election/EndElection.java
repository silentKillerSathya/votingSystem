package election;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

@WebServlet("/myapp/admin/EndElection")
public class EndElection extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public EndElection() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("end election servlet called");
		String id = (String) request.getParameter("electionId");
		int electionId = Integer.parseInt(id);	
		Election election = new Election();
		JSONObject responseObject = election.endElection(electionId);
		response.getWriter().append(responseObject.toString());
	}
}
