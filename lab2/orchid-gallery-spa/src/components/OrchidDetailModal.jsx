// src/components/OrchidDetailModal.jsx
import { Badge, Button, Modal } from 'react-bootstrap';

export default function OrchidDetailModal({ show, orchid, onClose }) {
  return (
    <Modal show={show} onHide={onClose} centered backdrop="static" keyboard={true}>
      <Modal.Header closeButton>
        <Modal.Title>{orchid?.orchidName ?? 'Orchid detail'}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {orchid ? (
          <>
            <img
              src={orchid.image}
              alt={orchid.orchidName}
              className="img-fluid rounded mb-3 w-100"
              style={{ maxHeight: '280px', objectFit: 'cover' }}
            />
            <p><strong>Category:</strong> {orchid.category}</p>
            <p><strong>Origin:</strong> {orchid.origin}</p>
            <p><strong>Color:</strong> {orchid.color}</p>
            <p><strong>Rating:</strong> {orchid.rating} / 5.0 ⭐</p>
            <p>
              <strong>Special:</strong>{' '}
              {orchid.isSpecial ? (
                <Badge bg="warning" text="dark">Yes</Badge>
              ) : (
                'No'
              )}
            </p>
            <p><strong>Description:</strong> {orchid.description}</p>
          </>
        ) : (
          <p>Chưa chọn Orchid.</p>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
