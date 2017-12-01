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
 * Servlet implementation class SubmitContract
 */
@WebServlet("/SubmitContract")
public class SubmitContract extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitContract() {
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
			String cid = request.getParameter("cid");
			String from = request.getParameter("from");
			String to = request.getParameter("to");
			String value = request.getParameter("val");
			String[] pids = request.getParameter("products").split(",");
			util.saveNewContract(util.writeContract(cid, from, to, value, pids));
			builder.add("success", true);
			msg = "Your contract has been successfully saved";
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
