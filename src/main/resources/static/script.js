
const API = "/api";

let medicines = [];
let doses = [];

function $(id) {
    return document.getElementById(id);
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, char => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[char]);
}

function today() {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function showMessage(message, error = false) {
    const box = $("message");
    if (!box) return;

    box.textContent = message;
    box.className = error ? "message error" : "message";
    box.style.display = "block";
}

async function request(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });

    if (!response.ok) {
        const detail = await response.text();
        throw new Error(detail || `Server error: ${response.status}`);
    }

    if (response.status === 204) return null;

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

// -------------------------------
// MEDICINES
// -------------------------------

async function loadMedicines() {
    try {
        medicines = await request(`${API}/medicines`) || [];

        renderMedicineTable();
        renderMedicineOptions();
        updateDashboard();
        renderDashboardMedicineTable();
        renderCaregiverTable();
    } catch (error) {
        if ($("medicineTable")) {
            $("medicineTable").innerHTML =
                `<tr><td colspan="9" class="empty">
                    Unable to load medicines.
                </td></tr>`;
        }

        if ($("dashboardMedicineTable")) {
            $("dashboardMedicineTable").innerHTML =
                `<tr><td colspan="4" class="empty">
                    Unable to load medicines.
                </td></tr>`;
        }

        showMessage(error.message, true);
    }
}

function renderMedicineTable() {
    const table = $("medicineTable");
    if (!table) return;

    if (medicines.length === 0) {
        table.innerHTML =
            '<tr><td colspan="9" class="empty">No medicines added yet.</td></tr>';
        return;
    }

    table.innerHTML = medicines.map(m => `
        <tr>
            <td>${escapeHtml(m.id)}</td>
            <td>${escapeHtml(m.medicineName)}</td>
            <td>${escapeHtml(m.dosage)}</td>
            <td>${escapeHtml(m.scheduleTime)}</td>
            <td>${escapeHtml(m.startDate)}</td>
            <td>${escapeHtml(m.endDate)}</td>
            <td>${escapeHtml(m.patientName)}</td>
            <td>${escapeHtml(m.caregiverName)}</td>
            <td>
                <button class="small"
                    onclick="editMedicine(${Number(m.id)})">Edit</button>
                <button class="small danger"
                    onclick="deleteMedicine(${Number(m.id)})">Delete</button>
            </td>
        </tr>
    `).join("");
}

function renderDashboardMedicineTable() {
    const table = $("dashboardMedicineTable");
    if (!table) return;

    if (medicines.length === 0) {
        table.innerHTML =
            '<tr><td colspan="4" class="empty">No medicines added yet.</td></tr>';
        return;
    }

    table.innerHTML = medicines.map(m => `
        <tr>
            <td>${escapeHtml(m.medicineName)}</td>
            <td>${escapeHtml(m.dosage)}</td>
            <td>${escapeHtml(m.scheduleTime)}</td>
            <td>${escapeHtml(m.patientName)}</td>
        </tr>
    `).join("");
}

function renderMedicineOptions() {
    const select = $("doseMedicine");
    if (!select) return;

    const previous = select.value;

    select.innerHTML = '<option value="">Select medicine</option>' +
        medicines.map(m => `
            <option value="${Number(m.id)}">
                ${escapeHtml(m.medicineName)}
            </option>
        `).join("");

    if (previous) select.value = previous;
}

function resetMedicineForm() {
    const form = $("medicineForm");
    if (!form) return;

    form.reset();
    $("medicineId").value = "";

    if ($("medicineFormTitle")) {
        $("medicineFormTitle").textContent = "Add Medicine";
    }
}

