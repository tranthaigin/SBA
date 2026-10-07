import http from "node:http";
import { spawn } from "node:child_process";
import path from "node:path";
import fs from "node:fs";

const PORT = 3000;
const BASE_DIR = path.resolve("c:/SBA301/bài tập/sba-301");

const PROJECTS = [
  {
    id: "slot2",
    name: "Slot 02 - Student Learning Dashboard",
    category: "React Fundamentals",
    path: "Slot02/sba301-learning-dashboard",
    port: 5173,
    type: "react",
    desc: "ReactJS + Vite tĩnh, Component Architecture, JSX expressions, data modules (course, student, dashboardData)."
  },
  {
    id: "slot3",
    name: "Slot 03 - Orchid Explorer Dashboard",
    category: "React-Bootstrap",
    path: "Slot03/orchid-explorer",
    port: 5174,
    type: "react",
    desc: "React-Bootstrap, Hero Section, Quick Stats Cards, Orchid Gallery 6 loài lan kèm vector SVG, Care Tips."
  },
  {
    id: "slot4",
    name: "Slot 04 - Interactive Orchid Explorer",
    category: "Props & State",
    path: "Slot04/interactive-orchid-explorer",
    port: 5175,
    type: "react",
    desc: "Props, useState mở Modal & Favorite độc lập, UserContext toàn ứng dụng, Derived search & special filter."
  },
  {
    id: "slot5",
    name: "Slot 05 - EventHub Campus Explorer",
    category: "Frontend Integration",
    path: "Slot05/eventhub-campus-explorer",
    port: 5176,
    type: "react",
    desc: "Danh sách 8 sự kiện campus, lọc từ khóa, lọc category dropdown, switch featured, Modal chi tiết, Reset filters."
  },
  {
    id: "slot6",
    name: "Slot 06 - React Hook Product Manager",
    category: "React Hooks Comprehensive",
    path: "Slot06/react-hook-product-manager",
    port: 5177,
    type: "react",
    desc: "Full CRUD sản phẩm, Custom Hook useLocalStorage, ThemeContext Dark/Light, useRef focus, controlled validation."
  },
  {
    id: "slot7",
    name: "Slot 07 - Campus Event Navigator",
    category: "React Router",
    path: "Slot07/sba301-event-navigator",
    port: 5178,
    type: "react",
    desc: "React Router DOM v6, Dynamic Route /events/:id, useParams, useNavigate (-1), Wildcard Route 404."
  },
  {
    id: "slot9",
    name: "Slot 09 - MiniStore Router SPA",
    category: "Nested Routes",
    path: "Slot09/ministore-spa",
    port: 5179,
    type: "react",
    desc: "Nested Routes với Outlet (Dashboard/Profile/Orders), useSearchParams lưu bộ lọc trực tiếp lên URL query string."
  },
  {
    id: "slot10",
    name: "Slot 10 - Orchid Router SPA",
    category: "Router SPA Demo",
    path: "Slot10/orchid-router-demo",
    port: 5180,
    type: "react",
    desc: "SPA đa trang hoàn chỉnh: Home, Orchids list + query, Dynamic Detail, About, Contact form redirect, Nested Dashboard."
  },
  {
    id: "lab1",
    name: "Lab 01 - Integrated React Lab 01",
    category: "Official Lab 1",
    path: "lab1",
    port: 5181,
    type: "react",
    desc: "Bài Lab 01 chính thức: Orchid Collection với Reusable Card, Props, State, Modal và React-Bootstrap."
  },
  {
    id: "lab2",
    name: "Lab 02 - Orchid Gallery SPA",
    category: "Official Lab 2",
    path: "lab2/orchid-gallery-spa",
    port: 5182,
    type: "react",
    desc: "Bài Lab 02 chính thức: Client-Server communication, Axios fetch, Loading/Error/Empty states, Modal detail, reload data."
  },
  {
    id: "slot8",
    name: "Slot 08 - Product REST API Kit",
    category: "Mock REST API",
    path: "Slot08/slot8-product-rest-api-lab",
    port: 3001,
    type: "node",
    desc: "Mock REST API với json-server, API contract documentation, Postman collection, reset database script."
  }
];

