// src/components/LoadingSpinner.jsx
import { Spinner } from 'react-bootstrap';

export default function LoadingSpinner() {
  return (
    <div className="text-center py-5">
      <Spinner animation="border" role="status" variant="primary">
        <span className="visually-hidden">Loading...</span>
      </Spinner>
      <div className="mt-2 text-muted fw-semibold">Loading Orchids...</div>
    </div>
  );
}
