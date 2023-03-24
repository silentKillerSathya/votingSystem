package election;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import enums.ElectionType;

@WebServlet(urlPatterns = {"/myapp/admin/ShowElectionDetails", "/myapp/user/ShowElectionDetails"})
public class ShowElectionDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public ShowElectionDetails() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = (String) request.getParameter("electionId");
		System.out.println(id);
		int electionId = Integer.parseInt(id);
		String type = (String) request.getParameter("electionType");
		ElectionType electionType = null;
		if(type.equals("Political")) {
			electionType = ElectionType.Political;
		}
		else {
			electionType = ElectionType.Organizational;
		}
		Election election = new Election();
		JSONObject responseObject = election.getElectionDetails(electionId, electionType);
		response.getWriter().append(responseObject.toString());
	}

}
