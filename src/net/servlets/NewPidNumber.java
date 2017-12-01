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
 * Servlet implementation class NewPidNumber
 */
@WebServlet("/NewPidNumber")
public class NewPidNumber extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewPidNumber() {
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
		try {
			CCPUtils util = new CCPUtils();
			String product = util.newProductNumber();
			builder.add("pid", product);
		} catch (Exception e) {;
			builder.add("error", e.getMessage());
			e.printStackTrace();
		}
		finally {
			writer.println(builder.build().toString());
			writer.flush();
			writer.close();
		}
		
	}
 }

