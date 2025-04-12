<%@page import="com.charity.model.DatabaseConnection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Charity Event Finder</title>
    <link rel="stylesheet" href="./css/ind_style.css">
</head>
<body>
    
    <% 
        int[] counts = DatabaseConnection.getEventSummary(); 
    %>
    <header>
        <h1>Welcome to Charity Event Finder</h1>
        <p>Your gateway to finding and registering for charity events!</p>
    </header>

    <nav>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
    </nav>

    <main>
        <section>
            <h2>Features</h2>
            <ul>
                <li>Discover upcoming charity events</li>
                <li>Register with ease</li>
                <li>Manage your registrations through your profile</li>
            </ul>
        </section>

        <section>
            <h2 class="main-title">We Have</h2>
            <div class="stats-container">
                <div class="stat">
                    <img src="images/places_covered.jpg" alt="Places Covered">
                    <p>We have covered over <strong><%= counts[1] %></strong> places!</p>
                </div>
                <div class="stat">
                    <img src="images/people_joined.jpg" alt="People Joined">
                    <p><strong><%= counts[2] %></strong> people have joined us till date!</p>
                </div>
                <div class="stat">
                    <img src="images/events_covered.jpeg" alt="Events Covered">
                    <p>We have covered <strong><%= counts[0] %></strong> events so far!</p>
                </div>
            </div>
        </section>

        <section>
            <h2>About Us</h2>
            <p>Charity Event Finder connects users to meaningful charity events. Explore available events and register to participate in causes that matter to you.</p>
        </section>
    </main>

    <footer>
        <p>&copy; 2025 Charity Event Finder. All rights reserved.</p>
    </footer>
</body>
</html>