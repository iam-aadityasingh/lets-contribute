package com.charity.controller;

import com.charity.model.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;

public class UserProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("email");

        if (userEmail == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String userQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setString(1, userEmail);
            ResultSet userRs = userStmt.executeQuery();

            if (userRs.next()) {
                request.setAttribute("username", userRs.getString("username"));
                request.setAttribute("ph_no", userRs.getLong("phone"));
                request.setAttribute("email", userRs.getString("email"));
                request.setAttribute("password", userRs.getString("password"));
            }

            String eventsQuery = "SELECT e.id, e.name, e.description, e.location, e.date, e.time " +
                                 "FROM registrations r " +
                                 "JOIN events e ON r.event_id = e.id " +
                                 "WHERE r.user_email = ?";
            PreparedStatement eventsStmt = conn.prepareStatement(eventsQuery);
            eventsStmt.setString(1, userEmail);
            ResultSet eventsRs = eventsStmt.executeQuery();

            ArrayList<String[]> registeredEvents = new ArrayList<>();
            while (eventsRs.next()) {
                registeredEvents.add(new String[]{
                        eventsRs.getInt("id") + "",
                        eventsRs.getString("name"),
                        eventsRs.getString("description"),
                        eventsRs.getString("location"),
                        eventsRs.getDate("date").toString(),
                        eventsRs.getTime("time").toString()
                });
            }

            String totalEventsQuery = "SELECT get_event_count(?)";
            PreparedStatement totalEventsStmt = conn.prepareStatement(totalEventsQuery);
            totalEventsStmt.setString(1, userEmail);

            ResultSet totalEventsRs = totalEventsStmt.executeQuery();
            if (totalEventsRs.next()) {
                int totalEvents = totalEventsRs.getInt(1);
                System.out.println("Total Events: " + totalEvents); 
                request.setAttribute("totalEvents", totalEvents);
            }
            
            request.setAttribute("registeredEvents", registeredEvents);
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);

        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