let currentProcess = null;
let currentProjectId = null;
let currentProjectPort = null;

function stopCurrent() {
  if (currentProcess) {
    try {
      if (process.platform === "win32") {
        spawn("taskkill", ["/pid", currentProcess.pid, "/f", "/t"]);
      } else {
        currentProcess.kill("SIGTERM");
      }
    } catch (e) {
      console.error("Error stopping process:", e);
    }
    currentProcess = null;
    currentProjectId = null;
    currentProjectPort = null;
  }
}

function startProject(proj, callback) {
  stopCurrent();

  const projDir = path.join(BASE_DIR, proj.path);
  console.log(`Starting ${proj.name} on port ${proj.port} from ${projDir}...`);

  const cmd = process.platform === "win32" ? "npm.cmd" : "npm";
  const args = proj.type === "node" 
    ? ["run", "api"]
    : ["run", "dev", "--", "--port", String(proj.port), "--host"];

  const proc = spawn(cmd, args, {
    cwd: projDir,
    stdio: "pipe",
    shell: true
  });

  currentProcess = proc;
  currentProjectId = proj.id;
  currentProjectPort = proj.port;

  proc.stdout.on("data", (d) => process.stdout.write(`[${proj.id}] ${d}`));
  proc.stderr.on("data", (d) => process.stderr.write(`[${proj.id} ERR] ${d}`));

  proc.on("exit", (code) => {
    console.log(`[${proj.id}] exited with code ${code}`);
    if (currentProjectId === proj.id) {
      currentProcess = null;
      currentProjectId = null;
      currentProjectPort = null;
    }
  });

  // Wait 1.5 seconds for dev server to bind port
  setTimeout(() => {
    callback(null, { port: proj.port, id: proj.id });
  }, 1800);
}

