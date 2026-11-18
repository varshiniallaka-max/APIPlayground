
package com.apiplayground.servlet;

import com.apiplayground.dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(
            request.getContextPath() + "/login.jsp"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null
                || username.trim().isEmpty()
                || password.isEmpty()) {

            request.setAttribute(
                "error", "Please enter your username and password."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
            return;
        }

        username = username.trim();

        int userId = userDAO.loginUser(username, password);

        if (userId > 0) {

            HttpSession session = request.getSession();
            session.setAttribute("userId", userId);
            session.setAttribute("username", username);

            response.sendRedirect(
                request.getContextPath() + "/dashboard.jsp"
            );

        } else {
            request.setAttribute(
                "error", "Invalid username or password."
            );

            request.getRequestDispatcher("/login.jsp")
                   .forward(request, response);
        }
    }
}
