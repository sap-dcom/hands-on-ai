# 🤖 Creating AI Agents: From Concepts to Production
### Tech Talk Guide — Building an AI Agent that Scaffolds CAP Java + Fiori UI

---

## 🎯 What This Talk Covers

This talk builds on our MCP server demo (shopping cart) and answers the bigger question:

> **"How do AI agents actually work — and how can we build one that creates a full SAP CAP Java + Fiori application from a single sentence?"**

We'll go from theory → architecture → live demo in ~45 minutes.

---

## 📋 Agenda

| # | Topic | Time |
|---|-------|------|
| 1 | What is an AI Agent? | 5 min |
| 2 | Anatomy of an Agent (LLM + Tools + Loop) | 8 min |
| 3 | MCP: The Tool Protocol for AI Agents | 7 min |
| 4 | Recap: Our MCP Server Demo (Shopping Cart) | 5 min |
| 5 | **LIVE DEMO: AI Agent builds CAP Java + Fiori UI** | 15 min |
| 6 | Q&A | 5 min |

---

## Module 1: What is an AI Agent?

### The Simple Definition

> An **AI Agent** is a system that uses an LLM (Large Language Model) to **perceive** its environment, **reason** about what to do, **take actions** using tools, and **repeat** until a goal is achieved.

### AI Assistant vs AI Agent

| | AI Assistant | AI Agent |
|--|--|--|
| **Scope** | Single response | Multi-step tasks |
| **Tools** | Usually none | Many (files, APIs, terminal) |
| **Memory** | Short-term | Short + long-term |
| **Autonomy** | Low | High |
| **Example** | "Explain what is CAP" | "Create a CAP Java app with Fiori UI" |

### The Key Insight

A regular LLM answers questions.  
An AI agent **does things**.

```
User: "Create a purchase order management app in CAP Java with Fiori UI"

LLM alone → gives you code snippets to copy-paste
AI Agent  → creates the full project, runs it, fixes errors, shows you the result
```

---

## Module 2: Anatomy of an AI Agent

### The 4 Core Components

```
┌─────────────────────────────────────────────────────────┐
│                      AI AGENT                           │
│                                                         │
│  ┌──────────┐    ┌──────────┐    ┌────────────────┐   │
│  │   LLM    │───▶│  Tools   │───▶│  Orchestration │   │
│  │(GPT-4o)  │    │ (MCP)    │    │    Loop        │   │
│  └──────────┘    └──────────┘    └────────────────┘   │
│        │              │                   │             │
│        └──────────────┴───────────────────┘             │
│                        │                                │
│                  ┌──────────┐                           │
│                  │  Memory  │                           │
│                  │(Context) │                           │
│                  └──────────┘                           │
└─────────────────────────────────────────────────────────┘
```

### Component 1: LLM (The Brain)
- Understands natural language goals
- Decides **which tool to call** and with **what parameters**
- Interprets tool results and plans next steps
- Examples: GPT-4o, Claude 3.5, Gemini Pro

### Component 2: Tools (The Hands)
- Anything the agent can **do** in the world
- File system operations, terminal commands, API calls
- In our case: **MCP servers** provide tools
- Examples: `create_cap_project`, `add_fiori_app`, `run_ui5_linter`

### Component 3: Orchestration Loop (The ReAct Pattern)
The agent runs in a continuous loop:

```
GOAL: "Create CAP Java + Fiori app for purchase orders"
  │
  ▼
┌─────────────────────────────────────────┐
│  1. THINK: What's the first step?       │
│     → "I need to create a CAP project"  │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│  2. ACT: Call a tool                    │
│     → cap-js-mcp.search_docs("CAP Java")│
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│  3. OBSERVE: Get tool result            │
│     → "CAP Java uses cds-maven-plugin"  │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│  4. THINK: What next?                   │
│     → "Create pom.xml with CDS plugin"  │
└──────────────────┬──────────────────────┘
                   │
                   ▼
            [REPEAT until done]
```

This is called the **ReAct pattern** (Reason + Act).

### Component 4: Memory
- **Short-term**: The conversation context (what happened so far)
- **Long-term**: System prompt, project rules, saved knowledge
- In Cline: `.clinerules` files provide long-term memory

---

## Module 3: MCP — The Tool Protocol for AI Agents

### What Problem Does MCP Solve?

Before MCP, every AI tool had its own custom API. A Jira tool for Claude didn't work with GPT-4o. A GitHub tool for one IDE didn't work with another.

