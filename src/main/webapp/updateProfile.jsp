<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Update Profile</title>
    <link rel="stylesheet" href="css/updateProfile_style.css">
</head>
<body>
    <h1>Enter updated details</h1>
    <form action="UpdateProfileServlet" method="post">
        <input type="text" name="username" placeholder="Enter new username" value="<%=request.getParameter("username") %>" required />
        <input type="text" name="phNo" placeholder="Enter new ph.no" value="<%=request.getParameter("phNo") %>" required />
        <input type="password" name="password" placeholder="Enter new password" value="<%=request.getParameter("password") %>" required />
        <button type="submit">Update</button>
    </form>
</body>
</html>
