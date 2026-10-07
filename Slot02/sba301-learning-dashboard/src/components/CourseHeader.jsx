import { course } from "../data/dashboardData";

function CourseHeader() {
  return (
    <header className="hero">
      <p className="eyebrow">{course.slot} • {course.topic}</p>
      <h1>{course.code}</h1>
      <p className="course-name">{course.name}</p>
    </header>
  );
}

export default CourseHeader;
