
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = `${API_BASE_URL}/admin`;
const DOCTOR_API = `${API_BASE_URL}/doctor/login`;

// Open the login modals when the buttons are clicked.
window.addEventListener("load", function () {
    const adminButton = document.getElementById("adminId");
    const doctorButton = document.getElementById("doctorId");
  
    if (adminButton) {
      adminButton.addEventListener("click", function () {
        openModal("adminLogin");
      });
    }
  
    if (doctorButton) {
      doctorButton.addEventListener("click", function () {
        openModal("doctorLogin");
      });
    }
  });

// Handle Admin login.
window.adminLoginHandler = async function () {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  const admin = {
    username: username,
    password: password
  };

  try {
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(admin)
    });

    const data = await response.json();

    if (response.ok) {
      localStorage.setItem("token", data.token);
      selectRole("admin");
    } else {
      alert(data.error || "Invalid username or password.");
    }
  } catch (error) {
    console.error("Admin login error:", error);
    alert("Something went wrong. Please try again.");
  }
};

// Handle Doctor login.
window.doctorLoginHandler = async function () {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const doctor = {
    identifier: email,
    password: password
  };

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json();

    if (response.ok) {
      localStorage.setItem("token", data.token);
      selectRole("doctor");
    } else {
      alert(data.error || "Invalid email or password.");
    }
  } catch (error) {
    console.error("Doctor login error:", error);
    alert("Something went wrong. Please try again.");
  }
};
