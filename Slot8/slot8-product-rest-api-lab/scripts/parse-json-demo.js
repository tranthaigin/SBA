const productJson = `
{
  "id": 1,
  "name": "iPhone 15",
  "category": "Phone",
  "price": 25000000,
  "quantity": 10,
  "active": true
}
`;

console.log("1) Raw JSON text:");
console.log(productJson);

const productObject = JSON.parse(productJson);
console.log("2) JavaScript object:");
console.log(productObject);
console.log("Name:", productObject.name);
console.log("Price:", productObject.price);

const jsonString = JSON.stringify(productObject, null, 2);
console.log("3) JSON.stringify result:");
console.log(jsonString);
