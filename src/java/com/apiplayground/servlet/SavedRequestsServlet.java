
package com.apiplayground.servlet;

import com.apiplayground.dao.APIRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/saved")
public class SavedRequestsServlet extends HttpServlet {

    private final APIRequestDAO requestDAO = new APIRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = Integer.parseInt(
                session.getAttribute("userId").toString());

        List<String[]> savedRequests =
                requestDAO.getRequestsByUser(userId);

        request.setAttribute("savedRequests", savedRequests);

        request.getRequestDispatcher("/saved.jsp")
                .forward(request, response);
    }
}
