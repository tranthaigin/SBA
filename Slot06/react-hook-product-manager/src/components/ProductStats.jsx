import Card from "react-bootstrap/Card";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";

const money = new Intl.NumberFormat("vi-VN", {
  style: "currency",
  currency: "VND"
});

export default function ProductStats({ products }) {
  const itemTypes = products.length;
  const totalQuantity = products.reduce((sum, item) => sum + item.quantity, 0);
  const inventoryValue = products.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  const stats = [
    ["Product Types", itemTypes],
    ["Total Quantity", totalQuantity],
    ["Total Inventory Value", money.format(inventoryValue)]
  ];

  return (
    <Row className="g-3 mb-4">
      {stats.map(([label, value]) => (
        <Col key={label} xs={12} md={4}>
          <Card className="text-center shadow-sm h-100">
            <Card.Body>
              <div className="text-secondary small mb-1">{label}</div>
              <strong className="fs-5 text-primary">{value}</strong>
            </Card.Body>
          </Card>
        </Col>
      ))}
    </Row>
  );
}
