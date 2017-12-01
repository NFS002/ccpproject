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
 * Servlet implementation class SubmitProduct
 */
@WebServlet("/SubmitProduct")
public class SubmitProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitProduct() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		String msg = "";
		try {
			CCPUtils util = new CCPUtils();
			String aid = request.getParameter("aid");
			String pid = request.getParameter("pid");
			String size = request.getParameter("size");
			String rrp = request.getParameter("rrp");
			String description = request.getParameter("description");
			util.createAndSaveNewProduct(aid,pid,size,rrp,description);
			builder.add("success", true);
			msg = "Your product has been successfully saved";
		} catch (Exception e) {;
			builder.add("error", "Internal");
			e.printStackTrace();
			msg = "There has been an internal sever error and your contract could not be saved, please try again";
		}
		finally {
			builder.add("msg", msg);
			writer.println(builder.build().toString());
			writer.flush();
			writer.close();
		}
	}

}
