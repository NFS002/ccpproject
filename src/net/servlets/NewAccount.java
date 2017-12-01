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

import com.google.gson.Gson;

import net.accounts.Account;
import net.database.CCPUtils;

/**
 * Servlet implementation class NewAccount
 */
@WebServlet("/NewAccount")
public class NewAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewAccount() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//no response
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		Gson g = new Gson();
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			CCPUtils util = new CCPUtils();
			String firstName = request.getParameter("firstName");
			String secondName = request.getParameter("secondName");
			String dob = request.getParameter("dob");
			String email = request.getParameter("email");
			String phoneNumber = request.getParameter("phone");
			String uname = request.getParameter("uname");
			String pw = request.getParameter("pw");
			String address = request.getParameter("address");
			Account account = util.createAccount(firstName + " " + secondName, dob, email, phoneNumber, uname, pw, address);
			jsonBuilder.add("account", g.toJson(account));
		}
		catch (AccountException e) {
			jsonBuilder = Json.createObjectBuilder();
			jsonBuilder.add("error",e.getMessage());
		}
		catch (Exception e) {
			jsonBuilder = Json.createObjectBuilder();
			jsonBuilder.add("error","internal");
		}
		finally {
			out.print(jsonBuilder.build().toString());
			out.flush();
			out.close();
		}
	
		
	}

}
