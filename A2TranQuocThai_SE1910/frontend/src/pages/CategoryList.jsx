import { useEffect, useState } from "react";
import { Alert, Button, Container, Form, Table } from "react-bootstrap";
import { getCategories, deleteCategory } from "../api/categoryApi";
import CategoryForm from "../components/CategoryForm";
import { useAuth } from "../context/AuthContext";
export default function CategoryList() {
  const { user } = useAuth();
  const [items, setItems] = useState([]),
    [search, setSearch] = useState(""),
    [error, setError] = useState(""),
    [show, setShow] = useState(false),
    [editing, setEditing] = useState(null);
  async function load() {
    try {
      const r = await getCategories({ keyword: search });
      setItems(r.data);
      setError("");
    } catch (e) {
      setError(e.response?.data?.message || "Cannot load categories");
    }
  }
  useEffect(() => {
    if (user?.accountRole === 2) load();
  }, [search, user]);
  if (user?.accountRole !== 2)
    return (
      <Container>
        <Alert variant="danger">Staff access required.</Alert>
      </Container>
    );
  async function remove(c) {
    if (!window.confirm(`Delete ${c.categoryName}?`)) return;
    try {
      await deleteCategory(c.categoryId);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || "Cannot delete category");
    }
  }
  return (
    <Container>
      <h2>Category Management</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      <Form.Control
        aria-label="Search categories"
        placeholder="Search categories"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <Button
        className="my-3"
        onClick={() => {
          setEditing(null);
          setShow(true);
        }}
      >
        Create Category
      </Button>
      <Table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {items.map((c) => (
            <tr key={c.categoryId}>
              <td>{c.categoryName}</td>
              <td>{c.categoryDesciption}</td>
              <td>{c.isActive ? "Active" : "Inactive"}</td>
              <td>
                <Button
                  onClick={() => {
                    setEditing(c);
                    setShow(true);
                  }}
                >
                  Edit
                </Button>{" "}
                <Button variant="danger" onClick={() => remove(c)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      {items.length === 0 && <p>No categories found.</p>}
      <CategoryForm
        show={show}
        category={editing}
        onHide={() => setShow(false)}
        onCreated={load}
      />
    </Container>
  );
}
