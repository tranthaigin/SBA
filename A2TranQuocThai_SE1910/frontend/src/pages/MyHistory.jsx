import { useEffect, useState } from "react";
import { Container, Table, Button, Alert, Badge, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

/**
 * Component trang Lịch sử đăng bài (MyHistory) dành cho Staff (Role 2).
 * Hiển thị toàn bộ các bài viết tin tức do chính tài khoản Staff này làm tác giả.
 */
export default function MyHistory() {
  const { user } = useAuth();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  /**
   * Tải danh sách các bài viết do user hiện tại tạo từ endpoint /api/news/created-by/{userId}.
   */
  const fetchHistory = async () => {
    if (!user) return;
    setLoading(true);
    setError("");
    try {
      const res = await axios.get(`/api/news/created-by/${user.accountId}`);
      setHistory(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load news history");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, [user]);

  // Yêu cầu quyền Staff
  if (!user || user.accountRole !== 2) {
    return (
      <Container className="py-5 text-center">
        <Alert variant="warning">
          Please log in as a Staff member to view your article publishing history.
        </Alert>
      </Container>
    );
  }

  return (
    <Container className="py-2">
      <div className="mb-4">
        <h2>My Publishing History</h2>
        <small className="text-muted">
          Articles created by {user.accountName} ({user.accountEmail})
        </small>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="primary" />
        </div>
      ) : history.length === 0 ? (
        <div className="text-center py-5 text-muted">
          You have not created any news articles yet.
        </div>
      ) : (
        <Table responsive hover className="align-middle border shadow-sm">
          <thead className="table-light">
            <tr>
              <th>Article ID</th>
              <th>Title</th>
              <th>Category</th>
              <th>Status</th>
              <th>Created Date</th>
            </tr>
          </thead>
          <tbody>
            {history.map((a) => (
              <tr key={a.newsArticleId}>
                <td><code>{a.newsArticleId}</code></td>
                <td className="fw-semibold">{a.newsTitle}</td>
                <td>
                  <Badge bg="secondary">
                    {a.category?.categoryName || "Uncategorized"}
                  </Badge>
                </td>
                <td>
                  <Badge bg={a.newsStatus ? "success" : "warning"}>
                    {a.newsStatus ? "Active" : "Inactive"}
                  </Badge>
                </td>
                <td className="text-muted small">
                  {a.createdDate ? new Date(a.createdDate).toLocaleString() : "-"}
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
}
