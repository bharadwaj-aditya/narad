package com.narad.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3134663403811102423L;
	private static final Logger logger = LoggerFactory.getLogger(DefaultServlet.class);

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map parameterMap = req.getParameterMap();
		StringBuilder builder = new StringBuilder();
		builder.append("Request landed at default servlet. Request path: ").append(req.getRequestURI());
		builder.append("Parameters: ").append(parameterMap.toString());
		builder.append(" .Redirecting to default page. ");

		logger.info(builder.toString());
		//resp.sendRedirect("/services/v1/");
		resp.sendRedirect("index.html");
	}
}
