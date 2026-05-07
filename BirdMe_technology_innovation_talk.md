# BirdMe 技术与创新点一分钟讲解稿

## PPT-ready English Bullet Points

### Slide 1: What Makes BirdMe Special?

- More than a static bird information website
- Combines bird conservation, emergency help, knowledge Q&A, and community participation
- Turns passive reading into immediate action
- Built-in AI Agent for natural language interaction
- Supports three user intents:
  - In-site navigation
  - Quick rescue guidance
  - Bird knowledge Q&A

### Slide 2: Why This Approach?

- Bird rescue scenarios are often urgent and unfamiliar to ordinary users
- Traditional menus require users to search manually
- A chat-based Agent lowers the barrier for users
- Users can simply describe their problem in natural language
- The system can answer, guide, or navigate automatically
- This makes bird protection more accessible and actionable

### Slide 3: Creative Part: BirdMe AI Agent

- Built with Dify to manage Agent workflow, prompts, and knowledge base
- Uses GLM-4 as the reasoning and Q&A model
- GLM-4 provides strong natural language understanding and complex reasoning ability
- The Agent recognizes three types of intent:
  - Page navigation
  - Emergency rescue support
  - Bird-related knowledge Q&A
- Agent responses can trigger website navigation, not just text replies

### Slide 4: Knowledge Base and RAG

- Collected public and authoritative bird conservation materials
- Includes bird science, rescue guidance, and protection articles
- Uses a knowledge-base-enhanced Q&A approach
- Reduces reliance on generic model answers
- Improves reliability, relevance, and domain accuracy

### Slide 5: Technology Stack

- Vue: interactive front-end and single-page application structure
- Element UI: fast and consistent UI component development
- Backend API: user data, rescue reports, forum data, and Dify proxy calls
- Dify: Agent building, prompt management, and knowledge base integration
- GLM-4: natural language understanding, reasoning, and Q&A generation
- Front-end/back-end separation: clear structure and easier future extension

| Layer | Technology | Role |
| --- | --- | --- |
| Front-end | Vue 2, Vue Router, Vuex | SPA structure, routing, shared state |
| UI | Element UI | Consistent components and forms |
| HTTP / build | Axios, Vue CLI 4, Webpack | API client; dev server and production bundle |
| Maps & charts | Leaflet, ECharts | Map and statistics visualization |
| Back-end | Spring Boot 2.7, Java 8 | REST APIs, file upload, interceptors |
| Data access | MyBatis-Plus | ORM and SQL mapping |
| Database | SQLite (JDBC); MySQL driver available | Persistent storage for users, reports, forum, etc. |
| AI agent | Dify (+ GLM-4 as configured in Dify) | Agent workflow, prompts, knowledge base, RAG-style Q&A |
| Secure integration | Server-side Dify proxy (`DifyController`) | Keeps API keys off the browser |
| Optional LLM SDK | Alibaba DashScope Java SDK | Additional model calls from the server where needed |

### Slide 6: Technical Difficulties: Automatic Rescue Flow

- Challenge: how to turn an AI rescue intent into a real rescue report
- The Agent first identifies emergency rescue intent from the user message
- The front end captures the structured signal or navigation result returned by the Agent
- Then the rescue page opens the rescue form dialog automatically
- The form standardizes key information:
  - Location
  - Bird name / species
  - Injury type
  - Injury description
  - Image upload
- Using a form prevents missing information and improves report quality

### Slide 7: Technical / Research Highlights

- Multi-intent recognition for different user needs
- RAG-based knowledge enhancement for bird conservation content
- AI Agent connected with real website actions
- User-centered design for real rescue scenarios
- Scalable architecture for future features such as image recognition and rescue station recommendation

### Optional One-slide Summary

- BirdMe combines conservation knowledge, emergency rescue, and AI assistance
- The AI Agent recognizes navigation, rescue, and Q&A intents
- Dify + GLM-4 + knowledge base improves intelligence and reliability
- The system transforms bird protection from passive information browsing into active user support

---

## 1. What is special：我们的独特之处

BirdMe 不只是一个静态的鸟类科普网站，而是把 **鸟类保护、紧急救助、知识问答和社区参与** 结合在一起的平台。

与普通鸟类保护网站相比，我们的特色是：

