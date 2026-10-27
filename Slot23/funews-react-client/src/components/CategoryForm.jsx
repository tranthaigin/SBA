import { useEffect, useState } from "react";
import { Alert, Button, Form, Modal } from "react-bootstrap";
import { createCategory } from "../api/categoryApi";
import { getApiErrorMessage } from "../api/apiError";
import {
  initialCategoryForm,
  toCategoryPayload,
  validateCategoryForm,
} from "../utils/categoryForm";

export default function CategoryForm({ show, onHide, onCreated }) {
  const [form, setForm] = useState(initialCategoryForm);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (show) {
      setForm(initialCategoryForm);
      setError("");
    }
  }, [show]);

  function handleChange(event) {
    const { name, value, type, checked } = event.target;
    setForm((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    const validationMessage = validateCategoryForm(form);
    if (validationMessage) {
      setError(validationMessage);
      return;
    }

    try {
      setSubmitting(true);
      setError("");
      await createCategory(toCategoryPayload(form));
      onHide();
      await onCreated();
    } catch (requestError) {
      if (requestError.response?.status === 400) {
        setError(getApiErrorMessage(requestError, "Invalid category data."));
      } else if (requestError.response?.status === 409) {
        setError(getApiErrorMessage(requestError, "Category name already exists."));
      } else {
        setError(getApiErrorMessage(requestError, "Create category failed."));
      }
    } finally {
      setSubmitting(false);
    }
  }

  function handleClose() {
    if (submitting) {
      return;
    }
    setError("");
    onHide();
  }

  return (
    <Modal show={show} onHide={handleClose} centered>
      <Form onSubmit={handleSubmit}>
        <Modal.Header closeButton>
          <Modal.Title>Create Category</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          <Form.Group className="mb-3">
            <Form.Label>Category Name</Form.Label>
            <Form.Control
              name="categoryName"
              value={form.categoryName}
              onChange={handleChange}
              maxLength={100}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Description</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              name="categoryDescription"
              value={form.categoryDescription}
              onChange={handleChange}
              maxLength={250}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Parent Category ID</Form.Label>
            <Form.Control
              type="number"
              min="1"
              max="32767"
              step="1"
              name="parentCategoryId"
              value={form.parentCategoryId}
              onChange={handleChange}
              placeholder="Optional"
            />
            <Form.Text muted>Leave empty if this is a root category.</Form.Text>
          </Form.Group>
          <Form.Check
            type="switch"
            name="active"
            label="Active"
            checked={form.active}
            onChange={handleChange}
          />
        </Modal.Body>
        <Modal.Footer>
          <Button
            type="button"
            variant="secondary"
            disabled={submitting}
            onClick={handleClose}
          >
            Cancel
          </Button>
          <Button type="submit" disabled={submitting}>
            {submitting ? "Saving..." : "Create"}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
