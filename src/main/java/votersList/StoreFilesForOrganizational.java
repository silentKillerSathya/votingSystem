package votersList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.simple.JSONObject;

@MultipartConfig(maxFileSize = 1024*1024*1024)
@WebServlet("/myapp/admin/StoreFilesForOrganizational")
public class StoreFilesForOrganizational extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public StoreFilesForOrganizational() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject responseObject = new JSONObject();		
		System.out.println("storefile(org) filter called");
		Part part = request.getPart("file");
		InputStream ins = part.getInputStream();
		byte[] bytes = ins.readAllBytes();
		String head = part.getHeader("Content-Disposition");
		System.out.println(head);
		
		String path = "/home/esakki-zstk293/OrganizationalElectionVoters/"+(head.substring(head.lastIndexOf("\"",head.length()-2),head.lastIndexOf("\""))).substring(1);

		File file = new File(path);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.flush();
		fos.close();
		responseObject.put("Filepath", path);
		responseObject.put("StatusCode", 200);
		responseObject.put("Message", "File saved Successfully!!");		
		response.getWriter().append(responseObject.toString());
	}
}
