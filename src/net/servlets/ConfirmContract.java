package net.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.AccountException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.database.CCPUtils;

/**
 * Servlet implementation class ConfirmContract
 */
@WebServlet("/ConfirmContract")
public class ConfirmContract extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmContract() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
			    String cid = request.getParameter("cid");
			    String pw = request.getParameter("pw");
			 	CCPUtils util = new CCPUtils();
			 	util.signAndProcess(cid, pw);
			 	builder.add("msg", "Contract has been successfull confirmed");
	        }
		 catch(AccountException e) {
			 builder.add("error",e.getMessage());
			 e.printStackTrace(); 
		 }
		 catch  (Exception e) {
			 e.printStackTrace();
			 builder.add("error", "There has been an internal error. Please try again");
			 e.printStackTrace();
		 }
		 finally {
			out.print(builder.build().toString());
			out.flush();
			out.close();			 
		 }
	}

}
