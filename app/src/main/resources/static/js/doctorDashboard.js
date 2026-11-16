
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayButton = document.getElementById("todayButton");
const datePicker = document.getElementById("datePicker");

// Get the current date using local time
function getLocalDate() {
    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

let selectedDate = getLocalDate();
const token = localStorage.getItem("token");
let patientName = "all";

// Display a message inside the table
function showMessage(message) {
    tableBody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");

    cell.colSpan = 5;
    cell.classList.add("noPatientRecord");
    cell.textContent = message;

    row.appendChild(cell);
    tableBody.appendChild(row);
}

// Load appointments
async function loadAppointments() {
    if (!tableBody) {
        console.error("Patient table body was not found.");
        return;
    }

    if (!token) {
        console.error("Doctor token was not found.");
        showMessage("Please log in again.");
        return;
    }

    try {
        const response = await getAllAppointments(
            selectedDate,
            patientName,
            token
        );

        console.log("Raw appointments response:", response);

        // Handle different possible response structures
        let appointments = [];

        if (Array.isArray(response)) {
            appointments = response;
        } else if (Array.isArray(response?.appointments)) {
            appointments = response.appointments;
        } else if (Array.isArray(response?.data)) {
            appointments = response.data;
        } else {
            console.error("Unexpected appointments response:", response);
            showMessage("Could not read appointments from the server.");
            return;
        }

        tableBody.innerHTML = "";

        if (appointments.length === 0) {
            showMessage("No appointments found for this date.");
            return;
        }

        appointments.forEach(appointment => {
            const patient = {
                id: appointment.patient?.id ?? appointment.patientId ?? "",
                name: appointment.patient?.name ?? appointment.patientName ?? "",
                phone: appointment.patient?.phone ?? appointment.phone ?? "",
                email: appointment.patient?.email ?? appointment.email ?? ""
            };

            const row = createPatientRow(
                patient,
                appointment.id,
                appointment.doctor?.id ?? appointment.doctorId ?? ""
            );

            tableBody.appendChild(row);
        });

        console.log("Appointments displayed:", appointments.length);

    } catch (error) {
        console.error("Error loading appointments:", error);
        showMessage("Error loading appointments. Try again later.");
    }
}

if (searchBar) {
    searchBar.addEventListener("input", () => {
        patientName = searchBar.value.trim() || "all";
        loadAppointments();
    });
}

if (todayButton) {
    todayButton.addEventListener("click", () => {
        selectedDate = getLocalDate();

        if (datePicker) {
            datePicker.value = selectedDate;
        }

        loadAppointments();
    });
}

if (datePicker) {
    datePicker.addEventListener("change", () => {
        selectedDate = datePicker.value || getLocalDate();
        loadAppointments();
    });
}

if (datePicker) {
    datePicker.value = selectedDate;
}

loadAppointments();
