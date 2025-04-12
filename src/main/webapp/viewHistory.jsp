<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>History</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&icon_names=account_circle" />
    <link rel="stylesheet" href="css/history_style.css">
</head>
<body>
    <div class="header">
        <h1>User Profile</h1>
        <div class="profile-icon"> 
            <form action="UserProfileServlet" method="get"> 
                <button type="submit"> 
                    <span class="material-symbols-outlined">account_circle</span> 
                </button> 
            </form> 
        </div>
    </div>
    <% 
        ArrayList<String[]> historyList = (ArrayList<String[]>) request.getAttribute("historyList"); 
        if (historyList == null || historyList.isEmpty()) { 
    %>
       <p class="no-history">No history found.</p>
    <% } else { %>
        <table>
            <tr>
                <th>Action</th>
                <th>Old Username</th>
                <th>New Username</th>
                <th>Old Phone</th>
                <th>New Phone</th>
                <th>Old Password</th>
                <th>New Password</th>
                <th>Event ID</th>
                <th>Timestamp</th>
            </tr>
            <% for (String[] history : historyList) { %>
                <tr>
                    <% for (String value : history) { %>
                        <td><%= value != null ? value : "N/A" %></td>
                    <% } %>
                </tr>
            <% } %>
        </table>
    <% } %>
</body>
</html>
