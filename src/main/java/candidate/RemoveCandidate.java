package candidate;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

@WebServlet("/myapp/admin/RemoveCandidate")
public class RemoveCandidate extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public RemoveCandidate() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String candidateId = request.getParameter("candidateId");		
		String eId = request.getParameter("electionId");
		System.out.println(eId);
		int electionId = Integer.parseInt(""+eId);
		
		Candidate candidate = new Candidate();
		JSONObject responseObject = candidate.removeCandidate(candidateId, electionId);
		response.getWriter().append(responseObject.toString());
	}
}
