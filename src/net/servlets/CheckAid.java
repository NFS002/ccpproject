package net.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.database.CCPUtils;

/**
 * Servlet implementation class CheckAid
 */
@WebServlet("/CheckAid")
public class CheckAid extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckAid() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		 try {
			 
			    Class.forName("com.mysql.jdbc.Driver").newInstance();
			    String number = request.getParameter("aid");
			 	CCPUtils util = new CCPUtils();
			 	if (!util.checkNumberAvailability(number)) {
			 		builder.add("success", true);
			 	}
	            else {
	            		builder.add("error", "Account number is not valid");
	            }
	        }
		 catch  (Exception e) {
			 e.printStackTrace();
			 builder.add("error", "internal");
			 e.printStackTrace();
		 }
		 finally {
			out.print(builder.build().toString());
			out.flush();
			out.close();			 
		 }
	}

}