**MCP (Model Context Protocol)** = A universal standard for AI tools.

```
Before MCP:
  Agent A ──(custom)──▶ Tool 1
  Agent B ──(custom)──▶ Tool 1 (incompatible!)
  Agent C ──(custom)──▶ Tool 1 (incompatible!)

After MCP:
  Agent A ──(MCP)──▶ MCP Server ◀──(MCP)── Agent B
  Agent C ──(MCP)──────────────────────────────────
                     (any agent works!)
```

### MCP Architecture

```
┌────────────────┐         ┌─────────────────────┐
│   MCP CLIENT   │         │    MCP SERVER        │
│  (Cline/IDE)   │◀───────▶│  (Tool Provider)     │
│                │  JSON   │                     │
│  - Discovers   │  over   │  - Exposes Tools    │
│    tools       │  HTTP/  │  - Handles calls    │
│  - Calls tools │  STDIO  │  - Returns results  │
│  - Gets results│         │                     │
└────────────────┘         └─────────────────────┘
```

### Types of MCP Transport

| Transport | When to Use | Example |
|-----------|-------------|---------|
| **STDIO** | Local tools, same machine | `npx -y @cap-js/mcp-server` |
| **HTTP/SSE** | Remote tools, microservices | `http://my-tool-server/mcp` |
| **Streamable HTTP** | Our demo! Real-time streaming | `http://localhost:8080/api/mcp` |


When Cline connects to our server, it sees these tools and can call them autonomously.

### The MCP Ecosystem for SAP Development

| MCP Server | What It Provides |
|------------|-----------------|
| `cap-js-mcp` | CAP model search, docs, schema inspection |
| `fiori-mcp` | Fiori app generation, annotations, linting |
| `ui5-mcp-server` | UI5 API reference, linting, app creation |
| `sap-jira` | Jira issue management |
| `sap-wiki` | SAP Wiki search and content |
| `github-tools` | PR reviews, code management |

---

## Module 4: Connect to MCP Server


## Module 6: Create Skills




## Module 5: LIVE DEMO — AI Agent Builds CAP Java + Fiori UI

### 🎬 The Big Picture

We'll show Cline acting as an **autonomous AI agent** to:
1. Scaffold a complete CAP Java project
2. Define a domain model (Purchase Orders)
3. Add a SAP Fiori Elements UI
4. Wire everything together

**All from a single natural language prompt.**

### Prerequisites for the Demo

Make sure these MCP servers are connected in Cline:

```json
{
  "mcpServers": {
    "cap-js-mcp": {
      "command": "npx",
      "args": ["-y", "@cap-js/mcp-server"],
      "disabled": false
    },
    "fiori-mcp": {
      "command": "npx",
      "args": ["--yes", "@sap-ux/fiori-mcp-server@latest", "fiori-mcp"],
      "disabled": false
    }
  }
}
```

### Demo Step 1: Understand the Agent's Starting State

Before typing the prompt, show the audience:
- Empty directory (or a simple folder)
- Cline's MCP tool panel — show the available tools from cap-js-mcp and fiori-mcp
- Explain: "These tools are the agent's hands. The LLM is its brain."

### Demo Step 2: The Single Prompt

Type this in Cline:

```
Create a complete SAP CAP Java application with Fiori UI for managing Purchase Orders.

Requirements:
Backend (CAP Java):
- Entity: PurchaseOrder (ID, orderNumber, vendor, amount, currency, status, createdAt)
- Entity: OrderItem (ID, purchaseOrder, product, quantity, unitPrice)
- OData V4 service exposing both entities with CRUD

Frontend (SAP Fiori Elements):
- List Report showing all Purchase Orders
- Object Page with Order details and line items table
- Create/Edit forms for both entities

Please scaffold the complete project under ./cap-java-demo/
```

### Demo Step 3: Watch the Agent Work

The agent will autonomously:

```
Step 1: THINK
  → "I need to check how to create a CAP Java project"
  → Calls: cap-js-mcp.search_docs("CAP Java maven project structure")

Step 2: ACT
  → Creates project directory structure
  → Writes pom.xml with CDS Maven plugin
  → Creates db/schema.cds with domain model

Step 3: OBSERVE
  → Reads file system to verify creation
  → Checks model is valid

Step 4: THINK
  → "Now I need the service definition"
  → Creates srv/po-service.cds

Step 5: ACT (Fiori)
  → Calls: fiori-mcp.list_functionality()
  → Gets list of supported functionalities

Step 6: THINK
  → Calls: fiori-mcp.get_functionality_details(...)
  → Gets exact parameters for Fiori app generation

Step 7: ACT
  → Calls: fiori-mcp.execute_functionality(...)
  → Fiori app is scaffolded with annotations

Step 8: VERIFY
  → Runs: ui5-mcp-server.run_ui5_linter()
  → Fixes any issues found

Step 9: DONE
  → Reports: "Your CAP Java + Fiori app is ready!"
```

