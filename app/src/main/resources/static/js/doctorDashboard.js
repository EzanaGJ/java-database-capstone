
import { API_BASE_URL } from "./config/config.js";
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const APPOINTMENT_API = `${API_BASE_URL}/appointments`;

const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayButton = document.getElementById("todayButton");
const datePicker = document.getElementById("datePicker");

function getLocalDate() {
    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

// Empty date means show all appointments
let selectedDate = "";
const token = localStorage.getItem("token");
let patientName = "all";

// Display messages inside the table
function showMessage(message) {
    tableBody.innerHTML = "";

    const row = document.createElement("tr");
    const cell = document.createElement("td");

    cell.colSpan = 8;
    cell.classList.add("noPatientRecord");
    cell.textContent = message;

    row.appendChild(cell);
    tableBody.appendChild(row);
}

// Get all appointments or filter by date
async function loadAppointments() {

    if (!tableBody) {
        console.error("Patient table body was not found.");
        return;
    }

    if (!token) {
        showMessage("Please log in again.");
        return;
    }

    try {
        let response;

        if (selectedDate) {

            // Get appointments for selected date
            response = await getAllAppointments(
                selectedDate,
                patientName,
                token
            );

        } else {

            // Get all appointments for this doctor
            const url =
                `${APPOINTMENT_API}/all/${encodeURIComponent(patientName)}/${token}`;

            const result = await fetch(url);

            if (!result.ok) {
                throw new Error("Failed to fetch appointments");
            }

            response = await result.json();
        }

        console.log("Raw appointments response:", response);

        let appointments = [];

        if (Array.isArray(response)) {
            appointments = response;
        } else if (Array.isArray(response?.appointments)) {
            appointments = response.appointments;
        } else if (Array.isArray(response?.data)) {
            appointments = response.data;
        } else {
            console.error("Unexpected response:", response);
            showMessage("Could not read appointments from the server.");
            return;
        }

        tableBody.innerHTML = "";

        if (appointments.length === 0) {
            showMessage("No appointments found.");
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

            // Appointment date
            const dateCell = document.createElement("td");

            const appointmentDate = appointment.appointmentDate;

            if (appointmentDate) {
                const [year, month, day] = appointmentDate.split("-");
                dateCell.textContent = `${day}/${month}/${year}`;
            } else {
                dateCell.textContent = "N/A";
            }

            // Appointment time
            const timeCell = document.createElement("td");
            timeCell.textContent =
                appointment.appointmentTimeOnly ?? "N/A";

            // Appointment status
            const statusCell = document.createElement("td");

            if (Number(appointment.status) === 0) {
                statusCell.textContent = "Scheduled";
            } else if (Number(appointment.status) === 1) {
                statusCell.textContent = "Completed";
            } else {
                statusCell.textContent = "Unknown";
            }

            // Add columns before prescription column
            row.insertBefore(dateCell, row.children[4]);
            row.insertBefore(timeCell, row.children[5]);
            row.insertBefore(statusCell, row.children[6]);

            tableBody.appendChild(row);
        });

        console.log("Appointments displayed:", appointments.length);

    } catch (error) {
        console.error("Error loading appointments:", error);
        showMessage("Error loading appointments. Try again later.");
    }
}

// Search by patient name
if (searchBar) {
    searchBar.addEventListener("input", () => {
        patientName = searchBar.value.trim() || "all";
        loadAppointments();
    });
}

// Today's appointments
if (todayButton) {
    todayButton.addEventListener("click", () => {
        selectedDate = getLocalDate();

        if (datePicker) {
            datePicker.value = selectedDate;
        }

        loadAppointments();
    });
}

// Filter by selected date
if (datePicker) {
    datePicker.addEventListener("change", () => {
        selectedDate = datePicker.value || "";

        loadAppointments();
    });

    datePicker.value = "";
}

// Initially display all appointments
loadAppointments();
