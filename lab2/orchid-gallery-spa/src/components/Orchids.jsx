// src/components/Orchids.jsx
import { useMemo, useState } from 'react';
import { Button, Card, Col, Container, Row } from 'react-bootstrap';
import useOrchids from '../hooks/useOrchids';
import CategoryFilter from './CategoryFilter';
import ErrorMessage from './ErrorMessage';
import LoadingSpinner from './LoadingSpinner';
import OrchidCard from './OrchidCard';
import OrchidDetailModal from './OrchidDetailModal';
import SearchBox from './SearchBox';

export default function Orchids() {
  const { orchids, loading, error, reload } = useOrchids();
  const [show, setShow] = useState(false);
  const [selectedOrchid, setSelectedOrchid] = useState(null);

  // Search & Filter state (Step 23 - Derived view)
  const [keyword, setKeyword] = useState('');
  const [category, setCategory] = useState('ALL');
  const [specialOnly, setSpecialOnly] = useState(false);

  const handleShow = (orchid) => {
    setSelectedOrchid(orchid);
    setShow(true);
  };

  const handleClose = () => {
    setShow(false);
    setSelectedOrchid(null);
  };

  // Derive available categories dynamically from orchids data
  const categories = useMemo(() => {
    const set = new Set(orchids.map((o) => o.category));
    return Array.from(set).filter(Boolean);
  }, [orchids]);

  // Derived state for filtered list (no extra network request)
  const visibleOrchids = useMemo(() => {
    return orchids.filter((o) => {
      const matchName = o.orchidName.toLowerCase().includes(keyword.trim().toLowerCase());
      const matchCategory = category === 'ALL' || o.category === category;
      const matchSpecial = !specialOnly || o.isSpecial;
      return matchName && matchCategory && matchSpecial;
    });
  }, [orchids, keyword, category, specialOnly]);

  const handleResetFilters = () => {
    setKeyword('');
    setCategory('ALL');
    setSpecialOnly(false);
  };

  return (
    <Container id="orchids" className="py-4">
      {/* Top Header & Reload Action */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-1 text-primary fw-bold">Orchids Collection</h2>
          <small className="text-muted">Explore rare and beautiful orchids</small>
        </div>
        <Button
          variant="outline-primary"
          onClick={reload}
          disabled={loading}
          className="d-flex align-items-center gap-2"
        >
          <span>↻</span>
          <span>{loading ? 'Refreshing...' : 'Reload'}</span>
        </Button>
      </div>

      {/* Filter / Search Bar (Derived state) */}
      <Card className="p-3 mb-4 filter-bar border-0 shadow-sm">
        <Row className="g-3 align-items-center">
          <Col md={5} sm={12}>
            <SearchBox value={keyword} onChange={setKeyword} />
          </Col>
          <Col md={7} sm={12}>
            <CategoryFilter
              categories={categories}
              selectedCategory={category}
              onSelectCategory={setCategory}
              specialOnly={specialOnly}
              onToggleSpecial={setSpecialOnly}
            />
          </Col>
        </Row>
      </Card>

      {/* Async State: Loading */}
      {loading && <LoadingSpinner />}

      {/* Async State: Error */}
      {error && <ErrorMessage message={error} onRetry={reload} />}

      {/* Derived UI State: Empty List */}
      {!loading && !error && visibleOrchids.length === 0 && (
        <div className="text-center py-5">
          <p className="text-muted fs-5 mb-3">Không có Orchid nào phù hợp với bộ lọc hiện tại.</p>
          <Button variant="secondary" size="sm" onClick={handleResetFilters}>
            Xóa bộ lọc
          </Button>
        </div>
      )}

      {/* Async State: Data Cards */}
      {!loading && !error && visibleOrchids.length > 0 && (
        <Row>
          {visibleOrchids.map((orchid) => (
            <Col xs={12} sm={6} lg={3} key={orchid.id} className="mb-4">
              <OrchidCard orchid={orchid} onDetail={handleShow} />
            </Col>
          ))}
        </Row>
      )}

      {/* Detail Modal */}
      <OrchidDetailModal
        show={show}
        orchid={selectedOrchid}
        onClose={handleClose}
      />
    </Container>
  );
}
