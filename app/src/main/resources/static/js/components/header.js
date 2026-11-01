
function renderHeader() {
    const headerDiv = document.getElementById("header");

    if (!headerDiv) {
        return;
    }

    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = "";
        return;
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

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
            <a href="/" class="logo">
            <img src="/assets/images/logo/logo.png" alt="Smart Clinic Logo" class="logo-img">
        </a>                <nav>
                    <button id="addDocBtn" class="adminBtn">Add Doctor</button>
                    <a href="#" id="logoutBtn">Logout</a>
                </nav>
            </header>
        `;
    }

    // Doctor header
    else if (role === "doctor") {
        headerContent = `
            <header class="header">
                <a href="/" class="logo">Smart Clinic</a>
                <nav>
                    <a href="/">Home</a>
                    <a href="#" id="logoutBtn">Logout</a>
                </nav>
            </header>
        `;
    }

    // Patient header
    else if (role === "patient") {
        headerContent = `
            <header class="header">
                <a href="/" class="logo">Smart Clinic</a>
                <nav>
                    <a href="/pages/patientDashboard.html">Login</a>
                    <a href="/pages/patientDashboard.html" id="signupBtn">Sign Up</a>
                </nav>
            </header>
        `;
    }

    // Logged-in patient header
    else if (role === "loggedPatient") {
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

    // Attach listeners after inserting the header.
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

// Logout admin and doctor.
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

// Logout patient and return to the patient login page.
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

// Make functions accessible to other scripts.
window.renderHeader = renderHeader;
window.logout = logout;
window.logoutPatient = logoutPatient;

renderHeader();
