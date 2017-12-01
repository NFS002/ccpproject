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

import com.google.gson.Gson;

import net.accounts.Account;
import net.database.CCPUtils;


/**
 * Servlet implementation class HelloServlet
 */
@WebServlet("/GetAccount")
public class GetAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAccount() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		//No response
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		 try {
			    Class.forName("com.mysql.jdbc.Driver").newInstance();
			 	CCPUtils util = new CCPUtils();
			 	String uname = request.getParameter("uname");
			 	String pw = request.getParameter("pw");
			 	Account a = util.getAccount(uname, pw);
			 	Gson g = util.createCCPJsonSerializer();
	            if (a != null)
	            		builder.add("account",g.toJson(a).replace("\\\"", "\""));
	            else
	            		builder.add("error", "We could not retrieve your account details, "
	            				+ "please make sure you have entered your username and password correctly");
	        }
		 catch  (Exception e) {
			 builder.add("error", "There has been an internal error, please try again");
			 e.printStackTrace();
		 }
		 finally {
			out.print(builder.build().toString());
			out.flush();
			out.close();			 
		 }
	}

}
