package common;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(name="DBConnection", urlPatterns={"/DBConnection"}, loadOnStartup=0)
public class DBConnection extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public DBConnection() {
        super();
    }
 
    public void init() {
    	ServletContext context = getServletContext();
    	String dbName =  context.getInitParameter("dbName");
    	String dbUserName = context.getInitParameter("dbUsername");
    	String dbPassword = context.getInitParameter("dbPassword");
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			ApplicationVariables.dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbName, dbUserName, dbPassword);
		} 
    	catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
    	catch (SQLException e) {
    		System.out.println(e.getMessage());
		}
    }
}
