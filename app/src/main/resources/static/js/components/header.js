function renderHeader() {
    const headerDiv = document.getElementById("header");

    if (!headerDiv) {
        return;
    }

    // Clear the session when the homepage is opened.
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Check for an invalid or expired session.
    if (
        (role === "loggedPatient" || role === "admin" || role === "doctor") &&
        !token
    ) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    let headerContent = "";

    if (role === "admin") {
        headerContent = `
            <header class="header">
                <a href="/" class="logo">Smart Clinic</a>
                <nav>
                    <button
                        id="addDocBtn"
                        class="adminBtn"
                        onclick="openModal('addDoctor')"
                    >Add Doctor</button>
                    <a href="#" id="logoutBtn">Logout</a>
                </nav>
            </header>
        `;
    } else if (role === "doctor") {
        headerContent = `
            <header class="header">
                <a href="/" class="logo">Smart Clinic</a>
                <nav>
                    <a href="/">Home</a>
                    <a href="#" id="logoutBtn">Logout</a>
                </nav>
            </header>
        `;
    } else if (role === "patient") {
        headerContent = `
            <header class="header">
                <a href="/" class="logo">Smart Clinic</a>
                <nav>
                    <a href="/pages/patientDashboard.html">Login</a>
                    <a href="/pages/patientDashboard.html" id="signupBtn">Sign Up</a>
                </nav>
            </header>
        `;
    } else if (role === "loggedPatient") {
        headerContent = `
            <header class="header">
                <a href="/" class="logo">Smart Clinic</a>
                <nav>
                    <a href="/pages/patientDashboard.html">Home</a>
                    <a href="/pages/patientAppointments.html">Appointments</a>
                    <a href="#" id="logoutPatientBtn">Logout</a>
                </nav>
            </header>
        `;
    }

    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", function (event) {
            event.preventDefault();
            logout();
        });
    }

    const logoutPatientBtn = document.getElementById("logoutPatientBtn");

    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", function (event) {
            event.preventDefault();
            logoutPatient();
        });
    }

    const addDocBtn = document.getElementById("addDocBtn");

    if (addDocBtn) {
        addDocBtn.addEventListener("click", function () {
            if (typeof openModal === "function") {
                openModal("addDoctor");
            }
        });
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

// Make functions accessible to inline handlers and other scripts.
window.renderHeader = renderHeader;
window.logout = logout;
window.logoutPatient = logoutPatient;

renderHeader();
