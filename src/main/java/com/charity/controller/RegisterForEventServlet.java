package com.charity.controller;

import com.charity.model.DatabaseConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RegisterForEventServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("email");
        int eventId = Integer.parseInt(request.getParameter("event_id"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT * FROM registrations WHERE user_email = ? AND event_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, userEmail);
            checkStmt.setInt(2, eventId);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                    response.sendRedirect("UserProfileServlet");
            } else {
                String insertQuery = "INSERT INTO registrations (user_email, event_id) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, userEmail);
                insertStmt.setInt(2, eventId);

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    String updateCountQuery = "UPDATE events SET registered_count = registered_count + 1 WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateCountQuery);
                    updateStmt.setInt(1, eventId);
                    updateStmt.executeUpdate();
                    response.sendRedirect("UserProfileServlet");
                } else {
                    out.println("<h3>Failed to register for the event!</h3>");
                }
            }
        } catch (SQLException e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
