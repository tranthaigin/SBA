import { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button, Form, Badge, Modal, Alert, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

/**
 * Component trang danh sách Tin tức (NewsList).
 * Cho phép khách vãng lai đọc và tìm kiếm tin tức kích hoạt (Active);
 * Cho phép nhân viên (Staff) quản lý thêm, sửa, xóa bài viết kèm chọn nhiều Tags.
 */
export default function NewsList() {
  const { user } = useAuth();
  const [news, setNews] = useState([]);
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Trạng thái modal tạo mới và chỉnh sửa bài viết
  const [showModal, setShowModal] = useState(false);
  const [editingArticle, setEditingArticle] = useState(null);
  const [formData, setFormData] = useState({
    newsTitle: "",
    headline: "",
    newsContent: "",
    newsSource: "",
    categoryId: "",
    tagIds: [],
    newsStatus: true
  });

  // Trạng thái modal xem chi tiết bài viết
  const [viewArticle, setViewArticle] = useState(null);

  const isStaff = user?.accountRole === 2;

  /**
   * Gọi API tải danh sách bài viết từ server; nếu là Staff sẽ lấy toàn bộ, nếu là độc giả chỉ lấy tin Active.
   */
  const fetchNews = async () => {
    setLoading(true);
    setError("");
    try {
      const url = isStaff
        ? (search ? `/api/news?search=${encodeURIComponent(search)}` : "/api/news")
        : "/api/news/active";
      const res = await axios.get(url);
      setNews(isStaff ? res.data : res.data.filter(a => `${a.newsTitle || ""} ${a.headline}`.toLowerCase().includes(search.toLowerCase())));
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load news");
    } finally {
      setLoading(false);
    }
  };

  /**
   * Tải danh sách danh mục hoạt động và danh sách thẻ tags từ backend để nạp vào form.
   */
  const fetchCategories = async () => {
    try {
      const res = await axios.get("/api/categories/active");
      setCategories(res.data);
      setTags((await axios.get("/api/tags")).data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchNews();
    fetchCategories();
  }, [user, search]);

  /**
   * Mở modal thêm bài viết mới với form sạch.
   */
  const handleOpenCreate = () => {
    setEditingArticle(null);
    setFormData({
      newsTitle: "",
      headline: "",
      newsContent: "",
      newsSource: "",
      categoryId: categories[0]?.categoryId || "",
      tagIds: [],
      newsStatus: true
    });
    setShowModal(true);
  };

  /**
   * Mở modal chỉnh sửa với dữ liệu của bài viết được chọn.
   */
  const handleOpenEdit = (article) => {
    setEditingArticle(article);
    setFormData({
      newsTitle: article.newsTitle || "",
      headline: article.headline || "",
      newsContent: article.newsContent || "",
      newsSource: article.newsSource || "",
      categoryId: article.category?.categoryId || "",
      tagIds: article.tags ? article.tags.map(t => t.tagId) : [],
      newsStatus: article.newsStatus
    });
    setShowModal(true);
  };

  /**
   * Xử lý lưu bài viết (thêm mới hoặc cập nhật) gửi request REST lên backend.
   */
  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingArticle) {
        await axios.put(`/api/news/${editingArticle.newsArticleId}`, formData);
      } else {
        await axios.post("/api/news", formData);
      }
      setShowModal(false);
      fetchNews();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to save news article");
    }
  };

  /**
   * Xử lý xóa bài viết sau khi người dùng xác nhận.
   */
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this news article?")) return;
    try {
      await axios.delete(`/api/news/${id}`);
      fetchNews();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to delete article");
    }
  };

  /**
   * Xử lý chọn hoặc bỏ chọn thẻ Tag trên form bài viết.
   */
  const handleTagToggle = (tagId) => {
    setFormData(prev => {
      const exists = prev.tagIds.includes(tagId);
      return {
        ...prev,
        tagIds: exists ? prev.tagIds.filter(id => id !== tagId) : [...prev.tagIds, tagId]
      };
    });
  };

  return (
    <Container className="py-2">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2>News Articles</h2>
          <small className="text-muted">
            {isStaff ? "Manage company news & editorial content" : "Read active public news"}
          </small>
        </div>
        {isStaff && (
          <Button variant="primary" onClick={handleOpenCreate}>
            + Create News Article
          </Button>
        )}
      </div>

      {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

      <Form.Group className="mb-4">
        <Form.Control
          type="text"
          placeholder="Search news by title, headline or content..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </Form.Group>

      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="primary" />
        </div>
      ) : news.length === 0 ? (
        <div className="text-center py-5 text-muted">
          No news articles found.
        </div>
      ) : (
        <Row xs={1} md={2} lg={3} className="g-4">
          {news.map((item) => (
            <Col key={item.newsArticleId}>
              <Card className="h-100 shadow-sm border-0">
                <Card.Body className="d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <Badge bg="secondary">
                      {item.category?.categoryName || "Uncategorized"}
                    </Badge>
                    <Badge bg={item.newsStatus ? "success" : "warning"}>
                      {item.newsStatus ? "Active" : "Inactive"}
                    </Badge>
                  </div>
                  <Card.Title className="h5 mb-2">{item.newsTitle}</Card.Title>
                  <Card.Subtitle className="mb-3 text-muted small">{item.headline}</Card.Subtitle>
                  <Card.Text className="text-truncate flex-grow-1">
                    {item.newsContent}
                  </Card.Text>

                  {item.tags && item.tags.length > 0 && (
                    <div className="mb-3 d-flex flex-wrap gap-1">
                      {item.tags.map(t => (
                        <Badge key={t.tagId} bg="light" text="dark" className="border">
                          #{t.tagName}
                        </Badge>
                      ))}
                    </div>
                  )}

                  <div className="mt-auto pt-2 border-top d-flex justify-content-between align-items-center">
                    <small className="text-muted">
                      By {item.createdBy?.accountName || "Staff"}
                    </small>
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

      {/* Detail Modal */}
      <Modal show={!!viewArticle} onHide={() => setViewArticle(null)} size="lg" centered>
        {viewArticle && (
          <>
            <Modal.Header closeButton>
              <Modal.Title>{viewArticle.newsTitle}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <div className="d-flex gap-2 mb-3">
                <Badge bg="primary">{viewArticle.category?.categoryName}</Badge>
                <Badge bg={viewArticle.newsStatus ? "success" : "warning"}>
                  {viewArticle.newsStatus ? "Active" : "Inactive"}
                </Badge>
              </div>
              <h5 className="text-muted mb-3">{viewArticle.headline}</h5>
              <p style={{ whiteSpace: "pre-wrap" }}>{viewArticle.newsContent}</p>
              <div className="mt-4 pt-3 border-top text-muted small">
                <div>Source: {viewArticle.newsSource || "FUNews Editorial"}</div>
                <div>Author: {viewArticle.createdBy?.accountName} ({viewArticle.createdBy?.accountEmail})</div>
              </div>
            </Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={() => setViewArticle(null)}>Close</Button>
            </Modal.Footer>
          </>
        )}
      </Modal>

      {/* Create/Edit Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} size="lg" centered>
        <Form onSubmit={handleSave}>
          <Modal.Header closeButton>
            <Modal.Title>{editingArticle ? "Edit News Article" : "Create News Article"}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>News Title *</Form.Label>
              <Form.Control
                type="text"
                required
                value={formData.newsTitle}
                onChange={(e) => setFormData({ ...formData, newsTitle: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Headline</Form.Label>
              <Form.Control
                type="text"
                value={formData.headline}
                onChange={(e) => setFormData({ ...formData, headline: e.target.value })}
              />
            </Form.Group>

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

            <Form.Group className="mb-3">
              <Form.Label>Tags</Form.Label>
              <div className="d-flex flex-wrap gap-2">
                {tags.map(t => (
                  <Button
                    key={t.tagId}
                    size="sm"
                    variant={formData.tagIds.includes(t.tagId) ? "primary" : "outline-secondary"}
                    onClick={() => handleTagToggle(t.tagId)}
                    type="button"
                  >
                    #{t.tagName}
                  </Button>
                ))}
              </div>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>News Content *</Form.Label>
              <Form.Control
                as="textarea"
                rows={5}
                required
                value={formData.newsContent}
                onChange={(e) => setFormData({ ...formData, newsContent: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>News Source</Form.Label>
              <Form.Control
                type="text"
                value={formData.newsSource}
                onChange={(e) => setFormData({ ...formData, newsSource: e.target.value })}
              />
            </Form.Group>

            <Form.Check
              type="checkbox"
              label="Active Status"
              checked={formData.newsStatus}
              onChange={(e) => setFormData({ ...formData, newsStatus: e.target.checked })}
            />
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
            <Button variant="primary" type="submit">
              {editingArticle ? "Save Changes" : "Create Article"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}