async function saveMedicine(event) {
    event.preventDefault();

    const id = $("medicineId").value;

    const medicine = {
        medicineName: $("medicineName").value.trim(),
        dosage: $("dosage").value.trim(),
        scheduleTime: $("scheduleTime").value,
        startDate: $("startDate").value,
        endDate: $("endDate").value,
        patientName: $("patientName").value.trim(),
        caregiverName: $("caregiverName").value.trim()
    };

    if (medicine.endDate < medicine.startDate) {
        showMessage("End date cannot be before the start date.", true);
        return;
    }

    try {
        if (id) {
            await request(`${API}/medicines/${id}`, {
                method: "PUT",
                body: JSON.stringify(medicine)
            });
            showMessage("Medicine updated successfully.");
        } else {
            await request(`${API}/medicines`, {
                method: "POST",
                body: JSON.stringify(medicine)
            });
            showMessage("Medicine added successfully.");
        }

        resetMedicineForm();
        await loadMedicines();
    } catch (error) {
        showMessage(error.message, true);
    }
}

function editMedicine(id) {
    const medicine = medicines.find(m => Number(m.id) === id);
    if (!medicine) return;

    $("medicineId").value = medicine.id;
    $("medicineName").value = medicine.medicineName || "";
    $("dosage").value = medicine.dosage || "";
    $("scheduleTime").value =
        String(medicine.scheduleTime || "").slice(0, 5);
    $("startDate").value = medicine.startDate || "";
    $("endDate").value = medicine.endDate || "";
    $("patientName").value = medicine.patientName || "";
    $("caregiverName").value = medicine.caregiverName || "";

    $("medicineFormTitle").textContent = "Update Medicine";
    window.scrollTo({ top: 0, behavior: "smooth" });
}

async function deleteMedicine(id) {
    if (!confirm("Are you sure you want to delete this medicine?")) {
        return;
    }

    try {
        await request(`${API}/medicines/${id}`, {
            method: "DELETE"
        });

        showMessage("Medicine deleted successfully.");
        await loadMedicines();
    } catch (error) {
        showMessage(error.message, true);
    }
}

// -------------------------------
// DOSE TRACKING
// -------------------------------

async function loadDoses() {
    try {
        doses = await request(`${API}/doses`) || [];
        renderDoseTable();
        updateDashboard();
    } catch (error) {
        if ($("doseTable")) {
            $("doseTable").innerHTML =
                '<tr><td colspan="6" class="empty">Unable to load doses.</td></tr>';
        }

        showMessage(error.message, true);
    }
}

function medicineName(id) {
    const medicine = medicines.find(m => Number(m.id) === Number(id));
    return medicine ? medicine.medicineName : `ID: ${id}`;
}

function renderDoseTable() {
    const table = $("doseTable");
    if (!table) return;

    if (doses.length === 0) {
        table.innerHTML =
            '<tr><td colspan="6" class="empty">No dose records yet.</td></tr>';
        return;
    }

    table.innerHTML = doses.map(d => {
        const status = String(d.status || "PENDING").toUpperCase();
        const cssStatus = status.toLowerCase();

        return `
            <tr>
                <td>${escapeHtml(d.id)}</td>
                <td>${escapeHtml(medicineName(d.medicineId))}</td>
                <td>${escapeHtml(d.doseDate)}</td>
                <td>
                    <span class="status ${cssStatus}">
                        ${escapeHtml(status)}
                    </span>
                </td>
                <td>${escapeHtml(d.takenAt || "—")}</td>
                <td>
                    ${status !== "TAKEN"
                        ? `<button class="small"
                            onclick="markDoseTaken(${Number(d.id)})">
                            Mark Taken
                           </button>`
                        : "—"}
                    <button class="small danger"
                        onclick="deleteDose(${Number(d.id)})">
                        Delete
                    </button>
                </td>
            </tr>
        `;
    }).join("");
}