### What the Agent Creates

```
cap-java-demo/
├── pom.xml                        ← CAP Java + Spring Boot
├── package.json                   ← CDS tools
├── .cdsrc.json                    ← CAP configuration
├── db/
│   └── schema.cds                 ← Domain model
│       ├── entity PurchaseOrder
│       └── entity OrderItem
├── srv/
│   ├── po-service.cds             ← OData V4 service
│   └── po-service.java            ← Custom handlers (if needed)
├── app/
│   └── po-list/                   ← Fiori Elements app
│       ├── webapp/
│       │   ├── manifest.json
│       │   └── Component.js
│       ├── annotations.cds        ← UI annotations
│       └── ui5.yaml
└── README.md
```

### Key Files Explained

**db/schema.cds** — Domain Model
```cds
namespace com.sap.purchaseorders;

entity PurchaseOrder {
    key ID         : UUID;
    orderNumber    : String(20);
    vendor         : String(100);
    amount         : Decimal(10,2);
    currency       : String(3);
    status         : String(20) enum {
        NEW = 'NEW'; 
        APPROVED = 'APPROVED'; 
        REJECTED = 'REJECTED';
    };
    createdAt      : DateTime;
    items          : Composition of many OrderItem on items.order = $self;
}

entity OrderItem {
    key ID         : UUID;
    order          : Association to PurchaseOrder;
    product        : String(100);
    quantity       : Integer;
    unitPrice      : Decimal(10,2);
}
```

**srv/po-service.cds** — Service Definition
```cds
using com.sap.purchaseorders as db from '../db/schema';

service PurchaseOrderService @(path: '/odata/v4/po') {
    entity PurchaseOrders as projection on db.PurchaseOrder;
    entity OrderItems     as projection on db.OrderItem;
}
```

**app/po-list/annotations.cds** — Fiori UI Annotations
```cds
using PurchaseOrderService from '../../srv/po-service';

annotate PurchaseOrderService.PurchaseOrders with @(
    UI.LineItem: [
        { Value: orderNumber, Label: 'Order #' },
        { Value: vendor, Label: 'Vendor' },
        { Value: amount, Label: 'Amount' },
        { Value: status, Label: 'Status' }
    ],
    UI.HeaderInfo: {
        TypeName: 'Purchase Order',
        TypeNamePlural: 'Purchase Orders',
        Title: { Value: orderNumber }
    }
);
```

---

## Module 6: What Makes This "Agentic"?

### Why This Is More Than Just Code Generation

| Characteristic | What Cline Does |
|---------------|-----------------|
| **Multi-step** | Creates 10+ files in the right order |
| **Tool use** | Calls cap-js-mcp, fiori-mcp, file tools |
| **Error recovery** | If linter finds issues, agent fixes them |
| **Decision making** | Chooses CDS annotations based on entity types |
| **Context awareness** | Remembers what was created in step 1 for step 7 |

### The ReAct Loop in Action

```
Observe: Empty folder
Think:   "Start with pom.xml"
Act:     write_to_file("pom.xml", ...)
Observe: pom.xml created
Think:   "Now schema.cds"
Act:     write_to_file("db/schema.cds", ...)
Observe: Schema created
Think:   "Check what fiori-mcp can do"
Act:     fiori-mcp.list_functionality()    ← MCP tool call!
Observe: [list of capabilities returned]
Think:   "I'll use 'Add Fiori Elements Page' functionality"
Act:     fiori-mcp.get_functionality_details(...)
Observe: Required parameters received
Think:   "Execute with our service and entity"
Act:     fiori-mcp.execute_functionality(...)  ← MCP tool call!
Observe: Fiori app created
Think:   "Lint the UI to verify"
Act:     ui5-mcp-server.run_ui5_linter(...)  ← MCP tool call!
Observe: "No issues found"
Think:   "Task complete!"
Act:     Report result to user
```

### The Agent Composes Multiple MCP Servers

