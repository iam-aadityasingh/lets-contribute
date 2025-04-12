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

public class DeleteEventServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
            PrintWriter out = response.getWriter();
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("email") == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String userEmail = (String) session.getAttribute("email");
            String eventId = request.getParameter("id");

            if (eventId == null || eventId.isEmpty()) {
                response.sendRedirect("userProfile.jsp");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                
                String deleteSQL = "DELETE FROM registrations WHERE user_email = ? AND event_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                    pstmt.setString(1, userEmail);
                    pstmt.setInt(2, Integer.parseInt(eventId));
                    int deletedEvent = pstmt.executeUpdate();
                    
                    if(deletedEvent > 0){
                        String updateCountQuery = "UPDATE events SET registered_count = registered_count - 1 WHERE id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateCountQuery);
                        updateStmt.setInt(1, Integer.parseInt(eventId));
                        updateStmt.executeUpdate();
                    }
                }
               
                String fetchSQL = "SELECT e.id, e.name, e.description, e.location, e.date, e.time " +
                                  "FROM events e JOIN registrations r ON e.id = r.event_id " +
                                  "WHERE r.user_email = ?";
                
                try (PreparedStatement pstmt = conn.prepareStatement(fetchSQL)) {
                    pstmt.setString(1, userEmail);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        
                        ArrayList<String[]> registeredEvents = new ArrayList<>();
                        while (rs.next()) {
                            registeredEvents.add(new String[]{
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getString("location"),
                                rs.getString("date"),
                                rs.getString("time")
                            });
                        }   
                        request.setAttribute("registeredEvents", registeredEvents);
                    }
                }
                
            request.getRequestDispatcher("UserProfileServlet").forward(request, response); 
            } catch (Exception e) {
                out.println("Exception: "+ e);
            }
            
        }
    }