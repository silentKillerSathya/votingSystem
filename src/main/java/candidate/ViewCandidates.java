package candidate;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;

@WebServlet(urlPatterns = {"/myapp/admin/ViewCandidates", "/myapp/user/ViewCandidates"})
public class ViewCandidates extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
    public ViewCandidates() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("viewCandidate servlet called");
		
		String id = request.getParameter("electionId");
		int electionId = Integer.parseInt(id);
		Candidate candidate = new Candidate();
		JSONArray jsonArray = candidate.viewCandidates(electionId);
		response.getWriter().append(jsonArray.toString());
	}
}
