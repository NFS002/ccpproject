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

import net.database.CCPUtils;
import net.transactions.Contract;

/**
 * Servlet implementation class GetContract
 */
@WebServlet("/GetContract")
public class GetContract extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetContract() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//do nothing
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
			 	CCPUtils util = new CCPUtils();
			 	String cid = request.getParameter("cid");
			 	Contract c = util.getContractDetails(cid);
			 	Gson g = util.createCCPJsonSerializer();
	            if (c != null)
	            		builder.add("contract",g.toJson(c).replace("\\\"", "\""));
	            else
	            		builder.add("error", "No contract with CID '" + cid
	            				+ "' could be found");
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
