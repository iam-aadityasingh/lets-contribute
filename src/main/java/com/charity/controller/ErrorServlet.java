package com.charity.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage == null) {
            errorMessage = "An unknown error occurred.";
        }
        request.setAttribute("displayErrorMessage", errorMessage);
//        request.getRequestDispatcher("error.jsp").forward(request, response);
        response.sendRedirect("error.jsp");
    }

}