const HTML = `<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SBA301 - Web Project Launcher & Preview Hub</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    @keyframes pulse-subtle {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.6; }
    }
    .pulse-dot { animation: pulse-subtle 2s infinite; }
  </style>
</head>
<body class="bg-slate-900 text-slate-100 min-h-screen flex flex-col font-sans">
  <!-- Header -->
  <header class="bg-slate-800/80 backdrop-blur border-b border-slate-700/80 px-6 py-4 sticky top-0 z-50 flex items-center justify-between shadow-md">
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-500 to-blue-600 flex items-center justify-center font-bold text-white shadow-lg shadow-cyan-500/20 text-lg">
        S
      </div>
      <div>
        <h1 class="text-lg font-bold tracking-tight text-white flex items-center gap-2">
          SBA301 Project Hub & Live Viewer
          <span class="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-blue-500/20 text-blue-400 border border-blue-500/30">SE1910</span>
        </h1>
        <p class="text-xs text-slate-400">Sinh viên: <span class="text-slate-200 font-medium">Trần Quốc Thái</span> • Trình xem trực tiếp các dự án</p>
      </div>
    </div>
    <div id="status-bar" class="flex items-center gap-3 bg-slate-950/60 border border-slate-700/60 rounded-lg px-4 py-2 text-xs">
      <span class="w-2.5 h-2.5 rounded-full bg-emerald-400 pulse-dot"></span>
      <span id="active-info" class="text-slate-300 font-mono">Đang chờ chọn dự án...</span>
      <a id="btn-open-tab" href="#" target="_blank" class="hidden ml-2 px-3 py-1 bg-cyan-600 hover:bg-cyan-500 text-white rounded font-medium transition shadow flex items-center gap-1.5">
        Mở tab riêng ↗
      </a>
    </div>
  </header>

  <!-- Main Container -->
  <div class="flex-1 flex flex-col lg:flex-row overflow-hidden">
    <!-- Sidebar: Projects list -->
    <aside class="w-full lg:w-[420px] bg-slate-900/90 border-r border-slate-800 p-4 overflow-y-auto flex flex-col gap-2.5 shrink-0 max-h-[45vh] lg:max-h-[calc(100vh-73px)]">
      <div class="flex items-center justify-between pb-2 border-b border-slate-800">
        <span class="text-xs uppercase font-bold tracking-wider text-slate-400">Danh Sách Dự Án (${PROJECTS.length})</span>
        <span class="text-[11px] text-slate-500">Bấm nút để chạy & xem</span>
      </div>

      <div class="flex flex-col gap-2 mt-1" id="project-list">
        <!-- Rendered via JS -->
      </div>
    </aside>

    <!-- Content / Preview Area -->
    <main class="flex-1 bg-slate-950 flex flex-col min-h-[550px] relative">
      <div id="preview-header" class="bg-slate-900/60 border-b border-slate-800 px-5 py-2.5 flex items-center justify-between text-xs text-slate-400">
        <div class="flex items-center gap-2">
          <span class="text-slate-500">Preview:</span>
          <span id="preview-title" class="font-semibold text-slate-200">Chưa tải dự án nào</span>
        </div>
        <div class="flex items-center gap-2" id="preview-tools" style="display:none;">
          <button onclick="reloadIframe()" class="hover:text-white px-2 py-1 bg-slate-800 hover:bg-slate-700 rounded transition text-[11px] flex items-center gap-1">
            ↻ Tải lại
          </button>
        </div>
      </div>

      <!-- Preview Iframe -->
      <div class="flex-1 relative flex items-center justify-center p-2 bg-slate-950">
        <div id="empty-state" class="text-center p-8 max-w-md">
          <div class="w-16 h-16 rounded-2xl bg-slate-800/80 border border-slate-700 mx-auto flex items-center justify-center text-3xl mb-4 shadow-inner">
            🚀
          </div>
          <h3 class="text-base font-semibold text-slate-200 mb-1">Chọn một dự án để xem trực tiếp</h3>
          <p class="text-xs text-slate-400 leading-relaxed">
            Nhấn nút <strong class="text-cyan-400">"Chạy dự án"</strong> ở danh sách bên trái. Hệ thống sẽ tự động khởi động server Vite/Node và hiển thị giao diện ngay tại khung này!
          </p>
        </div>

        <div id="loading-state" class="hidden text-center p-8">
          <div class="w-12 h-12 border-4 border-cyan-500/20 border-t-cyan-500 rounded-full animate-spin mx-auto mb-4"></div>
          <p class="text-sm font-medium text-slate-200">Đang khởi động dev server...</p>
          <p class="text-xs text-slate-400 mt-1" id="loading-detail"></p>
        </div>

        <iframe id="project-frame" class="hidden w-full h-full rounded-lg border border-slate-800 bg-white shadow-2xl"></iframe>
      </div>
    </main>
  </div>

  <script>
    const projects = ${JSON.stringify(PROJECTS)};
    let activeId = null;

    function renderList() {
      const container = document.getElementById("project-list");
      container.innerHTML = projects.map(p => {
        const isActive = p.id === activeId;
        return \`
          <div class="group p-3 rounded-xl border transition-all duration-200 \${
            isActive 
              ? 'bg-blue-950/40 border-cyan-500/60 shadow-lg shadow-cyan-950/40' 
              : 'bg-slate-800/40 hover:bg-slate-800 border-slate-800 hover:border-slate-700'
          }">
            <div class="flex items-start justify-between gap-2 mb-1.5">
              <div>
                <span class="text-[10px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded bg-slate-700/60 text-slate-300 mr-1.5">\${p.category}</span>
                <span class="text-[11px] font-mono text-cyan-400">:\${p.port}</span>
              </div>
              \${isActive ? '<span class="text-[11px] font-bold text-emerald-400 flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-emerald-400"></span> ĐANG CHẠY</span>' : ''}
            </div>
            <h4 class="text-sm font-semibold text-slate-100 group-hover:text-cyan-300 transition">\${p.name}</h4>
            <p class="text-[11px] text-slate-400 mt-1 line-clamp-2 leading-relaxed">\${p.desc}</p>
            <div class="mt-3 flex items-center justify-between pt-2 border-t border-slate-800/60">
              <span class="text-[11px] text-slate-500 font-mono">\${p.path}</span>
              <button onclick="launchProject('\${p.id}')" class="px-3 py-1.5 rounded-lg text-xs font-medium transition \${
                isActive 
                  ? 'bg-emerald-600 hover:bg-emerald-500 text-white shadow-md' 
                  : 'bg-blue-600 hover:bg-blue-500 text-white shadow shadow-blue-600/30'
              }">
                \${isActive ? '↻ Tải lại' : '▶ Chạy dự án'}
              </button>
            </div>
          </div>
        \`;
      }).join('');
    }

    async function launchProject(id) {
      const proj = projects.find(p => p.id === id);
      if (!proj) return;

      activeId = id;
      renderList();

      document.getElementById("empty-state").classList.add("hidden");
      document.getElementById("project-frame").classList.add("hidden");
      document.getElementById("loading-state").classList.remove("hidden");
      document.getElementById("loading-detail").textContent = "Khởi chạy " + proj.name + " trên port " + proj.port;

      document.getElementById("active-info").textContent = "Đang chạy: " + proj.name + " (: " + proj.port + ")";
      document.getElementById("btn-open-tab").href = "http://localhost:" + proj.port;
      document.getElementById("btn-open-tab").classList.remove("hidden");

      try {
        const res = await fetch("/api/start/" + id, { method: "POST" });
        const data = await res.json();
        
        const frame = document.getElementById("project-frame");
        frame.src = "http://localhost:" + data.port;
        
        frame.onload = () => {
          document.getElementById("loading-state").classList.add("hidden");
          frame.classList.remove("hidden");
          document.getElementById("preview-title").textContent = proj.name + " (" + "http://localhost:" + data.port + ")";
          document.getElementById("preview-tools").style.display = "flex";
        };
      } catch (err) {
        alert("Lỗi khi khởi chạy: " + err.message);
        document.getElementById("loading-state").classList.add("hidden");
        document.getElementById("empty-state").classList.remove("hidden");
      }
    }

    function reloadIframe() {
      const frame = document.getElementById("project-frame");
      if (frame && frame.src) {
        frame.src = frame.src;
      }
    }

    renderList();
    // Auto launch first project (Slot 2)
    launchProject('slot2');
  </script>
</body>
</html>
`;

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://${req.headers.host}`);

  if (url.pathname === "/") {
    res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
    res.end(HTML);
    return;
  }

  if (url.pathname === "/api/projects" && req.method === "GET") {
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify({
      projects: PROJECTS,
      activeId: currentProjectId,
      activePort: currentProjectPort
    }));
    return;
  }

  if (url.pathname.startsWith("/api/start/") && req.method === "POST") {
    const id = url.pathname.replace("/api/start/", "");
    const proj = PROJECTS.find((p) => p.id === id);
    if (!proj) {
      res.writeHead(404, { "Content-Type": "application/json" });
      res.end(JSON.stringify({ error: "Project not found" }));
      return;
    }

    startProject(proj, (err, info) => {
      if (err) {
        res.writeHead(500, { "Content-Type": "application/json" });
        res.end(JSON.stringify({ error: String(err) }));
      } else {
        res.writeHead(200, { "Content-Type": "application/json" });
        res.end(JSON.stringify(info));
      }
    });
    return;
  }

  if (url.pathname === "/api/stop" && req.method === "POST") {
    stopCurrent();
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ success: true }));
    return;
  }

  res.writeHead(404);
  res.end("Not Found");
});

server.listen(PORT, () => {
  console.log(`SBA301 Project Hub running at http://localhost:${PORT}`);
});
