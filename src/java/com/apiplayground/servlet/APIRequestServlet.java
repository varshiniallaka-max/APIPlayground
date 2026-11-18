
package com.apiplayground.servlet;

import com.apiplayground.dao.APIRequestDAO;
import com.apiplayground.dao.HistoryDAO;
import com.apiplayground.dao.RequestDetailsDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api")
public class APIRequestServlet extends HttpServlet {

    private final APIRequestDAO requestDAO = new APIRequestDAO();
    private final RequestDetailsDAO detailsDAO =
            new RequestDetailsDAO();
    private final HistoryDAO historyDAO = new HistoryDAO();

    private HttpSession requireSession(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return null;
        }

        return session;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = requireSession(request, response);

        if (session == null) {
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));

                requestDAO.deleteRequest(id, userId);

            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request ID.");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/api");
            return;
        }

        if ("load".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));

                String[] saved =
                        requestDAO.getRequestById(id, userId);

                if (saved == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "Request not found.");
                    return;
                }

                request.setAttribute("savedRequest", saved);
                request.setAttribute(
                        "headers", detailsDAO.getHeaders(id));
                request.setAttribute(
                        "parameters", detailsDAO.getParameters(id));

            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request ID.");
                return;
            }
        }

        request.setAttribute(
                "savedRequests", requestDAO.getRequestsByUser(userId));

        request.getRequestDispatcher("/playground.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = requireSession(request, response);

        if (session == null) {
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");

        if ("save".equals(action)) {
            saveRequest(request, response, userId);
        } else if ("update".equals(action)) {
            updateRequest(request, response, userId);
        } else if ("execute".equals(action)) {
            executeRequest(request, response, userId);
        } else {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Unknown action.");
        }
    }

    private void saveRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId) throws IOException {

        String name = request.getParameter("requestName");
        String method = request.getParameter("method");
        String url = request.getParameter("url");
        String body = request.getParameter("body");

        if (!validFields(name, method, url)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Enter a request name, method, and URL.");
            return;
        }

        int id = requestDAO.addRequestForUserAndReturnId(
                userId, name.trim(), method.toUpperCase(),
                url.trim(), body);

        if (id > 0) {
            detailsDAO.saveHeaders(
                    id, readPairs(request, "header"));
            detailsDAO.saveParameters(
                    id, readPairs(request, "param"));
        }

        response.sendRedirect(
                request.getContextPath() + "/api");
    }

    private void updateRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId) throws IOException {

        try {
            int id = Integer.parseInt(
                    request.getParameter("requestId"));

            String name = request.getParameter("requestName");
            String method = request.getParameter("method");
            String url = request.getParameter("url");
            String body = request.getParameter("body");

            if (!validFields(name, method, url)) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Enter a request name, method, and URL.");
                return;
            }

            boolean updated = requestDAO.updateRequest(
                    id, userId, name.trim(), method.toUpperCase(),
                    url.trim(), body);

            if (updated) {
                detailsDAO.saveHeaders(
                        id, readPairs(request, "header"));
                detailsDAO.saveParameters(
                        id, readPairs(request, "param"));
            }

            response.sendRedirect(
                    request.getContextPath() + "/api");

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid request ID.");
        }
    }

    private void executeRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            int userId) throws IOException, ServletException {

        String method = request.getParameter("method");
        String url = request.getParameter("url");
        String body = request.getParameter("body");
        String requestIdText = request.getParameter("requestId");

        int requestId = 0;

        if (requestIdText != null && !requestIdText.isBlank()) {
            try {
                requestId = Integer.parseInt(requestIdText);
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request ID.");
                return;
            }
        }

        int statusCode = 0;
        long responseTime = 0;
        String responseBody;

        try {
            if (!validFields("Request", method, url)) {
                throw new IllegalArgumentException(
                        "Enter a valid HTTP method and URL.");
            }

            URI uri = URI.create(url.trim());

            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || uri.getHost() == null
                    || uri.getUserInfo() != null) {

                throw new IllegalArgumentException(
                        "Enter a valid HTTPS URL.");
            }

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();

            Map<String, String> parameters =
                    readPairs(request, "param");

            String finalUrl = addQueryParameters(uri, parameters);
            URI finalUri = URI.create(finalUrl);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(finalUri)
                    .timeout(Duration.ofSeconds(20));

            Map<String, String> headers =
                    readPairs(request, "header");

            for (Map.Entry<String, String> entry
                    : headers.entrySet()) {

                String key = entry.getKey();

                if (key.equalsIgnoreCase("host")
                        || key.equalsIgnoreCase("content-length")
                        || key.equalsIgnoreCase("connection")) {
                    continue;
                }

                builder.header(key, entry.getValue());
            }

            String requestBody = body == null ? "" : body;

            switch (method.toUpperCase()) {
                case "GET":
                    builder.GET();
                    break;

                case "POST":
                    builder.POST(
                            HttpRequest.BodyPublishers.ofString(
                                    requestBody));
                    break;

                case "PUT":
                    builder.PUT(
                            HttpRequest.BodyPublishers.ofString(
                                    requestBody));
                    break;

                case "PATCH":
                    builder.method(
                            "PATCH",
                            HttpRequest.BodyPublishers.ofString(
                                    requestBody));
                    break;

                case "DELETE":
                    builder.DELETE();
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Unsupported HTTP method.");
            }

            long startTime = System.currentTimeMillis();

            HttpResponse<String> result = client.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString());

            responseTime = System.currentTimeMillis() - startTime;
            statusCode = result.statusCode();
            responseBody = result.body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            statusCode = 0;
            responseBody = "Request interrupted.";

        } catch (Exception e) {
            statusCode = 0;
            responseBody = "Request failed: " + e.getMessage();
        }

        historyDAO.saveHistory(
                userId,
                requestId,
                method,
                url,
                statusCode,
                responseTime,
                responseBody);

        request.setAttribute("statusCode", statusCode);
        request.setAttribute("responseTime", responseTime);
        request.setAttribute("responseBody", responseBody);
        request.setAttribute("submittedMethod", method);
        request.setAttribute("submittedUrl", url);

        request.setAttribute(
                "savedRequests", requestDAO.getRequestsByUser(userId));

        request.getRequestDispatcher("/playground.jsp")
                .forward(request, response);
    }

    private String addQueryParameters(
            URI uri, Map<String, String> parameters) {

        if (parameters.isEmpty()) {
            return uri.toString();
        }

        StringBuilder result = new StringBuilder(uri.toString());

        for (Map.Entry<String, String> entry
                : parameters.entrySet()) {

            String separator = result.toString().contains("?")
                    ? "&" : "?";

            result.append(separator)
                    .append(URLEncoder.encode(
                            entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(
                            entry.getValue(), StandardCharsets.UTF_8));
        }

        return result.toString();
    }

    private boolean validFields(
            String name, String method, String url) {

        if (name == null || name.trim().isEmpty()
                || method == null
                || url == null || url.trim().isEmpty()) {
            return false;
        }

        List<String> allowedMethods = List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE");

        return allowedMethods.contains(method.toUpperCase());
    }

    private Map<String, String> readPairs(
            HttpServletRequest request, String prefix) {

        Map<String, String> values = new LinkedHashMap<>();

        String[] names =
                request.getParameterValues(prefix + "Name");
        String[] data =
                request.getParameterValues(prefix + "Value");

        if (names == null || data == null) {
            return values;
        }

        int count = Math.min(names.length, data.length);

        for (int i = 0; i < count; i++) {
            if (names[i] != null
                    && !names[i].trim().isEmpty()) {

                values.put(
                        names[i].trim(),
                        data[i] == null ? "" : data[i]);
            }
        }

        return values;
    }
}