- **从“浏览信息”升级为“即时行动”**：用户不仅能看鸟类知识，还能快速找到救助入口、救助站信息和上报通道。
- **内置智能 Agent**：用户可以直接用自然语言提问，系统自动判断用户意图，并给出回答或引导操作。
- **三类意图识别**：Agent 可以识别：
  1. 网站内页面跳转，例如引导用户去知识科普、救助页面等；
  2. 快速救助，例如用户发现受伤鸟类时，给出行动建议；
  3. 鸟类知识问答，例如鸟类识别、保护常识、迁徙知识等。
- **结合权威知识库**：我们整理网上公开、权威的鸟类科普与救助文章，构建知识库，让回答更贴近真实保护场景。

## 2. 为什么选择这种实现方式

我们选择“网站功能 + 智能体”的方式，是因为鸟类保护场景往往具有 **信息紧急、用户不专业、问题多样化** 的特点。

如果只做传统菜单和页面，用户需要自己查找信息；但当用户遇到受伤鸟类时，最需要的是快速、明确、低门槛的帮助。因此我们用 Agent 作为入口，让用户像聊天一样描述问题，系统再自动完成问答、导航或救助指引。

这种方式降低了用户使用门槛，也让保护鸟类从“被动阅读”变成“主动参与”。

## 3. 最有创意的部分

最有创意的是我们的 **BirdMe 智能 Agent**。

我们通过 Dify 搭建智能体，并接入 **GLM-4** 作为问答模型。GLM-4 的优势是自然语言理解能力和复杂推理能力较强，适合处理用户用自然语言描述的多样化问题。

同时，我们不是只让大模型自由回答，而是结合了鸟类保护领域的知识库。知识库来源于公开权威的鸟类科普、救助和保护文章，使 Agent 的回答更可靠、更有针对性。

另外，我们还让 Agent 的回复可以触发网站内导航。也就是说，它不仅能“回答问题”，还可以“帮助用户完成操作”。

## 4. 运用了哪些技术及选择原因

- **前端：Vue + Element UI**  
  用于构建交互式网页界面，开发效率高，适合快速实现多页面功能、表单、管理后台和聊天组件。

- **后端：后端 API 服务**  
  用于处理用户数据、救助信息、论坛内容、统计数据，并作为 Dify 调用的代理层，避免前端直接暴露敏感接口。

- **智能体平台：Dify**  
  用于快速搭建 Agent、管理 Prompt、连接大模型和知识库，减少从零开发 AI 工作流的成本。

- **大模型：GLM-4**  
  用于自然语言理解、鸟类知识问答和救助建议生成，适合复杂意图理解和多场景问答。

- **知识库/RAG 思路**  
  通过整理权威鸟类科普与救助资料，让模型基于领域资料回答，提升准确性和可信度。

- **前后端分离架构**  
  前端负责用户体验，后端负责数据和接口，AI 服务独立接入，结构清晰，便于扩展。

### 技术栈总览（表格）

| 层次 | 技术 | 版本/说明（以仓库为准） | 用途 |
| --- | --- | --- | --- |
| 前端框架 | Vue | 2.6.x | 组件化界面与单页应用 |
| 路由与状态 | Vue Router、Vuex | 3.4.x / 3.6.x | 页面导航与全局状态 |
| UI 组件库 | Element UI | 2.15.x | 表单、布局、弹窗等统一交互 |
| 网络与工程化 | Axios、Vue CLI | 0.21.x / 4.4.x | HTTP 请求；本地开发与生产构建 |
| 地图与可视化 | Leaflet、ECharts | 1.9.x、4.9.x | 地图展示与统计图表 |
| 后端框架 | Spring Boot | 2.7.17 | REST 接口、文件上传、拦截器（如登录） |
| 运行环境 | Java | 8 | 与 Spring Boot 2.7 配套 |
| 持久层 | MyBatis-Plus | 3.4.3.x | 实体映射与 CRUD |
| 数据库 | SQLite（当前配置） | JDBC 驱动 3.42.x | 本地/轻量部署下的数据存储；`pom` 中含 MySQL 驱动便于切换 |
| JSON 等工具 | Fastjson、Gson | 按 `pom` 版本 | 序列化与工具类场景 |
| 智能体与模型 | Dify、GLM-4（在 Dify 中配置） | — | Agent 编排、Prompt、知识库与对话；GLM-4 负责理解与生成 |
| 安全调用 AI | 后端 Dify 代理接口 | — | API Key 仅保存在服务端，前端不直连 Dify |
| 可选大模型 SDK | 阿里云 DashScope Java SDK | 2.9.x | 服务端按需调用通义等模型（与 Dify 链路互补） |

### 选型原因（与上表对应）

