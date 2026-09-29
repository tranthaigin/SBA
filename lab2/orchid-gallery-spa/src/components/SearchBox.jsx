// src/components/SearchBox.jsx
import { Form, InputGroup } from 'react-bootstrap';

export default function SearchBox({ value, onChange }) {
  return (
    <Form.Group className="mb-0 flex-grow-1" style={{ minWidth: '220px' }}>
      <Form.Label className="fw-semibold small text-secondary mb-1">Search by Name</Form.Label>
      <InputGroup>
        <Form.Control
          type="text"
          placeholder="Type orchid name..."
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
        {value && (
          <InputGroup.Text
            style={{ cursor: 'pointer' }}
            onClick={() => onChange('')}
            title="Clear search"
          >
            ✕
          </InputGroup.Text>
        )}
      </InputGroup>
    </Form.Group>
  );
}
