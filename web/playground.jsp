
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<%!
    private String escapeHtml(String value) {
        if (value == null) return "";

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String[] saved = (String[]) request.getAttribute("savedRequest");

    Map<String, String> headers =
            (Map<String, String>) request.getAttribute("headers");

    Map<String, String> parameters =
            (Map<String, String>) request.getAttribute("parameters");

    String requestName = saved != null ? saved[1] : "";
    String method = saved != null ? saved[2] : "GET";
    String url = saved != null ? saved[3] : "";
    String body = saved != null && saved[4] != null ? saved[4] : "";
    String requestId = saved != null ? saved[0] : "";

    if (headers == null) headers = new java.util.LinkedHashMap<>();
    if (parameters == null) parameters = new java.util.LinkedHashMap<>();

    Object statusObject = request.getAttribute("statusCode");
    String responseBody = (String) request.getAttribute("responseBody");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>API Playground | Request Workspace</title>

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

        <div class="brand">◈ API Playground</div>

        <div class="nav-label">WORKSPACE</div>

        <nav class="nav-links">

            <a class="nav-link"
               href="${pageContext.request.contextPath}/dashboard.jsp">
                ◫ Dashboard
            </a>

            <a class="nav-link active"
               href="${pageContext.request.contextPath}/api">
                ⇄ API Playground
            </a>

            <a class="nav-link"
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
                Welcome, ${sessionScope.username}
            </div>
        </div>

        <section class="workspace-heading">
            <h1>API Playground</h1>
            <p>Create, send and manage your HTTP requests.</p>
        </section>


        <div class="workspace-grid">

            <!-- LEFT COLUMN -->
            <div class="workspace-column">

                <!-- REQUEST EDITOR -->
                <section class="workspace-panel">

                    <div class="panel-heading">
                        <div>
                            <h2>Send API Request</h2>
                            <p class="panel-description">
                                Configure your request and execute it.
                            </p>
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/api"
                          method="post">

                        <input type="hidden" name="action" value="execute">

                        <input type="hidden" name="requestId"
                               value="<%= escapeHtml(requestId) %>">

                        <div class="request-url-grid">

                            <div>
                                <label for="method">HTTP Method</label>

                                <select name="method" id="method"
                                        class="request-method">
                                    <option value="GET" <%= "GET".equals(method) ? "selected" : "" %>>GET</option>
                                    <option value="POST" <%= "POST".equals(method) ? "selected" : "" %>>POST</option>
                                    <option value="PUT" <%= "PUT".equals(method) ? "selected" : "" %>>PUT</option>
                                    <option value="PATCH" <%= "PATCH".equals(method) ? "selected" : "" %>>PATCH</option>
                                    <option value="DELETE" <%= "DELETE".equals(method) ? "selected" : "" %>>DELETE</option>
                                </select>
                            </div>

                            <div class="url-field">
                                <label for="url">Request URL</label>

                                <input type="url" id="url" name="url"
                                       value="<%= escapeHtml(url) %>"
                                       placeholder="https://jsonplaceholder.typicode.com/posts"
                                       required>
                            </div>

                        </div>


                        <!-- HEADERS -->
                        <div class="field-section">

                            <div class="field-section-title">Headers</div>

                            <div id="headers">

                                <% for (Map.Entry<String, String> entry : headers.entrySet()) { %>

                                    <div class="pair">

                                        <input name="headerName"
                                               value="<%= escapeHtml(entry.getKey()) %>"
                                               placeholder="Header name">

                                        <input name="headerValue"
                                               value="<%= escapeHtml(entry.getValue()) %>"
                                               placeholder="Header value">

                                    </div>

                                <% } %>

                            </div>

                            <button type="button"
                                    class="workspace-button secondary-button"
                                    onclick="addPair('headers','headerName','headerValue')">
                                + Add Header
                            </button>

                        </div>


                        <!-- QUERY PARAMETERS -->
                        <div class="field-section">

                            <div class="field-section-title">Query Parameters</div>

                            <div id="parameters">

                                <% for (Map.Entry<String, String> entry : parameters.entrySet()) { %>

                                    <div class="pair">

                                        <input name="paramName"
                                               value="<%= escapeHtml(entry.getKey()) %>"
                                               placeholder="Parameter name">

                                        <input name="paramValue"
                                               value="<%= escapeHtml(entry.getValue()) %>"
                                               placeholder="Parameter value">

                                    </div>

                                <% } %>

                            </div>

                            <button type="button"
                                    class="workspace-button secondary-button"
                                    onclick="addPair('parameters','paramName','paramValue')">
                                + Add Parameter
                            </button>

                        </div>


                        <!-- REQUEST BODY -->
                        <div class="field-section">

                            <label for="body">
                                Request Body (for POST, PUT or PATCH)
                            </label>

                            <textarea id="body" name="body"
                                      placeholder='{"title":"Example","body":"Hello","userId":1}'><%= escapeHtml(body) %></textarea>

                        </div>


                        <div class="request-actions">

                            <button type="submit"
                                    class="workspace-button">
                                ▶ Send Request
                            </button>

                        </div>

                    </form>

                </section>


                <!-- RESPONSE VIEWER -->
                <% if (statusObject != null) { %>

                    <section class="workspace-panel response-panel">

                        <div class="panel-heading">

                            <div>
                                <h2>Response</h2>
                                <p class="panel-description">
                                    Result returned by your API request.
                                </p>
                            </div>

                            <button type="button"
                                    class="response-copy"
                                    onclick="copyResponse()">
                                Copy Response
                            </button>

                        </div>

                        <div class="response-meta">

                            <span class="response-status">
                                HTTP Status Code: <%= escapeHtml(String.valueOf(statusObject)) %>
                            </span>

                            <span class="response-label">
                                Response Body
                            </span>

                        </div>

                        <pre id="responseOutput"
                             class="response-output"><%= escapeHtml(responseBody) %></pre>

                    </section>

                <% } else { %>

                    <section class="workspace-panel response-panel">

                        <div class="panel-heading">
                            <div>
                                <h2>Response</h2>
                                <p class="panel-description">
                                    Your API response will appear here.
                                </p>
                            </div>
                        </div>

                        <p class="response-empty">
                            No response available yet. Configure your request
                            and select Send Request to see the result.
                        </p>

                    </section>

                <% } %>

            </div>


            <!-- RIGHT COLUMN -->
            <div class="workspace-column">

                <!-- SAVE REQUEST -->
                <section class="workspace-panel save-panel">

                    <div class="panel-heading">
                        <div>
                            <h2>Save Request</h2>
                            <p class="panel-description">
                                Save your request for future use.
                            </p>
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/api"
                          method="post">

                        <input type="hidden" name="action"
                               value="<%= requestId.isEmpty() ? "save" : "update" %>">

                        <input type="hidden" name="requestId"
                               value="<%= escapeHtml(requestId) %>">


                        <label for="requestName">Request Name</label>

                        <input type="text" id="requestName"
                               name="requestName"
                               value="<%= escapeHtml(requestName) %>"
                               placeholder="Example: Test GET"
                               required>


                        <label for="saveMethod">HTTP Method</label>

                        <select id="saveMethod" name="method">

                            <option value="GET" <%= "GET".equals(method) ? "selected" : "" %>>GET</option>
                            <option value="POST" <%= "POST".equals(method) ? "selected" : "" %>>POST</option>
                            <option value="PUT" <%= "PUT".equals(method) ? "selected" : "" %>>PUT</option>
                            <option value="PATCH" <%= "PATCH".equals(method) ? "selected" : "" %>>PATCH</option>
                            <option value="DELETE" <%= "DELETE".equals(method) ? "selected" : "" %>>DELETE</option>

                        </select>


                        <label for="saveUrl">Request URL</label>

                        <input type="url" id="saveUrl"
                               name="url"
                               value="<%= escapeHtml(url) %>"
                               placeholder="Enter request URL"
                               required>


                        <label for="saveBody">Request Body</label>

                        <textarea id="saveBody"
                                  name="body"
                                  placeholder="Enter request body"><%= escapeHtml(body) %></textarea>


                        <button type="submit"
                                class="workspace-button full-button">

                            <%= requestId.isEmpty() ? "Save Request" : "Update Request" %>

                        </button>

                    </form>

                </section>

            </div>

        </div>

    </main>

</div>


<!-- PAGE JAVASCRIPT -->
<script>

    // Add header and parameter fields
    const addPair = (containerId, firstName, secondName) => {

        const container = document.getElementById(containerId);

        const row = document.createElement("div");
        row.className = "pair";

        const first = document.createElement("input");
        first.name = firstName;
        first.placeholder = firstName === "headerName"
            ? "Header name" : "Parameter name";

        const second = document.createElement("input");
        second.name = secondName;
        second.placeholder = firstName === "headerName"
            ? "Header value" : "Parameter value";

        row.append(first, second);

        container.appendChild(row);
    };


    // Light and Dark theme
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


    // Format JSON response when possible
    const responseOutput = document.getElementById("responseOutput");

    if (responseOutput) {

        const originalResponse = responseOutput.textContent;

        try {
            const jsonResponse = JSON.parse(originalResponse);

            responseOutput.textContent =
                JSON.stringify(jsonResponse, null, 4);

        } catch (error) {
            // Keep the original response if it is not valid JSON.
        }
    }


    // Copy response
    const copyResponse = async () => {

        const output = document.getElementById("responseOutput");

        if (!output) return;

        try {
            await navigator.clipboard.writeText(output.textContent);
            alert("Response copied successfully!");
        } catch (error) {
            alert("Unable to copy response. Please select and copy it manually.");
        }
    };

</script>

</body>
</html>
