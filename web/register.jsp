
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%!
    private String escapeHtml(String value) {
        if (value == null) return "";

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>API Playground - Register</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

    <script>
        const savedTheme = localStorage.getItem("apiPlaygroundTheme") || "light";
        document.documentElement.setAttribute("data-theme", savedTheme);
    </script>
</head>

<body class="auth-page">

    <button type="button"
            class="auth-theme-button"
            onclick="toggleTheme()"
            id="themeButton">
        🌙 Dark Mode
    </button>

    <div class="auth-card">

        <div class="auth-brand">
            <span>API</span> Playground
        </div>

        <h1 class="auth-heading">Create Account</h1>

        <p class="auth-subtitle">
            Register for API Playground
        </p>

        <% if (request.getAttribute("error") != null) { %>
            <div class="auth-message auth-error">
                <%= escapeHtml(String.valueOf(request.getAttribute("error"))) %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/register"
              method="post">

            <label for="username">Username</label>
            <input type="text"
                   id="username"
                   name="username"
                   placeholder="Enter your username"
                   required
                   maxlength="50"
                   autocomplete="username">

            <label for="email">Email</label>
            <input type="email"
                   id="email"
                   name="email"
                   placeholder="Enter your email"
                   required
                   maxlength="150"
                   autocomplete="email">

            <label for="password">Password</label>

            <div class="auth-password-wrap">
                <input type="password"
                       id="password"
                       name="password"
                       placeholder="Create a password"
                       required
                       minlength="8"
                       autocomplete="new-password">

                <button type="button"
                        class="password-toggle"
                        onclick="togglePassword()"
                        id="passwordToggle">
                    Show
                </button>
            </div>

            <button type="submit" class="auth-submit">
                Create Account
            </button>

        </form>

        <p class="auth-link">
            Already have an account?
            <a href="${pageContext.request.contextPath}/login.jsp">
                Login
            </a>
        </p>

    </div>

    <script>
        const toggleTheme = () => {
            const currentTheme =
                document.documentElement.getAttribute("data-theme");

            const newTheme =
                currentTheme === "dark" ? "light" : "dark";

            document.documentElement.setAttribute("data-theme", newTheme);

            localStorage.setItem("apiPlaygroundTheme", newTheme);

            updateThemeButton();
        };

        const updateThemeButton = () => {
            const currentTheme =
                document.documentElement.getAttribute("data-theme");

            document.getElementById("themeButton").textContent =
                currentTheme === "dark" ? "☀️ Light Mode" : "🌙 Dark Mode";
        };

        const togglePassword = () => {
            const password = document.getElementById("password");
            const button = document.getElementById("passwordToggle");

            if (password.type === "password") {
                password.type = "text";
                button.textContent = "Hide";
            } else {
                password.type = "password";
                button.textContent = "Show";
            }
        };

        updateThemeButton();
    </script>

</body>
</html>
