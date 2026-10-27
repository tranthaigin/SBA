import { useEffect, useState } from "react";
import { Alert, Button, Container, Form, Spinner } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import { getCategoryById, updateCategory } from "../api/categoryApi";
import { getApiErrorMessage } from "../api/apiError";
import {
  toCategoryPayload,
  validateCategoryForm,
} from "../utils/categoryForm";

export default function EditCategory() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const controller = new AbortController();

    async function loadCategory() {
      setLoading(true);
      setError("");
      setForm(null);
      try {
        const response = await getCategoryById(id, { signal: controller.signal });
        const category = response.data;
        setForm({
          categoryName: category.categoryName ?? "",
          categoryDescription: category.categoryDescription ?? "",
          parentCategoryId: category.parentCategoryId ?? "",
          active: category.active ?? true,
        });
      } catch (requestError) {
        if (requestError.code !== "ERR_CANCELED") {
          if (requestError.response?.status === 404) {
            setError(`Category ${id} not found.`);
          } else {
            setError(getApiErrorMessage(requestError, "Cannot load category."));
          }
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    loadCategory();
    return () => controller.abort();
  }, [id]);

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
      await updateCategory(id, toCategoryPayload(form));
      navigate("/categories");
    } catch (requestError) {
      if (requestError.response?.status === 400) {
        setError(getApiErrorMessage(requestError, "Invalid category data."));
      } else if (requestError.response?.status === 404) {
        setError(`Category ${id} not found.`);
      } else if (requestError.response?.status === 409) {
        setError(
          getApiErrorMessage(
            requestError,
            "Category update conflicts with existing data.",
          ),
        );
      } else {
        setError(getApiErrorMessage(requestError, "Update category failed."));
      }
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) {
    return (
      <Container className="py-5 text-center" role="status">
        <Spinner animation="border" />
        <div className="mt-2">Loading category...</div>
      </Container>
    );
  }

  return (
    <Container className="edit-category" style={{ maxWidth: 720 }}>
      <h2>Edit Category #{id}</h2>
      <div className="text-muted mb-3">FUNewsManagementSystem</div>
      {error && <Alert variant="danger" role="alert">{error}</Alert>}
      {form ? (
        <Form onSubmit={handleSubmit}>
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
              rows={4}
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
            />
          </Form.Group>
          <Form.Check
            type="switch"
            name="active"
            label="Active"
            checked={form.active}
            onChange={handleChange}
            className="mb-4"
          />
          <Button type="submit" disabled={submitting} className="me-2">
            {submitting ? "Saving..." : "Save Changes"}
          </Button>
          <Button
            type="button"
            variant="secondary"
            disabled={submitting}
            onClick={() => navigate("/categories")}
          >
            Cancel
          </Button>
        </Form>
      ) : (
        <Button variant="secondary" onClick={() => navigate("/categories")}>
          Back to categories
        </Button>
      )}
    </Container>
  );
}
