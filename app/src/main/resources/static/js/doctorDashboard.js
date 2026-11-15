
import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRow.js";

const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayButton = document.getElementById("todayButton");
const datePicker = document.getElementById("datePicker");

let selectedDate = new Date().toISOString().split("T")[0];
const token = localStorage.getItem("token");
let patientName = "null";

async function loadAppointments() {
    if (!tableBody) {
        console.error("Patient table body was not found.");
        return;
    }

    try {
        const appointments = await getAllAppointments(
            selectedDate,
            patientName,
            token
        );

        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");

            cell.colSpan = 5;
            cell.classList.add("noPatientRecord");
            cell.textContent = "No Appointments found for today.";

            row.appendChild(cell);
            tableBody.appendChild(row);
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

    } catch (error) {
        console.error("Error loading appointments:", error);

        tableBody.innerHTML = "";

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 5;
        cell.classList.add("noPatientRecord");
        cell.textContent = "Error loading appointments. Try again later.";

        row.appendChild(cell);
        tableBody.appendChild(row);
    }
}

if (searchBar) {
    searchBar.addEventListener("input", () => {
        patientName = searchBar.value.trim() || "null";
        loadAppointments();
    });
}

if (todayButton) {
    todayButton.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split("T")[0];

        if (datePicker) {
            datePicker.value = selectedDate;
        }

        loadAppointments();
    });
}

if (datePicker) {
    datePicker.addEventListener("change", () => {
        selectedDate = datePicker.value || new Date().toISOString().split("T")[0];
        loadAppointments();
    });
}

document.addEventListener("DOMContentLoaded", async () => {
    if (datePicker) {
        datePicker.value = selectedDate;
    }

    await loadAppointments();
});
