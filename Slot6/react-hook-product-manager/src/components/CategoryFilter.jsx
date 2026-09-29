import Form from "react-bootstrap/Form";

export default function CategoryFilter({ value, categories, onChange }) {
  return (
    <Form.Select value={value} onChange={(event) => onChange(event.target.value)}>
      {categories.map((category) => (
        <option key={category} value={category}>
          {category}
        </option>
      ))}
    </Form.Select>
  );
}
