import { Link, useSearchParams } from "react-router-dom";
import { products } from "../data/products";

export default function ProductsPage() {
  const [params, setParams] = useSearchParams();
  const q = (params.get("q") ?? "").toLowerCase();
  const category = params.get("category") ?? "all";

  const filtered = products.filter(
    (p) =>
      p.name.toLowerCase().includes(q) &&
      (category === "all" || p.category === category)
  );

  const update = (key, value) => {
    const next = new URLSearchParams(params);
    if (value && value !== "all") {
      next.set(key, value);
    } else {
      next.delete(key);
    }
    setParams(next);
  };

  return (
    <section>
      <h1>Products Catalog</h1>
      <p className="text-muted">
        Filters are stored in URL query parameters (shareable & bookmarkable).
      </p>

      <div style={{ display: "flex", gap: "10px", flexWrap: "wrap", marginBottom: "20px" }}>
        <input
          value={params.get("q") ?? ""}
          onChange={(e) => update("q", e.target.value)}
          placeholder="Search product..."
          style={{ minWidth: "240px" }}
        />
        <select
          value={category}
          onChange={(e) => update("category", e.target.value)}
        >
          <option value="all">All Categories</option>
          <option value="phone">Phones</option>
          <option value="laptop">Laptops</option>
          <option value="tablet">Tablets</option>
        </select>
        {(q || category !== "all") && (
          <button onClick={() => setParams({})}>Reset Filters</button>
        )}
      </div>

      <div className="product-list">
        {filtered.map((p) => (
          <div key={p.id} className="product-item">
            <h3 style={{ margin: "0 0 8px 0" }}>{p.name}</h3>
            <p style={{ margin: "0 0 8px 0", color: "#64748b" }}>
              Category: {p.category} • Price: {p.price.toLocaleString()} VND
            </p>
            <Link to={`/products/${p.id}`}>
              <button>View detail</button>
            </Link>
          </div>
        ))}
      </div>

      {filtered.length === 0 && (
        <p style={{ color: "#ef4444", fontWeight: "bold" }}>No matching products found.</p>
      )}
    </section>
  );
}