| 技术 | 选择原因（一句话） |
| --- | --- |
| Vue + Element UI | 成熟、文档全，适合快速做出管理端与业务页 |
| Spring Boot + MyBatis-Plus | 生态成熟，接口与数据层分离清晰，易维护 |
| SQLite / 可换 MySQL | 开发与小规模部署简单；生产可按需换库 |
| Dify + GLM-4 | 低代码搭建 Agent 与知识库，专注业务而非自研编排 |
| 后端代理 Dify | 降低密钥泄露风险，便于统一日志与错误处理 |

## 5. 技术或研究方法上的亮点

- **多意图识别设计**：将用户输入分为页面导航、快速救助、知识问答三类，提高系统响应效率。
- **自动救助流程设计**：Agent 识别到救助意图后，前端捕获返回结果或导航信号，引导用户进入救助流程，并通过弹窗表单收集完整信息。
- **RAG 知识增强**：不是单纯依赖大模型，而是用鸟类保护资料构建知识库，增强专业性。
- **AI 与网站操作结合**：Agent 可以从聊天窗口直接引导页面跳转，把问答转化为实际操作。
- **面向真实场景设计**：围绕“发现受伤鸟类怎么办”这一真实问题，设计快速救助和科普流程。
- **可扩展性强**：未来可以继续扩展鸟类图片识别、救助站推荐、用户上报数据分析等功能。

## 6. Technical Difficulties：如何实现 Agent 自动救助

### English Answer

The main technical difficulty is how to transform the Agent's rescue intent recognition into a real rescue workflow on the website.

After the user says something like "I found an injured bird", the Agent identifies it as a rescue intent and returns a structured response or navigation signal. The front end captures this result from the Agent response. If the result indicates a rescue scenario, the website guides the user to the rescue page and opens the rescue request dialog.

On the rescue page, the dialog is controlled by a visible state variable. When the rescue flow starts, the front end fills in available location information, sets the dialog to visible, and shows a form for the user to complete. The form collects key fields such as location, bird name, species, injury type, injury description, and image upload.

We use a form because rescue reports need complete and standardized information. If users only describe the case in free text, important details may be missing. The form structure helps prevent omissions, improves data quality, and makes it easier for rescue stations or administrators to process the report.

### 中文答案

技术难点主要在于：如何把 Agent 识别到的“救助意图”转化成网站里真正可执行的救助流程。

当用户输入类似 “I found an injured bird” 的内容后，Agent 会先判断这是一个救助场景，并返回结构化结果或导航信号。前端会捕获 Agent 返回的结果，如果判断为救助意图，就引导用户进入救助页面，并触发救助表单弹窗。

在救助页面中，表单弹窗由一个可见状态变量控制。救助流程开始时，前端会把已知的地点、经纬度等信息填入表单，然后将弹窗状态设置为可见，展示救助申请表。用户需要填写鸟类名称、物种、受伤类型、受伤描述，并可以上传图片。

我们使用表单而不是完全依赖自由文本，是为了规范用户输入。救助场景对信息完整性要求比较高，如果只让用户随便描述，可能会遗漏地点、伤情类型、图片等关键信息。表单可以减少信息缺失，提高上报质量，也方便救助站或管理员后续处理。

## 一分钟口播版本

我们的 BirdMe 和普通鸟类保护网站最大的区别是，它不只是展示科普信息，而是把知识问答、快速救助、页面导航和社区参与整合到一个平台里。我们最有创意的部分是内置了一个基于 Dify 搭建的智能 Agent，并引用 GLM-4 作为问答模型。这个 Agent 可以识别三种用户意图：第一是网站内页面跳转，第二是遇到受伤鸟类时的快速救助，第三是鸟类知识问答。

我们选择这种实现方式，是因为真实的鸟类救助场景通常比较紧急，普通用户也不一定知道该去哪个页面找信息。通过聊天式 Agent，用户只需要描述问题，系统就能给出建议或直接引导到对应页面，降低了使用门槛。

技术上，我们使用 Vue 和 Element UI 构建前端界面，工程侧配合 Vue Router、Vuex 与 Axios；后端采用 Spring Boot、MyBatis-Plus，当前以 SQLite 持久化业务数据；Dify 搭建智能体与知识库，GLM-4 负责自然语言理解、推理与问答，并通过后端接口代理调用以保护密钥。我们还整理了公开权威的鸟类科普和救助文章，构建知识库，让回答更可靠。整体亮点是把 RAG 知识增强、多意图识别和网站操作结合起来，让鸟类保护从被动阅读变成即时、可行动的帮助。
