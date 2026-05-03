# README.md

### Build the fat JAR
```bash
mvn package -DskipTests
```

### Run all containers
```bash
docker-compose up --build
```

### Trigger a workflow
Wait for all five containers to log "Started.", then copy any PurchaseOrder `.xml` into `data/input/`:
```bash
cp data/input/sample-po.xml data/input/po-$(date +%s).xml
```
The Buyer detects the file, assigns a flow GUID, and kicks off the chain. All eight documents appear under `data/output/<flowId>/` as the flow progresses.

### Watch the flow
```bash
docker-compose logs -f buyer supplier inventory logistics bank
```

## Architecture

### Message flow
```
data/input/*.xml
       ↓ (WatchService)
    Buyer  ──PurchaseOrder──►  Supplier  ──InventoryRequest──►  Inventory
    writes:                    writes:                           writes:
    PurchaseOrder.xml          InventoryRequest.xml              InventoryResponse.xml
                                    ▲  ◄──InventoryResponse────────────┘
                                    │
                                    ├──ShipmentRequest──►  Logistics
                                    │  writes:             writes:
                                    │  ShipmentRequest.xml ShipmentNotification.xml
                                    │  ◄──ShipmentNotification──────────┘
                                    │
                                    └──Invoice──►  Buyer
                                       writes:     writes:
                                       Invoice.xml PaymentInstruction.xml
                                                       │
                                                       └──PaymentInstruction──►  Bank
                                                                                 writes:
                                                                                 PaymentConfirmation.xml
                                                                                       │
                                                                        ┌──────────────┴──────────────┐
                                                                      Buyer                        Supplier
                                                                   (getSender)               (RemoteAddresses)
```

### Output directory layout
```
data/
├── input/                         ← Buyer watches here
│   └── sample-po.xml
└── output/
    └── <flowId>/                  ← one subfolder per triggered flow
        ├── PurchaseOrder.xml
        ├── InventoryRequest.xml
        ├── InventoryResponse.xml
        ├── ShipmentRequest.xml
        ├── ShipmentNotification.xml
        ├── Invoice.xml
        ├── PaymentInstruction.xml
        └── PaymentConfirmation.xml
```

All five containers mount `./data:/app/data` so they share the same filesystem tree.

