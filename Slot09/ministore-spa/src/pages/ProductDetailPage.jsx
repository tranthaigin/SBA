import { Link, useNavigate, useParams } from "react-router-dom";
import { products } from "../data/products";

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const product = products.find((p) => p.id === Number(id));

  if (!product) {
    return (
      <section>
        <h1 style={{ color: "#ef4444" }}>Product Not Found</h1>
        <p>There is no product with identifier {id}.</p>
        <Link to="/products">
          <button>Back to Products</button>
        </Link>
      </section>
    );
  }

  return (
    <section>
      <button onClick={() => navigate(-1)} style={{ marginBottom: "16px" }}>
        ← Back
      </button>
      <h1>{product.name}</h1>
      <p><strong>Category:</strong> {product.category}</p>
      <p><strong>Price:</strong> {product.price.toLocaleString()} VND</p>
      <p><strong>Product ID:</strong> {product.id}</p>
      <Link to="/products">
        <button style={{ background: "#475569" }}>All Products</button>
      </Link>
    </section>
  );
}
