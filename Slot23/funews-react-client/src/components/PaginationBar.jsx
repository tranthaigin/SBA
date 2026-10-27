import { Button, ButtonGroup } from "react-bootstrap";

export default function PaginationBar({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) {
    return null;
  }

  return (
    <div className="d-flex justify-content-between align-items-center mt-3">
      <div className="text-muted">
        Page {page + 1} / {totalPages}
      </div>
      <ButtonGroup>
        <Button
          variant="outline-primary"
          disabled={page <= 0}
          onClick={() => onPageChange(page - 1)}
        >
          Previous
        </Button>
        <Button
          variant="outline-primary"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
        >
          Next
        </Button>
      </ButtonGroup>
    </div>
  );
}
