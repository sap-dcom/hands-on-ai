# 🗒️ AI Agents — One-Page Cheat Sheet
### Quick Reference for the Tech Talk

---

## 1. What is an AI Agent?

```
AI Agent = LLM + Tools + Memory + Loop

loop:
  thought = LLM.reason(goal, context, available_tools)
  if thought.requires_action:
    result = tool.call(thought.tool_name, thought.parameters)
    context.add(result)
  else:
    return thought.final_answer
```

## 2. AI Assistant vs AI Agent

| | Assistant | Agent |
|--|--|--|
| Steps | 1 | Many |
| Tools | ❌ | ✅ |
| Memory | Short | Short + Long |
| Example | "Explain CAP" | "Create CAP app" |

## 3. MCP in 30 Seconds

```
MCP = A standard protocol so any AI can use any tool

  Cline ──MCP──▶ cap-js-mcp      ← CAP docs & model info
  Cline ──MCP──▶ fiori-mcp       ← Fiori app generation
  Cline ──MCP──▶ Our Server      ← Shopping cart / domain tools
```

## 4. Our MCP Server (Java)

```java
// 1. Annotate methods as tools
@Tool(name="addItem", description="Add item to cart")
public String addItem(String name, int quantity) { ... }

// 2. Register with Spring AI
@Bean
public ToolCallbackProvider tools(ShoppingCartService svc) {
    return MethodToolCallbackProvider.builder().toolObjects(svc).build();
}

// 3. Configure in application.yaml
// spring.ai.mcp.server.streamable-http.mcp-endpoint: /api/mcp
```

## 5. The ReAct Loop

```
Think → Act → Observe → Think → Act → Observe → ...
```

Example for "Create CAP + Fiori app":
```
Think:   "Start with CAP docs"
Act:     cap-js-mcp.search_docs(...)
Observe: "CAP Java uses cds-maven-plugin"
Think:   "Create pom.xml"
Act:     write_to_file("pom.xml", ...)
Think:   "Create schema.cds"
...
Think:   "Generate Fiori app"
Act:     fiori-mcp.execute_functionality(...)
Think:   "Lint to verify"
Act:     ui5-mcp-server.run_ui5_linter(...)
Done!
```

## 6. Key Demo Prompt

```
Create a complete SAP CAP Java application for managing Purchase Orders.
Backend: PurchaseOrder + OrderItem entities, OData V4 service.
Frontend: Fiori List Report + Object Page.
Project: ./cap-po-demo/
```

## 7. 3 Ways to Build Agents

| Approach | Best For | Key Tech |
|----------|----------|----------|
| Cline + MCP Server | Dev productivity | Spring AI + @Tool |
| Spring AI Agent | Java apps | ChatClient + ToolCallbackProvider |
| LangChain/LangGraph | Python / ML | MCP adapters |

## 8. Takeaways

1. **Agent = LLM + Tools + Loop** — the LLM decides, tools act
2. **MCP = USB for AI tools** — build once, use anywhere  
3. **Our MCP server IS an AI agent tool** — any agent can use it
4. **Cline is an AI agent** — orchestrates cap-js-mcp + fiori-mcp to build apps