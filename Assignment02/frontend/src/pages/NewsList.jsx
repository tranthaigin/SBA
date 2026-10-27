import { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button, Form, Badge, Modal, Alert, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function NewsList() {
  const { user } = useAuth();
  const [news, setNews] = useState([]);
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Modal create/edit state
  const [showModal, setShowModal] = useState(false);
  const [editingArticle, setEditingArticle] = useState(null);
  const [formData, setFormData] = useState({
    newsTitle: "",
    headline: "",
    newsContent: "",
    newsSource: "",
    categoryId: "",
    newsStatus: true
  });

  // Detail modal state
  const [viewArticle, setViewArticle] = useState(null);

  const isStaff = user?.accountRole === 2;

  const fetchNews = async () => {
    setLoading(true);
    setError("");
    try {
      const url = isStaff
        ? (search ? `/api/news?search=${encodeURIComponent(search)}` : "/api/news")
        : (search ? `/api/news?search=${encodeURIComponent(search)}` : "/api/news/active");
      const res = await axios.get(url);
      setNews(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load news");
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await axios.get("/api/categories/active");
      setCategories(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchNews();
    fetchCategories();
  }, [user, search]);

  const handleOpenCreate = () => {
    setEditingArticle(null);
    setFormData({
      newsTitle: "",
      headline: "",
      newsContent: "",
      newsSource: "FPT University News",
      categoryId: categories[0]?.categoryId || "",
      newsStatus: true
    });
    setShowModal(true);
  };

  const handleOpenEdit = (art) => {
    setEditingArticle(art);
    setFormData({
      newsTitle: art.newsTitle || "",
      headline: art.headline || "",
      newsContent: art.newsContent || "",
      newsSource: art.newsSource || "",
      categoryId: art.category?.categoryId || "",
      newsStatus: art.newsStatus
    });
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingArticle) {
        await axios.put(`/api/news/${editingArticle.newsArticleId}?updatedById=${user.accountId}`, {
          ...formData,
          categoryId: Number(formData.categoryId)
        });
      } else {
        await axios.post("/api/news", {
          ...formData,
          categoryId: Number(formData.categoryId),
          createdById: user.accountId
        });
      }
      setShowModal(false);
      fetchNews();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to save news article");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this news article?")) return;
    try {
      await axios.delete(`/api/news/${id}`);
      fetchNews();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to delete news article");
    }
  };

  return (
    <Container className="py-2">
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div>
          <h2 className="mb-0">News Articles</h2>
          <small className="text-muted">
            {isStaff ? "Staff Workspace: Manage and publish articles" : "Public News Portal"}
          </small>
        </div>
        <div className="d-flex gap-2">
          <Form.Control
            type="text"
            placeholder="Search news by title / headline..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ width: "280px" }}
          />
          {isStaff && (
            <Button variant="success" onClick={handleOpenCreate}>
              + Write New Article
            </Button>
          )}
        </div>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="primary" />
        </div>
      ) : news.length === 0 ? (
        <div className="text-center py-5 text-muted">No news articles found.</div>
      ) : (
        <Row className="g-4">
          {news.map((item) => (
            <Col md={6} lg={4} key={item.newsArticleId}>
              <Card className="h-100 shadow-sm border-0 d-flex flex-column">
                <Card.Body className="d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <Badge bg="secondary">{item.category?.categoryName || "General"}</Badge>
                    {isStaff && (
                      <Badge bg={item.newsStatus ? "success" : "secondary"}>
                        {item.newsStatus ? "Active" : "Inactive"}
                      </Badge>
                    )}
                  </div>
                  <Card.Title className="fs-5">{item.newsTitle || item.headline}</Card.Title>
                  <Card.Subtitle className="mb-2 text-muted small">
                    {item.headline}
                  </Card.Subtitle>
                  <Card.Text className="text-muted small flex-grow-1">
                    {item.newsContent ? item.newsContent.slice(0, 120) + "..." : ""}
                  </Card.Text>
                  <div className="d-flex justify-content-between align-items-center pt-2 border-top">
                    <small className="text-muted">{item.newsSource || "FUNews"}</small>
                    <div className="d-flex gap-1">
                      <Button variant="outline-primary" size="sm" onClick={() => setViewArticle(item)}>
                        Read
                      </Button>
                      {isStaff && (
                        <>
                          <Button variant="outline-secondary" size="sm" onClick={() => handleOpenEdit(item)}>
                            Edit
                          </Button>
                          <Button variant="outline-danger" size="sm" onClick={() => handleDelete(item.newsArticleId)}>
                            Delete
                          </Button>
                        </>
                      )}
                    </div>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      {/* Read Article Modal */}
      {viewArticle && (
        <Modal show={true} onHide={() => setViewArticle(null)} size="lg" centered>
          <Modal.Header closeButton>
            <Modal.Title>{viewArticle.newsTitle || viewArticle.headline}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <h6 className="text-muted mb-3">{viewArticle.headline}</h6>
            <div className="mb-3">
              <Badge bg="info" className="me-2">{viewArticle.category?.categoryName}</Badge>
              <span className="small text-muted">Source: {viewArticle.newsSource}</span>
            </div>
            <p style={{ whiteSpace: "pre-line" }}>{viewArticle.newsContent}</p>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setViewArticle(null)}>
              Close
            </Button>
          </Modal.Footer>
        </Modal>
      )}

      {/* Create / Edit Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title>{editingArticle ? "Edit News Article" : "Create News Article"}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSave}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Headline *</Form.Label>
              <Form.Control
                required
                value={formData.headline}
                onChange={(e) => setFormData({ ...formData, headline: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>News Title</Form.Label>
              <Form.Control
                value={formData.newsTitle}
                onChange={(e) => setFormData({ ...formData, newsTitle: e.target.value })}
              />
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Category *</Form.Label>
                  <Form.Select
                    required
                    value={formData.categoryId}
                    onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                  >
                    <option value="">Select Category</option>
                    {categories.map((c) => (
                      <option key={c.categoryId} value={c.categoryId}>
                        {c.categoryName}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>News Source</Form.Label>
                  <Form.Control
                    value={formData.newsSource}
                    onChange={(e) => setFormData({ ...formData, newsSource: e.target.value })}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>News Content</Form.Label>
              <Form.Control
                as="textarea"
                rows={5}
                value={formData.newsContent}
                onChange={(e) => setFormData({ ...formData, newsContent: e.target.value })}
              />
            </Form.Group>
            <Form.Check
              type="checkbox"
              id="newsStatusCheck"
              label="Active Status"
              checked={formData.newsStatus}
              onChange={(e) => setFormData({ ...formData, newsStatus: e.target.checked })}
            />
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              Save Article
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}
