
package com.apiplayground.servlet;

import com.apiplayground.dao.HistoryDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    private final HistoryDAO historyDAO = new HistoryDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        request.setAttribute(
                "history", historyDAO.getHistoryByUser(userId));

        request.getRequestDispatcher("/history.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        String historyIdText =
                request.getParameter("historyId");

        if (historyIdText != null) {
            try {
                int historyId = Integer.parseInt(historyIdText);
                int userId = (Integer) session.getAttribute("userId");

                historyDAO.deleteHistory(historyId, userId);

            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid history ID.");
                return;
            }
        }

        response.sendRedirect(
                request.getContextPath() + "/history");
    }
}
