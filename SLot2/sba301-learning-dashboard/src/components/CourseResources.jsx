import { courseResources } from "../data/course";

function CourseResources() {
  return (
    <section className="card">
      <h2>Course Resources (E2)</h2>
      <ul className="check-list">
        {courseResources.map((res) => (
          <li key={res.id}>
            <strong>[{res.type}]</strong> {res.title} -{" "}
            <a href={res.url} target="_blank" rel="noreferrer">
              {res.url}
            </a>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default CourseResources;
