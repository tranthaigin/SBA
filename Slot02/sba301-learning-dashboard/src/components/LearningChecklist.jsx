import { learningItems } from "../data/dashboardData";

function LearningChecklist() {
  return (
    <section className="card">
      <h2>Slot 02 Learning Checklist</h2>
      <ul className="check-list">
        {learningItems.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </section>
  );
}

export default LearningChecklist;
