
import { deleteDoctor } from "../services/doctorServices.js";

export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

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

            if (!confirmed) return;

            const token = localStorage.getItem("token");

            const result = await deleteDoctor(doctor.id, token);

            if (result.success) {
                card.remove();
            } else {
                alert(result.message);
            }
        });

        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient" || role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.type = "button";

        bookNow.addEventListener("click", () => {
            if (role === "patient") {
                alert("Patient needs to login first.");
            } else {
                alert("Booking functionality is not implemented yet.");
            }
        });

        actionsDiv.appendChild(bookNow);
    }

    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    return card;
}
