
import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;

// Fetch all doctors.
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);

    if (!response.ok) {
      throw new Error(`Failed to fetch doctors: ${response.status}`);
    }

    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

// Delete a doctor using their ID and authentication token.
export async function deleteDoctor(doctorId, token) {
  try {
    const response = await fetch(
      `${DOCTOR_API}/${doctorId}/${token}`,
      { method: "DELETE" }
    );

    const data = await response.json().catch(() => ({}));

    return {
      success: response.ok,
      message: data.message || data.error || (
        response.ok ? "Doctor deleted successfully." : "Failed to delete doctor."
      )
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);

    return {
      success: false,
      message: "Could not connect to the server."
    };
  }
}

// Create a new doctor using an authentication token.
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(doctor)
    });

    const data = await response.json().catch(() => ({}));

    return {
      success: response.ok,
      message: data.message || data.error || (
        response.ok ? "Doctor saved successfully." : "Failed to save doctor."
      )
    };
  } catch (error) {
    console.error("Error saving doctor:", error);

    return {
      success: false,
      message: "Could not connect to the server."
    };
  }
}

export async function filterDoctors(name, time, specialty) {
    try {
        const searchName = name?.trim() || "all";
        const searchTime = time || "all";
        const searchSpecialty = specialty || "all";

        // If no filters are selected, load all doctors.
        if (
            searchName === "all" &&
            searchTime === "all" &&
            searchSpecialty === "all"
        ) {
            return await getDoctors();
        }

        const params = [
            searchName,
            searchTime,
            searchSpecialty
        ].map(value => encodeURIComponent(value));

        const response = await fetch(
            `${DOCTOR_API}/filter/${params.join("/")}`
        );

        if (!response.ok) {
            throw new Error(`Doctor filtering failed: ${response.status}`);
        }

        const data = await response.json();

        return Array.isArray(data)
            ? { doctors: data }
            : { doctors: data.doctors || [] };

    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Unable to filter doctors. Please try again.");
        return { doctors: [] };
    }
}

export async function loginDoctor(loginData) {
    try {
        const response = await fetch(`${DOCTOR_API}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginData)
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(
                data.message || data.error || "Doctor login failed."
            );
        }

        return data;

    } catch (error) {
        console.error("Doctor login error:", error);
        throw error;
    }
}