```
                    ┌───────────────────┐
                    │   CLINE AGENT     │
                    │   (GPT-4o brain)  │
                    └────────┬──────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
    │  cap-js-mcp  │ │  fiori-mcp   │ │ ui5-mcp-     │
    │              │ │              │ │ server       │
    │ - search_    │ │ - list_      │ │ - run_       │
    │   model      │ │   function   │ │   linter     │
    │ - search_    │ │ - get_       │ │ - get_       │
    │   docs       │ │   details    │ │   api_ref    │
    └──────────────┘ │ - execute_   │ └──────────────┘
                     │   function   │
                     └──────────────┘
```

---

## Module 7: How to Build Your Own AI Agent

### Option A: Use Cline + MCP (What We Did Today)

Best for: Developer productivity, code generation, enterprise integrations

```
1. Build MCP Server (Spring Boot + Spring AI)
   └─ Expose domain tools with @Tool annotations

2. Configure Cline with your MCP server
   └─ mcpServers config in Cline settings

3. Write a good system prompt
   └─ .clinerules or Cline custom instructions
```

### Option B: Build a Custom Agent in Java (Spring AI)

Best for: Embedded agents in your own application, production automation

```java
// Spring AI Agent with MCP Tools
@Service
public class CAPJavaAgentService {

    private final ChatClient chatClient;
    
    public CAPJavaAgentService(ChatClient.Builder builder, 
                               ToolCallbackProvider tools) {
        this.chatClient = builder
            .defaultSystem("""
                You are an expert SAP CAP Java developer.
                You create complete CAP Java applications with Fiori UI.
                Always follow SAP best practices.
                """)
            .defaultToolCallbacks(tools)
            .build();
    }
    
    public String createApp(String requirement) {
        return chatClient.prompt()
            .user(requirement)
            .call()
            .content();
    }
}
```

### Option C: Python Agent with LangChain / LangGraph

Best for: Data pipelines, ML workflows, Python ecosystem

```python
from langchain_openai import ChatOpenAI
from langchain.agents import create_react_agent, AgentExecutor
from langchain_mcp_adapters import load_mcp_tools

# Load tools from MCP server
tools = await load_mcp_tools("http://localhost:8080/api/mcp")

# Create ReAct agent
agent = create_react_agent(
    llm=ChatOpenAI(model="gpt-4o"),
    tools=tools,
    prompt=react_prompt
)

# Run agent
executor = AgentExecutor(agent=agent, tools=tools)
result = executor.invoke({"input": "Create CAP Java app for purchase orders"})
```

---

## Module 8: Real-World Patterns and Best Practices

### Pattern 1: Tool Descriptions Matter

The LLM decides which tool to call based on the **description**:

```java
// ❌ Bad description
@Tool(name = "addItem", description = "Adds item")
public String addItem(String name, int quantity) { ... }

// ✅ Good description
@Tool(
    name = "addItem",
    description = """
        Add an item to the shopping list or update its quantity if it already exists.
        Use this when the user wants to add groceries, products, or any item to track.
        Parameters: name (item name), quantity (how many to add, must be > 0)
        Returns a confirmation message.
        """
)
public String addItem(String name, int quantity) { ... }
```

### Pattern 2: Structured Output for Reliability

```java
// Use records for structured tool output
public record CreateProjectResult(
    boolean success,
    String projectPath,
    List<String> filesCreated,
    String message
) {}

@Tool(name = "createCAPProject", description = "Creates a new CAP Java project")
public CreateProjectResult createCAPProject(String name, String namespace) {
    // ... implementation
    return new CreateProjectResult(true, projectPath, files, "Project created");
}
```

### Pattern 3: Human in the Loop

Not every action should be automatic. For destructive operations:

```java
@Tool(
    name = "deleteProject",
    description = "DELETE a CAP project. ALWAYS ask user for confirmation first."
)
public String deleteProject(String projectPath) {
    // The description instructs the LLM to confirm with user first
}
```

### Pattern 4: Error Handling and Recovery

```java
@Tool(name = "buildProject", description = "Build the Maven project")
public String buildProject(String projectDir) {
    try {
        Process process = Runtime.getRuntime().exec(
            new String[]{"mvn", "clean", "install"}, null, new File(projectDir)
        );
        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            String errors = new String(process.getErrorStream().readAllBytes());
            // Return errors so the agent can fix them!
            return "BUILD FAILED. Errors:\n" + errors + 
                   "\nOutput:\n" + output;
        }
        return "BUILD SUCCESS: " + output;
    } catch (Exception e) {
        return "ERROR: " + e.getMessage();
    }
}
```

