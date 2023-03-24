package candidate;
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
@WebServlet("/myapp/admin/StoreImageForSymbol")
public class StoreImageForSymbol extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
    public StoreImageForSymbol() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject responseObject = new JSONObject();	
		System.out.println("store symbol servlet called");
		
		Part part = request.getPart("image");
		InputStream ins = part.getInputStream();
		byte[] bytes = ins.readAllBytes();
		String head = part.getHeader("Content-Disposition");
		String path = "/home/esakki-zstk293/Symbols/"+(head.substring(head.lastIndexOf("\"",head.length()-2),head.lastIndexOf("\""))).substring(1);
		System.out.println(path);
		File file = new File(path);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.flush();
		fos.close();
		responseObject.put("StatusCode", 200);
		responseObject.put("ImgPath", path);
		responseObject.put("Message", "symbol img saved Successfully!!");
		response.getWriter().append(responseObject.toString());
	}
}
