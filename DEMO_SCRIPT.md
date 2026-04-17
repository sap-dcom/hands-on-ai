# 🎬 Live Demo Script
## AI Agent Creates CAP Java + Fiori UI

> **Speaker:** Follow this script during the live demo portion of the tech talk.  
> **Duration:** ~15 minutes  
> **Pre-requisite:** Cline is running with cap-js-mcp, fiori-mcp, and ui5-mcp-server connected.

---

## Before You Start

### ✅ Pre-Demo Checklist

- [ ] VS Code is open with Cline extension visible
- [ ] cap-js-mcp is connected (green dot in Cline MCP panel)
- [ ] fiori-mcp is connected (green dot)
- [ ] ui5-mcp-server is connected (green dot)
- [ ] Our shopping cart MCP server is running (`mvn spring-boot:run`)
- [ ] Terminal is open and ready
- [ ] Cline is in **ACT mode** (not Plan mode)
- [ ] Zoom/screenshare is showing VS Code full screen

---

## Demo Part 1: Show the "Before" State [2 min]

**Say this:**
> "We're starting with a completely empty folder. No code, no project files, nothing.
> I'm going to type ONE sentence into Cline, and watch what happens."

**Action:** Show an empty directory in the file explorer.

**Say this:**
> "But before that — look at Cline's tool panel. These are the MCP tools the agent has access to."

**Action:** Click on the MCP section in Cline. Show the tools from:
- `cap-js-mcp` (search_model, search_docs)
- `fiori-mcp` (list_functionality, get_functionality_details, execute_functionality)
- `ich_mcp_server_local` (addItem, getItems, removeItem)

**Say this:**
> "Each of these tools is like a function the AI can call.
> The LLM decides — based on our goal — which tools to call and in what order.
> That decision-making loop is what makes it an AI agent."

---

## Demo Part 2: Simple Agent Task (Warm-up) [3 min]

**Say this:**
> "Let's warm up with our shopping cart. I'll show you the agent making multiple tool calls automatically."

**Type in Cline:**
```
I need to buy groceries. Please add:
- 2 packs of milk
- 3 chocolate bars
- 1 loaf of bread

Then show me the complete shopping list and tell me how many total items I have.
```

**What to narrate while agent works:**
- "See — it's calling `addItem` three times, one for each item"
- "Now it's calling `getItems` to retrieve the list"
- "It counted the items itself — that's the LLM reasoning over the tool output"

**Point out to audience:**
> "This is the ReAct loop: Think → Call `addItem` → Observe result → Think → Call `addItem` again → ... → Think → Call `getItems` → Observe → Summarize."

---

## Demo Part 3: The Main Act — CAP Java + Fiori [10 min]

### 3a. Set the Stage [1 min]

**Say this:**
> "Now for the real demo. I'm going to ask the agent to create a complete, production-ready SAP application.
> 
> CAP Java backend. OData V4 service. Fiori Elements UI. All connected. All working.
> 
> From one prompt."

### 3b. Type the Prompt [30 sec]

**Type in Cline:**
```
Create a complete SAP CAP Java application for managing Purchase Orders.

Requirements:

1. CAP Java Backend:
   - Entity: PurchaseOrder (ID, orderNumber String, vendor String, 
     amount Decimal, currency String(3), status String with enum NEW/APPROVED/REJECTED)
   - Entity: OrderItem (ID, order Association to PurchaseOrder, 
     product String, quantity Integer, unitPrice Decimal)
   - OData V4 service at path /odata/v4/po

2. SAP Fiori Elements Frontend:
   - List Report for Purchase Orders showing: orderNumber, vendor, amount, status
   - Object Page with order details and OrderItems as a table section

Create the project in ./cap-po-demo/ directory.
```

### 3c. Narrate the Agent's Actions [8 min]

**As the agent works, narrate each major step:**

**When agent calls `cap-js-mcp.search_docs`:**
> "The agent is consulting the CAP documentation. It's reading best practices for CAP Java structure before writing any code."

**When agent creates `pom.xml`:**
> "First file: the Maven POM. Notice it's using the CDS Maven plugin — the agent knows this is required for CAP Java."

**When agent creates `db/schema.cds`:**
> "The domain model. The agent created the entities based on our requirements. Look at the Composition relationship between PurchaseOrder and OrderItem — that's advanced CDS modeling."

**When agent creates `srv/po-service.cds`:**
> "The OData service definition. The agent automatically exposed both entities through the service."

**When agent calls `fiori-mcp.list_functionality`:**
> "NOW — the agent is using the fiori-mcp tool! It's asking: what Fiori features can I generate? This is the agent reasoning about its available tools."

**When agent calls `fiori-mcp.get_functionality_details`:**
> "The agent picked the right functionality and is now getting the exact parameters it needs to call it correctly."

**When agent calls `fiori-mcp.execute_functionality`:**
> "This is the money call. Fiori scaffold is being generated right now. Annotations, manifest, routing — all automated."

**When agent runs linter:**
> "The agent is self-verifying. It's not done until it confirms there are no errors."

### 3d. Show the Result [1 min]

**After agent finishes, do this:**

```bash
find ./cap-po-demo -type f | head -30
```

