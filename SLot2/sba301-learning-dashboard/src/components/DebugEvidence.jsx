import { debugChecklist } from "../data/dashboardData";

function DebugEvidence() {
  return (
    <section className="card">
      <h2>Debug Evidence (E4)</h2>
      <ul className="check-list">
        {debugChecklist.map((item) => (
          <li key={item.id} style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <span style={{ color: item.done ? "#16a34a" : "#dc2626", fontWeight: "bold" }}>
              {item.done ? "✓ PASS" : "✗ FAIL"}
            </span>
            <span>{item.label}</span>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default DebugEvidence;
