import { useCallback, useEffect, useState } from "react";
import {
  Alert,
  Badge,
  Button,
  Col,
  Container,
  Form,
  Row,
  Spinner,
  Table,
} from "react-bootstrap";
import { Link } from "react-router-dom";
import { deleteCategory, getCategories } from "../api/categoryApi";
import { getApiErrorMessage } from "../api/apiError";
import CategoryForm from "../components/CategoryForm";
import PaginationBar from "../components/PaginationBar";

const PAGE_SIZE = 10;

export default function CategoryList() {
  const [categories, setCategories] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [keywordInput, setKeywordInput] = useState("");
  const [keyword, setKeyword] = useState("");
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showCreate, setShowCreate] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const loadCategories = useCallback(async (signal) => {
    try {
      setLoading(true);
      setError("");
      const response = await getCategories({
        keyword,
        page,
        size: PAGE_SIZE,
        sort: "categoryName,asc",
        signal,
      });
      const pageData = response.data;
      if (!Array.isArray(pageData.content)) {
        throw new Error("Unexpected API response: expected a Page with a content array.");
      }
      setCategories(pageData.content);
      setTotalPages(pageData.totalPages ?? 0);
      setTotalElements(pageData.totalElements ?? 0);
    } catch (requestError) {
      if (requestError.code !== "ERR_CANCELED") {
        setError(getApiErrorMessage(requestError, "Cannot load categories."));
        setCategories([]);
      }
    } finally {
      if (!signal?.aborted) {
        setLoading(false);
      }
    }
  }, [keyword, page]);

  useEffect(() => {
    const controller = new AbortController();
    loadCategories(controller.signal);
    return () => controller.abort();
  }, [loadCategories, refreshKey]);

  function handleSearch(event) {
    event.preventDefault();
    setPage(0);
    setKeyword(keywordInput.trim());
  }

  function handleClearSearch() {
    setKeywordInput("");
    setKeyword("");
    setPage(0);
  }

  async function handleDelete(category) {
    if (deletingId !== null) {
      return;
    }

    const confirmed = window.confirm(`Delete category "${category.categoryName}"?`);
    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(category.categoryId);
      setError("");
      await deleteCategory(category.categoryId);
      if (categories.length === 1 && page > 0) {
        setPage((currentPage) => currentPage - 1);
      } else {
        setRefreshKey((currentKey) => currentKey + 1);
      }
    } catch (requestError) {
      if (requestError.response?.status === 409) {
        setError(
          getApiErrorMessage(
            requestError,
            "Cannot delete this category because it is being used by one or more news articles.",
          ),
        );
      } else if (requestError.response?.status === 404) {
        setError("Category no longer exists. Refreshing the list.");
        setRefreshKey((currentKey) => currentKey + 1);
      } else {
        setError(getApiErrorMessage(requestError, "Delete category failed."));
      }
    } finally {
      setDeletingId(null);
    }
  }

  function handleCreated() {
    setPage(0);
    setRefreshKey((currentKey) => currentKey + 1);
  }

  return (
    <Container>
      <div className="d-flex justify-content-between align-items-center mb-3 gap-3">
        <div>
          <h2 className="mb-1">Category Management</h2>
          <div className="text-muted">FUNewsManagementSystem</div>
        </div>
        <Button onClick={() => setShowCreate(true)}>Create Category</Button>
      </div>

      <Form onSubmit={handleSearch} className="mb-3">
        <Row className="g-2">
          <Col md={6}>
            <Form.Control
              value={keywordInput}
              onChange={(event) => setKeywordInput(event.target.value)}
              placeholder="Search category name..."
              aria-label="Search category name"
            />
          </Col>
          <Col xs="auto">
            <Button type="submit">Search</Button>
          </Col>
          <Col xs="auto">
            <Button type="button" variant="secondary" onClick={handleClearSearch}>
              Clear
            </Button>
          </Col>
        </Row>
      </Form>

      {error && <Alert variant="danger" role="alert">{error}</Alert>}

      {loading ? (
        <div className="py-5 text-center" role="status">
          <Spinner animation="border" />
          <div className="mt-2">Loading categories...</div>
        </div>
      ) : (
        <>
          <div className="mb-2 text-muted">Total records: {totalElements}</div>
          <Table striped bordered hover responsive className="align-middle">
            <thead>
              <tr>
                <th>ID</th>
                <th>Category Name</th>
                <th>Description</th>
                <th>Parent ID</th>
                <th>Status</th>
                <th style={{ width: 170 }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {categories.length === 0 ? (
                <tr>
                  <td colSpan={6} className="text-center text-muted py-4">
                    No categories found.
                  </td>
                </tr>
              ) : (
                categories.map((category) => (
                  <tr key={category.categoryId}>
                    <td>{category.categoryId}</td>
                    <td>{category.categoryName}</td>
                    <td>{category.categoryDescription}</td>
                    <td>{category.parentCategoryId ?? "-"}</td>
                    <td>
                      {category.active ? (
                        <Badge bg="success">Active</Badge>
                      ) : (
                        <Badge bg="secondary">Inactive</Badge>
                      )}
                    </td>
                    <td className="text-nowrap">
                      <Button
                        as={Link}
                        to={`/categories/${category.categoryId}/edit`}
                        size="sm"
                        className="me-2"
                        disabled={deletingId !== null}
                      >
                        Edit
                      </Button>
                      <Button
                        variant="danger"
                        size="sm"
                        disabled={deletingId !== null}
                        onClick={() => handleDelete(category)}
                      >
                        {deletingId === category.categoryId ? "Deleting..." : "Delete"}
                      </Button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
          <PaginationBar page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}

      <CategoryForm
        show={showCreate}
        onHide={() => setShowCreate(false)}
        onCreated={handleCreated}
      />
    </Container>
  );
}
