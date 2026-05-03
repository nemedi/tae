# README.md

## Build and Run

```bash
# Build the JAR
mvn package -DskipTests

# Run locally (requires Kafka on localhost:9092)
mvn spring-boot:run

# Run the full demo via Docker (Zookeeper + Kafka + app)
docker-compose up --build

# View live logs
docker-compose logs -f order2cash

# Tear down
docker-compose down
```

To run a single test:
```bash
mvn test -Dtest=ClassName#methodName
```

## Order-to-Cash Flow

The application simulates five business parties communicating through Kafka topics. Each step receives an XML document, applies an XSLT transform to produce the next document, and publishes it to the next topic.

```
BUYER ──────────────────────────────────────────────────────────────
  [1] Creates PurchaseOrder XML → publishes to purchase-orders

SUPPLIER ───────────────────────────────────────────────────────────
  [2] purchase-orders  → XSLT 01 → InventoryRequest  → inventory-requests
  [4] inventory-responses → XSLT 03 → ShipmentRequest → shipment-requests
  [6] shipment-notifications → XSLT 05 → Invoice → invoices

INVENTORY ──────────────────────────────────────────────────────────
  [3] inventory-requests → XSLT 02 → InventoryResponse → inventory-responses

LOGISTICS ──────────────────────────────────────────────────────────
  [5] shipment-requests → XSLT 04 → ShipmentNotification → shipment-notifications

BUYER ──────────────────────────────────────────────────────────────
  [7] invoices → XSLT 06 → PaymentInstruction → payment-instructions

BANK ───────────────────────────────────────────────────────────────
  [8] payment-instructions → XSLT 07 → PaymentConfirmation → payment-confirmations

BUYER + SUPPLIER ───────────────────────────────────────────────────
  [9] receive payment-confirmations → flow complete
```

## Architecture

| Package / Path | Role |
|---|---|
| `service/BuyerService` | Initiates flow via `CommandLineRunner`; steps 1, 7, 9 |
| `service/SupplierService` | Steps 2, 4, 6, 9 |
| `service/InventoryService` | Step 3 |
| `service/LogisticsService` | Step 5 |
| `service/BankService` | Step 8 |
| `util/XsltTransformer` | Wraps JAXP `Transformer`; accepts classpath XSLT + param map |
| `config/KafkaTopics` | Central constants for all 8 topic names |
| `config/KafkaConfig` | Declares all `NewTopic` beans (auto-created on startup) |
| `resources/xslt/01-07-*.xslt` | One XSLT per transformation step |

## XSLT Conventions

- All transforms are XSLT 1.0 (JDK built-in Xalan — no extra dependency needed).
- Dynamic IDs and dates are injected as `<xsl:param>` from the calling service.
- Data that downstream steps need (delivery address, prices, payment terms) is carried forward via `<xsl:copy-of>` so each transform is self-contained.
- `format-number($value, '#0.00')` is used for monetary amounts; the invoice XSLT computes `SubTotal`, `TaxAmount` (10%), and `TotalAmount` using XPath `sum()`.

## Kafka Topics

| Topic | Producer | Consumer(s) |
|---|---|---|
| `purchase-orders` | Buyer | Supplier |
| `inventory-requests` | Supplier | Inventory |
| `inventory-responses` | Inventory | Supplier |
| `shipment-requests` | Supplier | Logistics |
| `shipment-notifications` | Logistics | Supplier |
| `invoices` | Supplier | Buyer |
| `payment-instructions` | Buyer | Bank |
| `payment-confirmations` | Bank | Buyer, Supplier |

