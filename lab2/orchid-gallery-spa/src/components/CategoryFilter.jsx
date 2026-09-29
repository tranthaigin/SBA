// src/components/CategoryFilter.jsx
import { Form } from 'react-bootstrap';

export default function CategoryFilter({
  categories,
  selectedCategory,
  onSelectCategory,
  specialOnly,
  onToggleSpecial
}) {
  return (
    <div className="d-flex flex-wrap gap-3 align-items-end">
      <Form.Group style={{ minWidth: '180px' }}>
        <Form.Label className="fw-semibold small text-secondary mb-1">Category</Form.Label>
        <Form.Select
          value={selectedCategory}
          onChange={(e) => onSelectCategory(e.target.value)}
        >
          <option value="ALL">All Categories</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </Form.Select>
      </Form.Group>

      <Form.Group className="d-flex align-items-center mb-2">
        <Form.Check
          type="switch"
          id="special-filter-switch"
          label={<span className="fw-semibold small text-secondary">Special only</span>}
          checked={specialOnly}
          onChange={(e) => onToggleSpecial(e.target.checked)}
        />
      </Form.Group>
    </div>
  );
}
