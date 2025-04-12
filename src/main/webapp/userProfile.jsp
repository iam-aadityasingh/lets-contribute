<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>User Profile</title>
   <link rel="stylesheet" href="css/Profile_style.css">
</head>
<body>
    <div class="header">
        <h1>User Profile</h1>
        <form method="get" action="HomepageServlet">
            <button type="submit" class="home-button">
                <img src="./images/home.png" alt="home"/>
            </button>
        </form>
        
        <form method="get" action="LogoutServlet">
            <button type="submit" class="logout-button">
                <img src="./images/logout.png" alt="logout" />
            </button>
        </form>
    </div>

    <div class="user-info">
        <p><strong>Username:</strong> <%= request.getAttribute("username") %></p>
        <p><strong>Email:</strong> ${email}</p>
        <p><strong>Phone Number:</strong> <%= request.getAttribute("ph_no") %></p>
        <p><strong>Total Registered Events:</strong> <%= request.getAttribute("totalEvents") %></p>
        
        <form action="PassReqForUpdateServlet" method="post">
            <input type="hidden" name="username" value="<%= request.getAttribute("username")  %>"/>
            <input type="hidden" name="phNo" value="<%= request.getAttribute("ph_no")  %>"/>
            <input type="hidden" name="password" value="<%= request.getAttribute("password")  %>"/>
            <button type="submit" class="upBtn">Update Profile</button>
        </form>
        <form action="ViewHistoryServlet" method="post">
            <button type="submit" class="upBtn">View History</button>
        </form>
    </div>
            
    <h2>Registered Events</h2>
    <div class="registered-events">
        <% 
            ArrayList<String[]> registeredEvents = (ArrayList<String[]>) request.getAttribute("registeredEvents"); 
            if (registeredEvents != null && !registeredEvents.isEmpty()) { 
        %>
            <ul>
                <% for (String[] event : registeredEvents) { %>
                    <li>
                        <strong>Event Name:</strong> <%= event[1] %><br>
                        <strong>Description:</strong> <%= event[2] %><br>
                        <strong>Location:</strong> <%= event[3] %><br>
                        <strong>Date:</strong> <%= event[4] %><br>
                        <strong>Time:</strong> <%= event[5] %>
                        <form action="DeleteEventServlet" method="get">
                            <input type="hidden" name="id" value="<%= event[0] %>"/>
                            <button type="submit">Delete</button>
                        </form>
                    </li>
                <% } %>
            </ul>
        <% } else { %>
        <p class="no-events">You haven't registered for any events yet.</p>
        <% } %>
    </div>
</body>
</html>
