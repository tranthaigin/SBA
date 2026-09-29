import { environmentTools } from "../data/dashboardData";

function EnvironmentStatus() {
  return (
    <section className="card">
      <h2>Environment Status</h2>
      <div className="status-list">
        {environmentTools.map((tool) => {
          const badgeClass = tool.status === "Ready" ? "badge ready" : "badge pending";
          return (
            <div className="status-row" key={tool.name}>
              <span>{tool.name}</span>
              <span className={badgeClass}>{tool.status}</span>
            </div>
          );
        })}
      </div>
    </section>
  );
}

export default EnvironmentStatus;