The agent sees the failure, analyzes the errors, and fixes the code autonomously.

---

## 🎯 Key Takeaways

1. **AI Agent = LLM + Tools + Memory + Loop**  
   The LLM is the brain; tools are the hands; memory is the context; the loop is what makes it autonomous.


2. **The agent composes tools to achieve complex goals**  
   No single tool creates a full CAP + Fiori app. The agent orchestrates cap-js-mcp + fiori-mcp + file tools together.

3. **Our MCP server IS a tool for AI agents**  
   MCP Servers can be used by ANY MCP-compatible AI agent, not just Cline.

4. **You can build agents at any level**  
   - Simple: Cline + custom MCP server (what we did)
   - Medium: Spring AI agent with tool callbacks
   - Advanced: Multi-agent systems with LangGraph

---

## 📌 What We Demonstrated

```
┌──────────────────────────────────────────────────────────────┐
│                  TODAY'S DEMO ARCHITECTURE                    │
│                                                              │
│  User Prompt ──▶ Cline (AI Agent) ──▶ GPT-4o (SAP AI Core) │
│                       │                                      │
│          ┌────────────┼────────────┐                         │
│          ▼            ▼            ▼                         │
│   cap-js-mcp    fiori-mcp    Our MCP Server                  │
│   (CAP docs)   (Fiori UI)  (Shopping Cart)                   │
│          │            │                                      │
│          ▼            ▼                                      │
│   Complete CAP Java + Fiori App created!                     │
└──────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Try It Yourself

### Quick Demo Prompts for Cline

**Prompt 1 — CAP Java Agent Task:**
```
Create a minimal CAP Java project for managing a book catalog.
Entities: Book (ID, title, author, price, stock)
Add a Fiori list report showing all books.
Create it in ./book-catalog-demo/
```

**Prompt 2 — Multi-step Agent Task:**
```
I need a complete employee management application:
1. CAP Java backend with Employee, Department entities
2. OData V4 service
3. Fiori Elements UI with list report and object page
4. Build and verify it compiles successfully

Project location: ./employee-mgmt-demo/
```

---

## 📚 Further Reading

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Model Context Protocol Specification](https://modelcontextprotocol.io/)
- [SAP CAP Documentation](https://cap.cloud.sap/docs/)
- [SAP Fiori Elements](https://ui5.sap.com//#/topic/03265b0408e2432c9571d6b3feb6b1fd)
- [ReAct: Synergizing Reasoning and Acting in LLMs](https://arxiv.org/abs/2210.03629)
- [LangGraph for Multi-Agent Systems](https://langchain-ai.github.io/langgraph/)
- [Cline Documentation](https://docs.cline.bot/)

---

## 🎤 Speaker Notes

### Slide 1-3 (Introduction) [3 min]
- Start with the question: "Who has used GitHub Copilot or Cline to write code?"
- Ask: "Who has used it to create an **entire project** from scratch?"
- Bridge: "Today we'll see the difference between AI assistance and AI agency"

### Slide 4-6 (Agent Anatomy) [8 min]
- Draw the loop on whiteboard: Think → Act → Observe → Repeat
- Emphasize: "The LLM doesn't know about your file system. Tools give it hands."
- Show the ReAct pattern with the shopping cart as a simple example first

### Slide 7-9 (MCP) [7 min]
- Use the USB analogy: "Before USB, every device had a different connector. MCP is USB for AI tools."
- Show Cline's MCP tool panel — scroll through all the tools available
- Point to our shopping cart server in the list

### Slide 12-16 (LIVE DEMO) [15 min]
- **Do this live, don't skip it** — the agent working autonomously is the whole point
- Narrate what the agent is thinking while it works
- Point out each tool call: "See! It's calling fiori-mcp.list_functionality now"
- When it finishes, run: `cd cap-java-demo && mvn spring-boot:run`

### Closing [2 min]
- "You've seen Cline as an AI agent. Everything Cline does, you can build yourself with Spring AI."
- "The shopping cart MCP server we built — any AI agent can use it. Not just Cline."
- "AI agents are just beginning. The MCP ecosystem is growing fast."

---

*Tech Talk prepared for D-COM Innovation 2026*  
*Repository: https://github.tools.sap/d-comIN2026/custom_mcp_development*