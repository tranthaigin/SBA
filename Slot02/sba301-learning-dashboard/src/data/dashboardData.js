export { course, courseResources } from "./course";
export { student } from "./student";

export const learningItems = [
  "React mental model",
  "Vite project workflow",
  "JSX and JavaScript expressions",
  "Functional components",
  "Import and export",
  "Component composition",
  "Browser and console verification",
  "Git checkpoint"
];

// E3: One tool with status 'Pending' to demonstrate conditional ternary styling
export const environmentTools = [
  { name: "Node.js (v24)", status: "Ready" },
  { name: "npm (v11)", status: "Ready" },
  { name: "IntelliJ IDEA", status: "Ready" },
  { name: "Vite 5.x", status: "Ready" },
  { name: "Git CLI", status: "Ready" },
  { name: "Spring Boot Backend (Slot 12+)", status: "Pending" }
];

export const groupProject = {
  name: "FUNewsManagementSystem",
  targetUsers: "Students, Staff, and System Administrators",
  coreFeatures: [
    "Role-based Dashboard & Article Management",
    "Real-time News Publishing & Category Filtering",
    "Audit Trail, Tagging & Secure Authentication"
  ]
};

// E4: Debug evidence checklist
export const debugChecklist = [
  { id: "term", label: "Terminal checked - No build/compile errors", done: true },
  { id: "cons", label: "Browser Console checked - 0 errors/warnings", done: true },
  { id: "git", label: "Git committed - Working tree clean with checkpoint", done: true }
];
