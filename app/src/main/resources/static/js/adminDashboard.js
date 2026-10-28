import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

document.getElementById('addDocBtn').addEventListener('click', () => {
    openModal('addDoctor');
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error('Error loading doctors:', error);
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById('content');
    contentDiv.innerHTML = '';

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = '<p>No doctors found</p>';
        return;
    }

    doctors.forEach(doctor => {
        contentDiv.appendChild(createDoctorCard(doctor));
    });
}

async function filterDoctorsOnChange() {
    const searchValue = document.getElementById('searchBar').value;
    const filterTimeValue = document.getElementById('filterTime').value;
    const filterSpecialtyValue = document.getElementById('filterSpecialty').value;

    try {
        const doctors = await filterDoctors(
            searchValue,
            filterTimeValue,
            filterSpecialtyValue
        );

        renderDoctorCards(doctors);
    } catch (error) {
        console.error('Error filtering doctors:', error);
    }
}

document.getElementById('searchBar').addEventListener('input', filterDoctorsOnChange);
document.getElementById('filterTime').addEventListener('change', filterDoctorsOnChange);
document.getElementById('filterSpecialty').addEventListener('change', filterDoctorsOnChange);

async function adminAddDoctor(event) {
    event.preventDefault();

    const token = localStorage.getItem('token');

    if (!token) {
        alert('Valid login token required.');
        return;
    }

    const form = document.getElementById('addDoctorForm');
    const formData = new FormData(form);

    const doctorData = {
        name: formData.get('name'),
        specialty: formData.get('specialty'),
        email: formData.get('email'),
        password: formData.get('password'),
        mobileNo: formData.get('mobileNo'),
        availabilityTime: formData.get('availabilityTime'),
        availability: formData.getAll('availability')
    };

    try {
        await saveDoctor(doctorData, token);

        alert('Doctor added successfully.');
        document.getElementById('addDoctorModal').style.display = 'none';

        await loadDoctorCards();
    } catch (error) {
        alert(error.message || 'Failed to add doctor.');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadDoctorCards();

    const addDoctorForm = document.getElementById('addDoctorForm');

    if (addDoctorForm) {
        addDoctorForm.addEventListener('submit', adminAddDoctor);
    }
});