import { student } from "../data/dashboardData";

function StudentProfile() {
  return (
    <section className="card">
      <h2>Student Profile</h2>
      <p><strong>Name:</strong> {student.name}</p>
      <p><strong>Student ID:</strong> {student.studentId}</p>
      <p><strong>Group:</strong> {student.group}</p>
      <p><strong>Role:</strong> {student.role}</p>
    </section>
  );
}

export default StudentProfile;
