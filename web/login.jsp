
<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>API Playground | Login</title>

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

<div class="auth-page">

    <div class="auth-card">

        <button type="button"
                class="auth-theme-button"
                id="authThemeToggle"
                onclick="toggleAuthTheme()">
            🌙 Switch to Dark Mode
        </button>

        <div class="auth-brand">
            ◈ API Playground
        </div>

        <div class="auth-heading">

            <h1>Welcome Back</h1>

            <p>Login to your account</p>

        </div>


        <!-- ERROR MESSAGE -->

        <% if (request.getAttribute("error") != null) { %>

            <div class="auth-message auth-error">
                <%= request.getAttribute("error") %>
            </div>

        <% } %>


        <!-- REGISTRATION SUCCESS -->

        <% if ("true".equals(request.getParameter("registered"))) { %>

            <div class="auth-message auth-success">
                Registration successful! Please log in.
            </div>

        <% } %>


        <!-- LOGIN FORM -->

        <form action="${pageContext.request.contextPath}/login"
              method="post">

            <label for="username">
                Username
            </label>

            <input type="text"
                   id="username"
                   name="username"
                   placeholder="Enter your username"
                   required
                   autocomplete="username">


            <label for="password">
                Password
            </label>

            <div class="auth-password-wrap">

                <input type="password"
                       id="password"
                       name="password"
                       placeholder="Enter your password"
                       required
                       autocomplete="current-password">

                <button type="button"
                        class="password-toggle"
                        onclick="togglePassword()"
                        id="passwordToggle">
                    Show
                </button>

            </div>


            <button type="submit"
                    class="auth-submit">
                Login
            </button>

        </form>


        <div class="auth-link">

            Don't have an account?

            <a href="${pageContext.request.contextPath}/register.jsp">
                Register
            </a>

        </div>

    </div>

</div>


<script>

    // LIGHT AND DARK THEME

    const authThemeToggle =
        document.getElementById("authThemeToggle");

    const updateAuthThemeButton = () => {

        const currentTheme =
            document.documentElement.getAttribute("data-theme");

        authThemeToggle.textContent =
            currentTheme === "dark"
            ? "☀️ Switch to Light Mode"
            : "🌙 Switch to Dark Mode";
    };

    const toggleAuthTheme = () => {

        const currentTheme =
            document.documentElement.getAttribute("data-theme");

        const newTheme =
            currentTheme === "dark" ? "light" : "dark";

        document.documentElement.setAttribute("data-theme", newTheme);

        localStorage.setItem("apiPlaygroundTheme", newTheme);

        updateAuthThemeButton();
    };

    updateAuthThemeButton();


    // SHOW AND HIDE PASSWORD

    const togglePassword = () => {

        const passwordInput =
            document.getElementById("password");

        const passwordToggle =
            document.getElementById("passwordToggle");

        if (passwordInput.type === "password") {

            passwordInput.type = "text";

            passwordToggle.textContent = "Hide";

        } else {

            passwordInput.type = "password";

            passwordToggle.textContent = "Show";
        }
    };

</script>

</body>
</html>
