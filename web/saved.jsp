
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
    private String escapeSaved(String value) {
        if (value == null) return "";

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    private String getSavedMethodClass(String method) {
        if (method == null) return "";

        switch (method.toUpperCase()) {
            case "GET": return "saved-method-get";
            case "POST": return "saved-method-post";
            case "PUT": return "saved-method-put";
            case "PATCH": return "saved-method-patch";
            case "DELETE": return "saved-method-delete";
            default: return "";
        }
    }
%>

<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<String[]> savedRequests =
            (List<String[]>) request.getAttribute("savedRequests");

    if (savedRequests == null) {
        savedRequests = new java.util.ArrayList<String[]>();
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>API Playground | Saved Requests</title>

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

            <a class="nav-link"
               href="${pageContext.request.contextPath}/history">
                ◷ Request History
            </a>

            <a class="nav-link active"
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
                Welcome, <%= escapeSaved(String.valueOf(session.getAttribute("username"))) %>
            </div>

        </div>


        <!-- PAGE HEADING -->

        <section class="saved-heading">

            <h1>Saved Requests</h1>

            <p>
                View and manage your saved API requests.
            </p>

        </section>


        <!-- SAVED REQUESTS TABLE -->

        <section class="saved-panel">

            <div class="saved-toolbar">

                <h2>Your Saved Requests</h2>

                <% if (!savedRequests.isEmpty()) { %>

                    <input type="search"
                           id="savedSearch"
                           class="saved-search"
                           placeholder="Search by name, method or URL..."
                           oninput="filterSavedRequests()">

                <% } %>

            </div>


            <% if (savedRequests.isEmpty()) { %>

                <div class="saved-empty">

                    <p>No saved requests yet.</p>

                    <a class="workspace-button"
                       href="${pageContext.request.contextPath}/api">
                        Create API Request
                    </a>

                </div>

            <% } else { %>

                <div class="saved-table-wrap">

                    <table class="saved-table">

                        <thead>

                            <tr>
                                <th>Request Name</th>
                                <th>Method</th>
                                <th>Request URL</th>
                                <th>Actions</th>
                            </tr>

                        </thead>

                        <tbody id="savedTableBody">

                            <% for (String[] item : savedRequests) { %>

                                <tr class="saved-row">

                                    <td class="saved-name">
                                        <%= escapeSaved(item[1]) %>
                                    </td>

                                    <td>

                                        <span class="saved-method <%= getSavedMethodClass(item[2]) %>">
                                            <%= escapeSaved(item[2]) %>
                                        </span>

                                    </td>

                                    <td>

                                        <span class="saved-url">
                                            <%= escapeSaved(item[3]) %>
                                        </span>

                                    </td>

                                    <td>

                                        <div class="saved-actions">

                                            <a class="saved-action-button"
                                               href="<%= request.getContextPath() %>/api?action=load&id=<%= java.net.URLEncoder.encode(item[0], "UTF-8") %>">
                                                Load
                                            </a>

                                            <a class="saved-action-button delete-action"
                                               href="<%= request.getContextPath() %>/api?action=delete&id=<%= java.net.URLEncoder.encode(item[0], "UTF-8") %>"
                                               onclick="return confirm('Delete this saved request?');">
                                                Delete
                                            </a>

                                        </div>

                                    </td>

                                </tr>

                            <% } %>

                        </tbody>

                    </table>

                </div>

                <div class="saved-count" id="savedCount">
                    Total saved requests: <%= savedRequests.size() %>
                </div>

                <div class="saved-no-results" id="savedNoResults">
                    No matching saved requests found.
                </div>

            <% } %>

        </section>

    </main>

</div>


<!-- JAVASCRIPT -->

<script>

    // LIGHT AND DARK THEME

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


    // SEARCH SAVED REQUESTS

    const filterSavedRequests = () => {

        const searchInput = document.getElementById("savedSearch");

        if (!searchInput) return;

        const searchValue =
            searchInput.value.toLowerCase().trim();

        const rows = document.querySelectorAll(".saved-row");

        let visibleCount = 0;

        rows.forEach(row => {

            const rowText = row.textContent.toLowerCase();

            const matches = rowText.includes(searchValue);

            row.style.display = matches ? "" : "none";

            if (matches) {
                visibleCount++;
            }

        });

        const countElement = document.getElementById("savedCount");
        const emptyElement = document.getElementById("savedNoResults");

        if (countElement) {
            countElement.textContent =
                "Showing " + visibleCount + " of " +
                rows.length + " saved requests";
        }

        if (emptyElement) {
            emptyElement.style.display =
                visibleCount === 0 ? "block" : "none";
        }
    };

</script>

</body>
</html>
