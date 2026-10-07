import { useEffect, useState } from "react";
import Button from "react-bootstrap/Button";
import Card from "react-bootstrap/Card";
import Col from "react-bootstrap/Col";
import Form from "react-bootstrap/Form";
import Row from "react-bootstrap/Row";

const EMPTY_FORM = {
  name: "",
  category: "",
  price: "",
  quantity: ""
};

export default function ProductForm({ editingProduct, onSave, onCancel }) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editingProduct) {
      setForm({
        name: editingProduct.name,
        category: editingProduct.category,
        price: String(editingProduct.price),
        quantity: String(editingProduct.quantity)
      });
    } else {
      setForm(EMPTY_FORM);
    }
    setErrors({});
  }, [editingProduct]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const validate = () => {
    const nextErrors = {};
    if (!form.name.trim()) nextErrors.name = "Product name is required.";
    if (!form.category) nextErrors.category = "Category is required.";
    if (Number(form.price) <= 0) nextErrors.price = "Price must be greater than 0.";
    if (Number(form.quantity) < 0) nextErrors.quantity = "Quantity must be 0 or greater.";
    return nextErrors;
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const nextErrors = validate();
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0) return;

    onSave({
      id: editingProduct?.id,
      name: form.name.trim(),
      category: form.category,
      price: Number(form.price),
      quantity: Number(form.quantity)
    });

    if (!editingProduct) setForm(EMPTY_FORM);
  };

  return (
    <Card className="shadow-sm mb-4">
      <Card.Body>
        <Card.Title className="fw-bold mb-3">{editingProduct ? "Edit Product" : "Add New Product"}</Card.Title>
        <Form onSubmit={handleSubmit} noValidate>
          <Row className="g-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>Product Name</Form.Label>
                <Form.Control
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  isInvalid={Boolean(errors.name)}
                  placeholder="e.g. Wireless Mouse"
                />
                <Form.Control.Feedback type="invalid">{errors.name}</Form.Control.Feedback>
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Category</Form.Label>
                <Form.Select
                  name="category"
                  value={form.category}
                  onChange={handleChange}
                  isInvalid={Boolean(errors.category)}
                >
                  <option value="">Choose category...</option>
                  <option>Accessories</option>
                  <option>Display</option>
                  <option>Connectivity</option>
                  <option>Workspace</option>
                  <option>Other</option>
                </Form.Select>
                <Form.Control.Feedback type="invalid">{errors.category}</Form.Control.Feedback>
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Price (VND)</Form.Label>
                <Form.Control
                  type="number"
                  min="1"
                  name="price"
                  value={form.price}
                  onChange={handleChange}
                  isInvalid={Boolean(errors.price)}
                  placeholder="e.g. 500000"
                />
                <Form.Control.Feedback type="invalid">{errors.price}</Form.Control.Feedback>
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Quantity</Form.Label>
                <Form.Control
                  type="number"
                  min="0"
                  name="quantity"
                  value={form.quantity}
                  onChange={handleChange}
                  isInvalid={Boolean(errors.quantity)}
                  placeholder="e.g. 10"
                />
                <Form.Control.Feedback type="invalid">{errors.quantity}</Form.Control.Feedback>
              </Form.Group>
            </Col>
          </Row>

          <div className="d-flex gap-2 mt-3">
            <Button type="submit" variant="primary">
              {editingProduct ? "Update Product" : "Add Product"}
            </Button>
            {editingProduct && (
              <Button type="button" variant="secondary" onClick={onCancel}>
                Cancel
              </Button>
            )}
          </div>
        </Form>
      </Card.Body>
    </Card>
  );
}
