import { useEffect, useRef, useState } from "react";
import Alert from "react-bootstrap/Alert";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Header from "./components/Header";
import ProductForm from "./components/ProductForm";
import ProductList from "./components/ProductList";
import SearchBox from "./components/SearchBox";
import CategoryFilter from "./components/CategoryFilter";
import ProductStats from "./components/ProductStats";
import { useTheme } from "./context/ThemeContext";
import { initialProducts } from "./data/initialProducts";
import useLocalStorage from "./hooks/useLocalStorage";

export default function App() {
  const [products, setProducts] = useLocalStorage("sba301-products", initialProducts);
  const [editingProduct, setEditingProduct] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [category, setCategory] = useState("All");
  const [message, setMessage] = useState("");
  const searchInputRef = useRef(null);
  const { theme } = useTheme();

  // Document title sync effect
  useEffect(() => {
    document.title = `Products (${products.length}) | SBA301`;
  }, [products.length]);

  // Autofocus search input upon mount
  useEffect(() => {
    searchInputRef.current?.focus();
  }, []);

  // Feedback message timer effect with cleanup
  useEffect(() => {
    if (!message) return undefined;
    const timerId = setTimeout(() => setMessage(""), 2500);
    return () => clearTimeout(timerId);
  }, [message]);

  const saveProduct = (product) => {
    if (product.id) {
      setProducts((current) =>
        current.map((item) => (item.id === product.id ? product : item))
      );
      setMessage(`Product "${product.name}" updated successfully.`);
      setEditingProduct(null);
      return;
    }
    const newProduct = { ...product, id: Date.now() };
    setProducts((current) => [...current, newProduct]);
    setMessage(`Product "${newProduct.name}" added successfully.`);
  };

  const deleteProduct = (id) => {
    const product = products.find((item) => item.id === id);
    if (!product) return;
    if (!window.confirm(`Are you sure you want to delete "${product.name}"?`)) return;
    setProducts((current) => current.filter((item) => item.id !== id));
    if (editingProduct?.id === id) setEditingProduct(null);
    setMessage(`Product "${product.name}" deleted successfully.`);
  };

  const categories = ["All", ...new Set(products.map((item) => item.category))];
  const normalizedSearch = searchTerm.trim().toLowerCase();
  const filteredProducts = products.filter((product) => {
    const matchesName = product.name.toLowerCase().includes(normalizedSearch);
    const matchesCategory = category === "All" || product.category === category;
    return matchesName && matchesCategory;
  });

  return (
    <div className="min-vh-100 app-shell" data-bs-theme={theme}>
      <Header />
      <Container className="py-4">
        <div className="mb-4">
          <h1 className="h3 mb-1">Product Management Dashboard</h1>
          <p className="text-secondary mb-0">
            Frontend CRUD + React Hooks + Custom localStorage Persistence
          </p>
        </div>

        {message && (
          <Alert variant="success" dismissible onClose={() => setMessage("")}>
            {message}
          </Alert>
        )}

        <ProductStats products={products} />

        <ProductForm
          editingProduct={editingProduct}
          onSave={saveProduct}
          onCancel={() => setEditingProduct(null)}
        />

        <Row className="g-3 mb-4">
          <Col md={8}>
            <SearchBox
              value={searchTerm}
              onChange={setSearchTerm}
              inputRef={searchInputRef}
            />
          </Col>
          <Col md={4}>
            <CategoryFilter
              value={category}
              categories={categories}
              onChange={setCategory}
            />
          </Col>
        </Row>

        <ProductList
          products={filteredProducts}
          onEdit={setEditingProduct}
          onDelete={deleteProduct}
        />
      </Container>
    </div>
  );
}
