<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <link rel="stylesheet" href="css/register_style.css">
</head>
<body>
    <h1>Register</h1>
    <form action="RegisterServlet" method="post">
        <label for="name">Name:</label>
        <input type="text" name="name" required><br>
        <label for="phone">Phone:</label>
        <input type="text" name="phone" required><br>
        <label for="email">Email:</label>
        <input type="email" name="email" required><br>
        <label for="password">Password:</label>
        <input type="password" name="password" required><br>
        <button type="submit">Register</button>
    </form>
</body>
</html>
