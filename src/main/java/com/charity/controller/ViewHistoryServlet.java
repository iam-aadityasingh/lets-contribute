package com.charity.controller;

import com.charity.model.DatabaseConnection;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ViewHistoryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("email");

        if (userEmail == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String historyQuery = "SELECT action, old_username, new_username, old_phone, new_phone, old_password, new_password, event_id, timestamp " +
                                  "FROM audit_log WHERE user_email = ? " +
                                  "ORDER BY timestamp DESC";
            PreparedStatement stmt = conn.prepareStatement(historyQuery);
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String[]> historyList = new ArrayList<>();
            while (rs.next()) {
                historyList.add(new String[]{
                        rs.getString("action"),
                        rs.getString("old_username"),
                        rs.getString("new_username"),
                        rs.getString("old_phone"),
                        rs.getString("new_phone"),
                        rs.getString("old_password"),
                        rs.getString("new_password"),
                        rs.getInt("event_id") == 0 ? "N/A" : rs.getInt("event_id") + "",
                        rs.getTimestamp("timestamp").toString()
                });
            }

            request.setAttribute("historyList", historyList);
            request.getRequestDispatcher("viewHistory.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error retrieving history: " + e.getMessage());
            request.getRequestDispatcher("viewHistory.jsp").forward(request, response);
        }
    }
}
