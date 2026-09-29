const fs = require("fs");
const path = require("path");

const root = path.resolve(__dirname, "..");
const seed = path.join(root, "db.seed.json");
const db = path.join(root, "db.json");

fs.copyFileSync(seed, db);
console.log("db.json has been reset successfully from db.seed.json");
