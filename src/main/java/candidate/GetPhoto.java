package candidate;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/myapp/admin/GetPhoto", "/myapp/user/GetPhoto"})
public class GetPhoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public GetPhoto() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("get photo servlet called");
		String candidateImg = request.getParameter("candidateImg");
		File file = new File(candidateImg);
		FileInputStream fis = new FileInputStream(file);
		ServletOutputStream sos = response.getOutputStream();
		byte[] bytes = new byte[1024];
		int read = 0;
		response.setHeader("Content-Disposition", "inline");
		while((read = fis.read(bytes))!=-1) {
			sos.write(bytes, 0, read);
		}
		fis.close();
		sos.close();
	}
}