> "Look at everything that was created. Package structure, CDS models, service definitions, Fiori app with annotations, manifest.json — a complete application."

**Open key files:**
1. `db/schema.cds` — "This is exactly what we asked for"
2. `app/po-list/annotations.cds` — "Fiori UI annotations. The agent knew which fields to show in the list."
3. `pom.xml` — "CDS Maven plugin, Spring Boot — it's a real CAP Java project"

---

## Demo Part 4: The Comparison [1 min]

**Say this:**
> "How long would this have taken you manually?
> 
> Setting up Maven, adding the right CDS plugin version, defining entities with the right syntax,
> writing the Fiori annotations, wiring up the manifest.json...
> 
> For an experienced developer: 2-3 hours.
> For someone new to CAP: maybe a full day.
> 
> The agent just did it in 3 minutes."

**Pause for effect, then:**

> "And the best part? This is just one agent, one goal.
> 
> You can build multi-agent systems where Agent 1 creates the backend,
> Agent 2 writes the tests, Agent 3 reviews for security issues,
> and Agent 4 creates the deployment pipeline.
> 
> All running autonomously. All using MCP tools."

---

## Fallback Plan (If Demo Fails)

If Cline or MCP tools are unavailable, use this pre-recorded output:

### Show this pre-prepared file structure:
```
cap-po-demo/
├── pom.xml
├── package.json
├── .cdsrc.json
├── db/
│   └── schema.cds
├── srv/
│   └── po-service.cds
└── app/
    └── po-list/
        ├── webapp/
        │   ├── manifest.json
        │   └── Component.js
        ├── annotations.cds
        └── ui5.yaml
```

### Show these key files:

**db/schema.cds:**
```cds
namespace com.sap.purchaseorders;

entity PurchaseOrder {
    key ID          : UUID;
    orderNumber     : String(20);
    vendor          : String(100);
    amount          : Decimal(10,2);
    currency        : String(3);
    status          : String(20) default 'NEW';
    createdAt       : DateTime;
    items           : Composition of many OrderItem on items.order = $self;
}

entity OrderItem {
    key ID          : UUID;
    order           : Association to PurchaseOrder;
    product         : String(100);
    quantity        : Integer;
    unitPrice       : Decimal(10,2);
}
```

**srv/po-service.cds:**
```cds
using com.sap.purchaseorders as db from '../db/schema';

service PurchaseOrderService @(path: '/odata/v4/po') {
    @odata.draft.enabled: true
    entity PurchaseOrders as projection on db.PurchaseOrder;
    entity OrderItems     as projection on db.OrderItem;
}
```

**app/po-list/annotations.cds:**
```cds
using PurchaseOrderService from '../../srv/po-service';

annotate PurchaseOrderService.PurchaseOrders with @(
    UI.LineItem: [
        { Value: orderNumber, Label: 'Order Number' },
        { Value: vendor,      Label: 'Vendor'       },
        { Value: amount,      Label: 'Amount'        },
        { Value: currency,    Label: 'Currency'      },
        { Value: status,      Label: 'Status'        }
    ],
    UI.HeaderInfo: {
        TypeName       : 'Purchase Order',
        TypeNamePlural : 'Purchase Orders',
        Title          : { Value: orderNumber },
        Description    : { Value: vendor }
    },
    UI.FieldGroup#GeneralInfo: {
        Data: [
            { Value: orderNumber },
            { Value: vendor },
            { Value: amount },
            { Value: currency },
            { Value: status }
        ]
    },
    UI.Facets: [
        {
            $Type  : 'UI.ReferenceFacet',
            Label  : 'General Information',
            Target : '@UI.FieldGroup#GeneralInfo'
        },
        {
            $Type  : 'UI.ReferenceFacet',
            Label  : 'Order Items',
            Target : 'items/@UI.LineItem'
        }
    ]
);

annotate PurchaseOrderService.OrderItems with @(
    UI.LineItem: [
        { Value: product,   Label: 'Product'    },
        { Value: quantity,  Label: 'Quantity'   },
        { Value: unitPrice, Label: 'Unit Price' }
    ]
);
```

---

## Q&A Talking Points

**Q: "Can the agent deploy to Cloud Foundry automatically?"**
> Yes — you'd add an MCP tool that wraps `cf push`. The agent would call it after building.

**Q: "How does the agent know which MCP tools to use?"**
> The tool descriptions tell the LLM when to use each tool. Good descriptions = smarter agents.

**Q: "What if the agent makes a mistake?"**
> It usually self-corrects — it sees errors from the linter/build tools and fixes them.
> For production: add human approval steps for destructive operations.

**Q: "Is this safe? Can the agent delete files?"**
> Yes — Cline has an approval system. You configure which tools need human confirmation.
> Our `autoApprove` config in the MCP settings controls this.

**Q: "How much does it cost to run?"**
> GPT-4o is ~$15/million tokens. A typical scaffolding task uses 10-50K tokens.
> Complex agent tasks: ~$0.15-0.75 per task.

**Q: "Can we build agents that don't use Cline?"**
> Absolutely. Spring AI lets you build agents in pure Java.
> LangChain/LangGraph for Python. The MCP tools work with all of them.

---

*Demo prepared for D-COM Innovation Tech Talk 2026*