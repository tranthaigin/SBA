import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import InputGroup from "react-bootstrap/InputGroup";

export default function SearchBox({ value, onChange, inputRef }) {
  return (
    <InputGroup>
      <Form.Control
        ref={inputRef}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder="Search by product name..."
        aria-label="Search products"
      />
      <Button variant="outline-secondary" onClick={() => inputRef.current?.focus()}>
        Focus
      </Button>
    </InputGroup>
  );
}
