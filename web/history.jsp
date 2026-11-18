
<%@page import="java.util.List"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%!
    private String escapeHistory(String value) {
        if (value == null) return "";

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    private String getMethodClass(String method) {
        if (method == null) return "";

        switch (method.toUpperCase()) {
            case "GET": return "method-get";
            case "POST": return "method-post";
            case "PUT": return "method-put";
            case "PATCH": return "method-patch";
            case "DELETE": return "method-delete";
            default: return "";
        }
    }

    private String getStatusClass(String status) {
        if (status == null) return "status-other";

        try {
            int code = Integer.parseInt(status.trim());

            if (code >= 200 && code < 300) {
                return "status-success";
            }

            if (code >= 400) {
                return "status-error";
            }

            return "status-other";

        } catch (NumberFormatException e) {
            return "status-other";
        }
    }
%>

<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Object historyObject = request.getAttribute("history");

    List<String[]> history =
            historyObject instanceof List
            ? (List<String[]>) historyObject
            : new java.util.ArrayList<String[]>();
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>API Playground | Request History</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

    <script>
        (function () {
            const savedTheme =
                localStorage.getItem("apiPlaygroundTheme") || "light";

            document.documentElement.setAttribute("data-theme", savedTheme);
        })();
    </script>

</head>

<body>

<div class="app-layout">

    <!-- SIDEBAR -->

    <aside class="sidebar">

        <div class="brand">
            ◈ API Playground
        </div>

        <div class="nav-label">WORKSPACE</div>

        <nav class="nav-links">

            <a class="nav-link"
               href="${pageContext.request.contextPath}/dashboard.jsp">
                ◫ Dashboard
            </a>

            <a class="nav-link"
               href="${pageContext.request.contextPath}/api">
                ⇄ API Playground
            </a>

            <a class="nav-link active"
               href="${pageContext.request.contextPath}/history">
                ◷ Request History
            </a>

            <a class="nav-link"
               href="${pageContext.request.contextPath}/saved">
                ☆ Saved Requests
            </a>

        </nav>

        <div class="sidebar-bottom">

            <button type="button"
                    class="theme-button"
                    id="themeToggle"
                    onclick="toggleTheme()">
                🌙 Switch to Dark Mode
            </button>

            <a class="logout-link"
               href="${pageContext.request.contextPath}/logout">
                Logout
            </a>

        </div>

    </aside>


    <!-- MAIN CONTENT -->

    <main class="main-content">

        <div class="topbar">

            <div class="user-info">
                Welcome, <%= escapeHistory(String.valueOf(session.getAttribute("username"))) %>
            </div>

        </div>


        <!-- PAGE HEADING -->

        <section class="history-heading">

            <h1>Request History</h1>

            <p>
                Review and manage your previous API executions.
            </p>

        </section>


        <!-- HISTORY TABLE -->

        <section class="history-panel">

            <div class="history-toolbar">

                <h2>API Request History</h2>

                <% if (!history.isEmpty()) { %>

                    <input type="search"
                           id="historySearch"
                           class="history-search"
                           placeholder="Search by URL, method or status..."
                           oninput="filterHistory()">

                <% } %>

            </div>


            <% if (history.isEmpty()) { %>

                <div class="history-empty">

                    <p>No API requests recorded yet.</p>

                    <a class="workspace-button"
                       href="${pageContext.request.contextPath}/api">
                        Open API Playground
                    </a>

                </div>

            <% } else { %>


                <div class="history-table-wrap">

                    <table class="history-table">

                        <thead>

                            <tr>
                                <th>History ID</th>
                                <th>Request ID</th>
                                <th>Method</th>
                                <th>Request URL</th>
                                <th>Status Code</th>
                                <th>Response Time</th>
                                <th>Executed At</th>
                                <th>Action</th>
                            </tr>

                        </thead>


                        <tbody id="historyTableBody">

                        <% for (String[] item : history) { %>

                            <tr class="history-row">

                                <td>
                                    <%= escapeHistory(item[0]) %>
                                </td>

                                <td>
                                    <%= escapeHistory(item[1]) %>
                                </td>

                                <td>

                                    <span class="method-badge <%= getMethodClass(item[2]) %>">
                                        <%= escapeHistory(item[2]) %>
                                    </span>

                                </td>

                                <td>

                                    <span class="history-url">
                                        <%= escapeHistory(item[3]) %>
                                    </span>

                                </td>

                                <td>

                                    <span class="status-badge <%= getStatusClass(item[4]) %>">
                                        <%= escapeHistory(item[4]) %>
                                    </span>

                                </td>

                                <td>
                                    <%= escapeHistory(item[5]) %> ms
                                </td>

                                <td>
                                    <%= escapeHistory(item[6]) %>
                                </td>

                                <td>

                                    <form
                                        action="${pageContext.request.contextPath}/history"
                                        method="post"
                                        onsubmit="return confirm('Delete this history entry?');">

                                        <input type="hidden"
                                               name="historyId"
                                               value="<%= escapeHistory(item[0]) %>">

                                        <button type="submit"
                                                class="history-delete-button">
                                            Delete
                                        </button>

                                    </form>

                                </td>

                            </tr>

                        <% } %>

                        </tbody>

                    </table>

                </div>

                <div class="history-count" id="historyCount">
                    Total requests: <%= history.size() %>
                </div>

                <div class="history-empty"
                     id="noSearchResults"
                     style="display:none;">
                    No matching requests found.
                </div>

            <% } %>

        </section>

    </main>

</div>


<!-- JAVASCRIPT -->

<script>

    // THEME SWITCHING

    const themeToggle = document.getElementById("themeToggle");

    const updateThemeButton = () => {

        const currentTheme =
            document.documentElement.getAttribute("data-theme");

        themeToggle.textContent = currentTheme === "dark"
            ? "☀️ Switch to Light Mode"
            : "🌙 Switch to Dark Mode";
    };

    const toggleTheme = () => {

        const currentTheme =
            document.documentElement.getAttribute("data-theme");

        const newTheme =
            currentTheme === "dark" ? "light" : "dark";

        document.documentElement.setAttribute("data-theme", newTheme);

        localStorage.setItem("apiPlaygroundTheme", newTheme);

        updateThemeButton();
    };

    updateThemeButton();


    // SEARCH AND FILTER HISTORY

    const filterHistory = () => {

        const searchInput = document.getElementById("historySearch");

        if (!searchInput) return;

        const searchValue = searchInput.value.toLowerCase().trim();

        const rows = document.querySelectorAll(".history-row");

        let visibleCount = 0;

        rows.forEach(row => {

            const rowText = row.textContent.toLowerCase();

            const matches = rowText.includes(searchValue);

            row.style.display = matches ? "" : "none";

            if (matches) {
                visibleCount++;
            }

        });

        const countElement = document.getElementById("historyCount");
        const emptyElement = document.getElementById("noSearchResults");

        if (countElement) {
            countElement.textContent =
                "Showing " + visibleCount + " of " + rows.length + " requests";
        }

        if (emptyElement) {
            emptyElement.style.display =
                visibleCount === 0 ? "block" : "none";
        }
    };

</script>

</body>
</html>
