import { useEffect, useState } from "react";
import { Container, Table, Button, Alert, Badge, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function MyHistory() {
  const { user } = useAuth();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

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
              <th>Headline</th>
              <th>Category</th>
              <th>Created Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {history.map((art) => (
              <tr key={art.newsArticleId}>
                <td className="fw-mono text-muted">{art.newsArticleId}</td>
                <td className="fw-semibold">{art.headline}</td>
                <td>{art.category?.categoryName || "N/A"}</td>
                <td>{art.createdDate ? new Date(art.createdDate).toLocaleDateString() : "N/A"}</td>
                <td>
                  <Badge bg={art.newsStatus ? "success" : "secondary"}>
                    {art.newsStatus ? "Active" : "Inactive"}
                  </Badge>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
}
