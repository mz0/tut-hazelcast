package com.exactpro.web.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/*"})
public class Api extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest rx, HttpServletResponse tx) throws IOException {
        tx.getWriter().print("API endpoint is alive\r\n");
    }
}
