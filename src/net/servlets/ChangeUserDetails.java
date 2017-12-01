package net.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.database.CCPUtils;

/**
 * Servlet implementation class ChangeUserDetails
 */
@WebServlet("/ChangeUserDetails")
public class ChangeUserDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeUserDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// No response
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		String message  = "There has been an internal sever error, and your request has not been processed, please try again";
		try {
			CCPUtils util = new CCPUtils();
			String aid = request.getParameter("aid");
			Enumeration<String> parameters = request.getParameterNames();
			String name = null;
			String value = null;
			while (parameters.hasMoreElements()) {
				name = parameters.nextElement();
				value = request.getParameter(name);
			} 
				switch (name) {
				case "email":
					builder.add("success", true);
					message = "Your email address has successfully changed, please make a note of this";
					util.changeUserEmail(aid,value);
					break;
				case "phone":
					builder.add("success", true);
					message = "Your phone number has been successfully changed, please make a note of this";
					util.changeUserPhone(aid,value);
					break;
				case "address":
					builder.add("success", true);
					message = "Your address has been successfully changed, please make a note of this";
					util.changeUserAddress(aid,value);
					break;
				case "pw":
					builder.add("success", true);
					message = "Your password has been successfully changed, please make a note of this";
					util.changeUserPassword(aid,value);
					break;
				case "default":
					builder.add("error", true);
					message  = "There has been an internal sever error, and your request has not been processed, please try again";
					break;
				}
		} catch (Exception e) {
			message  = "There has been an internal sever error, and your request has not been processed, please try again";
			builder.add("error", true);
			e.printStackTrace();
		}
		finally {
			builder.add("msg", message);
			writer.println(builder.build().toString());
			writer.flush();
			writer.close();
		}
		
	}
}
