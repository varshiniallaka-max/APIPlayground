
package com.apiplayground.servlet;

import com.apiplayground.dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (username == null || email == null || password == null
                || username.trim().isEmpty()
                || email.trim().isEmpty()
                || password.isEmpty()) {

            request.setAttribute(
                    "error", "Please fill in all fields.");
            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
            return;
        }

        username = username.trim();
        email = email.trim();

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            request.setAttribute(
                    "error", "Enter a valid email address.");
            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
            return;
        }

        if (password.length() < 8) {
            request.setAttribute(
                    "error", "Password must contain at least 8 characters.");
            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
            return;
        }

        if (userDAO.usernameExists(username)) {
            request.setAttribute(
                    "error", "Username already exists.");
            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
            return;
        }

        boolean registered =
                userDAO.registerUser(username, email, password);

        if (registered) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp?registered=true");
        }  else {
            request.setAttribute(
            "error",
            "Registration failed. Check the NetBeans Output window for the database error.");
            request.getRequestDispatcher("/register.jsp")
            .forward(request, response);
}
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(
                request.getContextPath() + "/register.jsp");
    }
}
