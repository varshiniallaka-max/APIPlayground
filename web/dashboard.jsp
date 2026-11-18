
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>API Playground | Dashboard</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

    <script>
        // Apply saved theme before displaying the page
        (function () {
            const savedTheme = localStorage.getItem("apiPlaygroundTheme") || "light";
            document.documentElement.setAttribute("data-theme", savedTheme);
        })();
    </script>
</head>

<body>

<div class="app-layout">

    <!-- LEFT SIDEBAR -->
    <aside class="sidebar">

        <div class="brand">
            ◈ API Playground
        </div>

        <div class="nav-label">WORKSPACE</div>

        <nav class="nav-links">

            <a class="nav-link active"
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
    <main class="main-content dashboard-content">

        <div class="topbar">
            <div class="user-info">
                Welcome, ${sessionScope.username}
            </div>
        </div>

        <section class="page-heading">

            <h1>Dashboard</h1>

            <p>
                Manage and organize your API requests.
            </p>

        </section>


        <!-- DASHBOARD CARDS -->
        <section class="dashboard-grid">

            <article class="dashboard-card">

                <div class="card-icon">⇄</div>

                <h2>API Playground</h2>

                <p>
                    Create and test HTTP requests.
                </p>

                <a class="card-link"
                   href="${pageContext.request.contextPath}/api">
                    Open Playground →
                </a>

            </article>


            <article class="dashboard-card">

                <div class="card-icon">◷</div>

                <h2>Request History</h2>

                <p>
                    Review previous API executions.
                </p>

                <a class="card-link"
                   href="${pageContext.request.contextPath}/history">
                    View History →
                </a>

            </article>


            <article class="dashboard-card">

                <div class="card-icon">☆</div>

                <h2>Saved Requests</h2>

                <p>
                    Access your saved API requests.
                </p>

                <a class="card-link"
                   href="${pageContext.request.contextPath}/saved">
                    View Saved Requests →
                </a>

            </article>

        </section>

    </main>

</div>


<!-- THEME SWITCHING -->
<script>
    const themeToggle = document.getElementById("themeToggle");

    const updateThemeButton = () => {
        const currentTheme =
            document.documentElement.getAttribute("data-theme");

        if (currentTheme === "dark") {
            themeToggle.textContent = "☀️ Switch to Light Mode";
        } else {
            themeToggle.textContent = "🌙 Switch to Dark Mode";
        }
    };

    const toggleTheme = () => {
        const currentTheme =
            document.documentElement.getAttribute("data-theme");

        const newTheme = currentTheme === "dark" ? "light" : "dark";

        document.documentElement.setAttribute("data-theme", newTheme);

        localStorage.setItem("apiPlaygroundTheme", newTheme);

        updateThemeButton();
    };

    updateThemeButton();
</script>

</body>
</html>
