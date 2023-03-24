package election;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;

import enums.ElectionType;
import voter.Vote;

@WebServlet(urlPatterns = {"/myapp/admin/ShowAllElections", "/myapp/user/ShowAllElections"})
public class ShowAllElections extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public ShowAllElections() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("show elec servlet called");
		JSONArray jsonArray = null;
		Election election = new Election();
		election.updateElections();
		
		if(request.getParameter("voterId")!=null && !request.getParameter("voterId").equals("null")) {
			System.out.println("xxxxx");
			String voterId = request.getParameter("voterId");
			String type = request.getParameter("whichElection");
			ElectionType electionType = null;
			if(type.equals("political")) {
				electionType = ElectionType.Political;
			}
			else {
				electionType = ElectionType.Organizational;
			}		
			Vote voter = new Vote();
			jsonArray = voter.checkValidVoter(electionType, voterId);
		}
		else if(request.getParameter("whichElection").equals("all") || request.getParameter("whichElection").equals("allCandi") || request.getParameter("whichElection").equals("allResult") || request.getParameter("whichElection").equals("allForUser")) {
			jsonArray = election.showAllElections();
		}
		else if(request.getParameter("whichElection").equals("political")) {
			jsonArray = election.showOnlyPoliticalElections();
		}
		else if(request.getParameter("whichElection").equals("organizational")) {
			jsonArray = election.showOnlyOrganizationalElections();
		}	
		else if(request.getParameter("whichElection").equals("activeAdd") || request.getParameter("whichElection").equals("activeEnd")) {
			jsonArray = election.showOnlyActiveElections();
		}
		else if(request.getParameter("whichElection").equals("archived")) {
			jsonArray = election.showOnlyArchivedElections();
		}
		System.out.println(jsonArray);
		response.getWriter().append(jsonArray.toString());
	}	
}
