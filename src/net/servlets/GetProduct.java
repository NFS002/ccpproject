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
import net.transactions.Product;

/**
 * Servlet implementation class GetContract
 */
@WebServlet("/GetProduct")
public class GetProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProduct() {
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
			 	String pid = request.getParameter("pid");
			 	Product p = util.getProductDetails(pid);
			 	Gson g = util.createCCPJsonSerializer();
	            if (p != null)
	            		builder.add("product",g.toJson(p).replace("\\\"", "\""));
	            else
	            		builder.add("error", "No product with PID '" + pid
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
