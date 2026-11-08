import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';

import { createDoctorCard } from './components/doctorCard.js';

// Load all doctors from the backend.
async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error('Error loading doctors:', error);
    }
}

// Display doctor cards.
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById('content');

    if (!contentDiv) return;

    contentDiv.innerHTML = '';

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = '<p>No doctors found</p>';
        return;
    }

    doctors.forEach(doctor => {
        contentDiv.appendChild(createDoctorCard(doctor));
    });
}

// Search and filter doctors.
async function filterDoctorsOnChange() {
    const searchValue = document.getElementById('searchBar').value;
    const filterTimeValue = document.getElementById('filterTime').value;
    const filterSpecialtyValue = document.getElementById('filterSpecialty').value;

    try {
        const result = await filterDoctors(
            searchValue,
            filterTimeValue,
            filterSpecialtyValue
        );

        renderDoctorCards(result.doctors || []);
    } catch (error) {
        console.error('Error filtering doctors:', error);
    }
}

// Add a new doctor.
async function adminAddDoctor() {
    const token = localStorage.getItem('token');

    if (!token) {
        alert('Valid login token required.');
        return;
    }

    const doctorData = {
        name: document.getElementById('doctorName').value,
        specialty: document.getElementById('specialization').value,
        email: document.getElementById('doctorEmail').value,
        password: document.getElementById('doctorPassword').value,
        mobileNo: document.getElementById('doctorPhone').value,
        availability: Array.from(
            document.querySelectorAll(
                'input[name="availability"]:checked'
            )
        ).map(input => input.value)
    };

    try {
        const result = await saveDoctor(doctorData, token);

        if (result.success) {
            alert('Doctor added successfully.');

            document.getElementById('modal').style.display = 'none';

            await loadDoctorCards();
        } else {
            alert(result.message);
        }
    } catch (error) {
        console.error('Error adding doctor:', error);
        alert(error.message || 'Failed to add doctor.');
    }
}

// Make the function accessible to modals.js.
window.adminAddDoctor = adminAddDoctor;

// Initialize dashboard.
document.addEventListener('DOMContentLoaded', () => {
    loadDoctorCards();

    const searchBar = document.getElementById('searchBar');
    const filterTime = document.getElementById('filterTime');
    const filterSpecialty = document.getElementById('filterSpecialty');

    if (searchBar) {
        searchBar.addEventListener('input', filterDoctorsOnChange);
    }

    if (filterTime) {
        filterTime.addEventListener('change', filterDoctorsOnChange);
    }

    if (filterSpecialty) {
        filterSpecialty.addEventListener('change', filterDoctorsOnChange);
    }
});
