## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(250), NOT NULL
- last_name: VARCHAR(250), NOT NULL
- date_of_birth: DATE, NULL
- email: VARCHAR(250), UNIQUE, NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- phonenumber: VARCHAR(20), UNIQUE, NOT NULL
- address:VARCHAR(255), NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP

### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(250), NOT NULL
- last_name: VARCHAR(250), NOT NULL
- specialty: VARCHAR(250), NOT NULL
- email: VARCHAR(250), UNIQUE, NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
- is_active: BOOLEAN, NOT NULL, DEFAULT TRUE

 ### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

### Table: admin 
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(250), NOT NULL
- email: VARCHAR(250), UNIQUE, NOT NULL
- password_hash: VARCHAR(255), NOT NULL

### Table: clinic_locations
- id: INT, Primary Key, Auto Increment
- clinic_name: VARCHAR(250), NOT NULL
- address: VARCHAR(250), NOT NULL

### Table: payments 
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key, NOT NULL → appointments(id)
- patient_id: INT, Foreign Key, NOT NULL → patients(id)
- total: DECIMAL(10,2), NOT NULL
- status: INT (0 = Failed, 1 = Successful, 2 = Pending)
- payment_date: DATETIME, NULL
- created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP


## MongoDB Collection Design

### Collection: feedback
```json

{
  "patientName": "James Say",
  "appointmentId": 50,
  "doctorName": "Jane Max",
  "rating": 5,
  "comment": "The doctor was helpful and explained everything clearly.",
  "submittedAt": "2026-10-20T14:30:00Z",
  "tags": ["communication", "professionalism"]
}
```
   


```json
{
"_id": "
