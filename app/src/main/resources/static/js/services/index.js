
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Open login modals when the buttons are clicked.
window.onload = function () {
    const adminBtn = document.getElementById('adminId');
    const doctorBtn = document.getElementById('doctorId');

    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            localStorage.removeItem('token');
            openModal('adminLogin');
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            localStorage.removeItem('token');
            openModal('doctorLogin');
        });
    }
};

// Admin login handler
window.adminLoginHandler = async function () {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(admin)
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.token);
            selectRole('admin');
            window.location.href = `/adminDashboard/${data.token}`;
        } else {
            alert('Invalid credentials!');
        }

    } catch (error) {
        console.error('Admin login error:', error);
        alert('Something went wrong. Please try again.');
    }
};

// Doctor login handler
window.doctorLoginHandler = async function () {
    const email = document.getElementById('doctorLoginEmail').value.trim();
    const password = document.getElementById('doctorLoginPassword').value;

    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.token);
            selectRole('doctor');
            window.location.href = `/doctorDashboard/${data.token}`;
        } else {
            alert('Invalid credentials!');
        }

    } catch (error) {
        console.error('Doctor login error:', error);
        alert('Something went wrong. Please try again.');
    }
};