async function saveDose(event) {
    event.preventDefault();

    const dose = {
        medicineId: Number($("doseMedicine").value),
        doseDate: $("doseDate").value,
        status: $("doseStatus").value
    };

    if (!dose.medicineId) {
        showMessage("Please select a medicine.", true);
        return;
    }

    try {
        await request(`${API}/doses`, {
            method: "POST",
            body: JSON.stringify(dose)
        });

        showMessage("Dose record saved successfully.");
        $("doseForm").reset();
        $("doseDate").value = today();

        await loadDoses();
    } catch (error) {
        showMessage(error.message, true);
    }
}

async function markDoseTaken(id) {
    try {
        await request(`${API}/doses/${id}/taken`, {
            method: "PUT"
        });

        showMessage("Dose marked as taken.");
        await loadDoses();
    } catch (error) {
        showMessage(error.message, true);
    }
}

async function deleteDose(id) {
    if (!confirm("Are you sure you want to delete this dose?")) {
        return;
    }

    try {
        await request(`${API}/doses/${id}`, {
            method: "DELETE"
        });

        showMessage("Dose deleted successfully.");
        await loadDoses();
    } catch (error) {
        showMessage(error.message, true);
    }
}

// -------------------------------
// DASHBOARD
// -------------------------------

function updateDashboard() {
    if ($("totalMedicines")) {
        $("totalMedicines").textContent = medicines.length;
    }

    const taken = doses.filter(
        d => String(d.status).toUpperCase() === "TAKEN"
    ).length;

    const missed = doses.filter(
        d => String(d.status).toUpperCase() === "MISSED"
    ).length;

    if ($("dosesTaken")) {
        $("dosesTaken").textContent = taken;
    }

    if ($("missedDoses")) {
        $("missedDoses").textContent = missed;
    }

    if ($("totalDoseRecords")) {
        $("totalDoseRecords").textContent = doses.length;
    }
}

// -------------------------------
// CAREGIVER
// -------------------------------

function renderCaregiverTable() {
    const table = $("caregiverTable");
    if (!table) return;

    if (medicines.length === 0) {
        table.innerHTML =
            '<tr><td colspan="5" class="empty">No patient information available. Add medicines first.</td></tr>';
    } else {
        table.innerHTML = medicines.map(m => `
            <tr>
                <td>${escapeHtml(m.patientName || "Not provided")}</td>
                <td>${escapeHtml(m.caregiverName || "Not provided")}</td>
                <td>${escapeHtml(m.medicineName)}</td>
                <td>${escapeHtml(m.dosage)}</td>
                <td>${escapeHtml(m.scheduleTime)}</td>
            </tr>
        `).join("");
    }

    if ($("totalPatients")) {
        const patients = new Set(
            medicines.map(m => m.patientName).filter(Boolean)
        );
        $("totalPatients").textContent = patients.size;
    }

    if ($("totalCaregivers")) {
        const caregivers = new Set(
            medicines.map(m => m.caregiverName).filter(Boolean)
        );
        $("totalCaregivers").textContent = caregivers.size;
    }

    if ($("assignedMedicines")) {
        $("assignedMedicines").textContent =
            medicines.filter(m => m.caregiverName).length;
    }
}

// -------------------------------
// INITIALIZE CURRENT PAGE
// -------------------------------

document.addEventListener("DOMContentLoaded", async () => {
    const username = sessionStorage.getItem("careplanUser");

    if ($("welcomeName") && username) {
        $("welcomeName").textContent = username;
    }

    if ($("medicineForm")) {
        $("medicineForm").addEventListener("submit", saveMedicine);
    }

    if ($("doseForm")) {
        $("doseForm").addEventListener("submit", saveDose);
    }

    if ($("doseDate")) {
        $("doseDate").value = today();
    }

    const page = document.body.dataset.page;

    if (page === "dashboard") {
        await Promise.all([loadMedicines(), loadDoses()]);
    } else if (page === "medicines") {
        await loadMedicines();
    } else if (page === "doses") {
        await Promise.all([loadMedicines(), loadDoses()]);
    } else if (page === "caregiver") {
        await loadMedicines();
    }
});
