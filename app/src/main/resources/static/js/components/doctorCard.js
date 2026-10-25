import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "./modals.js";

export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    // Doctor information
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name || "";

    const specialization = document.createElement("p");
    specialization.textContent =
        `Specialty: ${doctor.specialization || doctor.specialty || ""}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email || ""}`;

    const availability = document.createElement("p");
    const times = Array.isArray(doctor.availability)
        ? doctor.availability.join(", ")
        : doctor.availability || "";

    availability.textContent = `Availability: ${times}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // Action buttons
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.type = "button";

        removeBtn.addEventListener("click", async () => {
            const confirmed = window.confirm(
                `Are you sure you want to delete ${doctor.name}?`
            );

            if (!confirmed) {
                return;
            }

            const token = localStorage.getItem("token");

            try {
                await deleteDoctor(doctor.id, token);
                card.remove();
            } catch (error) {
                console.error("Failed to delete doctor:", error);
                alert("Unable to delete this doctor. Please try again.");
            }
        });

        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.type = "button";

        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });

        actionsDiv.appendChild(bookNow);
    } else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.type = "button";

        bookNow.addEventListener("click", async (event) => {
            const token = localStorage.getItem("token");

            try {
                const patientData = await getPatientData(token);
                showBookingOverlay(event, doctor, patientData);
            } catch (error) {
                console.error("Failed to retrieve patient data:", error);
                alert("Unable to load your patient information. Please log in again.");
            }
        });

        actionsDiv.appendChild(bookNow);
    }

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
