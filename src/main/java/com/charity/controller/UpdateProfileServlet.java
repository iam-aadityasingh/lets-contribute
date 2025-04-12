package com.charity.controller;

import com.charity.model.DatabaseConnection;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UpdateProfileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String email = (String) session.getAttribute("email");
        String username = request.getParameter("username");
        String phone = request.getParameter("phNo");
        String password = request.getParameter("password");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE users SET username = ?, phone = ?, password = ? WHERE email = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);

            updateStmt.setString(1, username);
            updateStmt.setString(2, phone);
            updateStmt.setString(3, password);
            updateStmt.setString(4, email);

            int i = updateStmt.executeUpdate();

            if (i > 0) {
                request.setAttribute("username", username);
                request.setAttribute("ph_no", phone);
                request.setAttribute("password", password);

                ArrayList<String[]> registeredEvents = new ArrayList<>();
                String eventsQuery = "SELECT e.id, e.name, e.description, e.location, e.date, e.time " +
                                     "FROM registrations r " +
                                     "JOIN events e ON r.event_id = e.id " +
                                     "WHERE r.user_email = ?";
                PreparedStatement eventsStmt = conn.prepareStatement(eventsQuery);
                eventsStmt.setString(1, email);
                ResultSet eventsRs = eventsStmt.executeQuery();

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

                String totalEventsQuery = "SELECT get_event_count(?) from registrations";
                PreparedStatement totalEventsStmt = conn.prepareStatement(totalEventsQuery);
                totalEventsStmt.setString(1, email);
                ResultSet totalEventsRs = totalEventsStmt.executeQuery();
                if (totalEventsRs.next()) {
                    request.setAttribute("totalEvents", totalEventsRs.getInt(1));
                }

                request.setAttribute("registeredEvents", registeredEvents);
                request.getRequestDispatcher("userProfile.jsp").forward(request, response);
            }

        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
