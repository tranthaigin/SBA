import { groupProject } from "../data/dashboardData";

function ProjectSummary() {
  return (
    <section className="card">
      <h2>Group Project Summary</h2>
      <p><strong>Project:</strong> {groupProject.name}</p>
      <p><strong>Target users:</strong> {groupProject.targetUsers}</p>
      <h3>Core Features</h3>
      <ol>
        {groupProject.coreFeatures.map((feature) => (
          <li key={feature}>{feature}</li>
        ))}
      </ol>
    </section>
  );
}

export default ProjectSummary;
