# 💸 BankGPT – Conversational Banking with Generative AI

![Screenshot 2025-06-12 at 6 11 17 PM](https://github.com/user-attachments/assets/39b94241-2ef5-4ada-ac54-757d43a18c14)
![Screenshot 2025-06-12 at 6 11 05 PM](https://github.com/user-attachments/assets/c48226d2-9096-4b9a-8d9c-84c7ea203e0e)

**BankGPT** is an applied research prototype integrating **OpenAI’s GPT API** and local **RAG (Retrieval-Augmented Generation)** to create a next-gen AI assistant for banking tasks. Built as part of a Master’s Applied Project, the app delivers a conversational, smart, and modular experience for tasks like transfers, Zelle, and bill payments—entirely via natural language.

---

## 🧠 What Can BankGPT Do?

BankGPT allows users to:
- 💬 Chat naturally to perform tasks like **Transfer**, **Zelle**, **Wire**, and **Bill Pay**
- 📩 Receive structured JSON responses from GPT to power UI components
- 🔍 Ask questions like "What are Zelle limits?" with answers pulled from bank policy PDFs via **RAG**
- 🧭 Get redirected when asking out-of-scope questions

---

## ✨ Features

- 🔄 **Two Processing Modes**: 
  - **System Prompts** for structured workflows (Transfer, Zelle, etc.)
  - **RAG-based Responses** for policy lookup via PDFs

- 🧾 **Structured JSON Outputs**: 
  - Each GPT reply includes `next`, `message`, and required fields for the next UI element

- 📱 **Composable UI**:
  - Built using **Jetpack Compose**, dynamically renders input fields like amount, recipient, date, etc.

- 🧮 **Fallback Logic**:
  - If a user asks an unsupported question (e.g., "Tell me a joke"), BankGPT gently redirects them to supported flows.

---

## 🧩 Architecture Overview

**User Input → ViewModel → (GPT API or Local RAG Server) → JSON → Dynamic UI via Compose → (Mock API Trigger)**

This architecture allows modular growth. Adding a new action (e.g., card control) only requires updating the prompt logic and UI bindings.

---

## 🛠️ Technology Stack

| Layer          | Tool / Stack                   |
|----------------|--------------------------------|
| LLM API        | OpenAI GPT-4                   |
| Retrieval      | LangChain + FAISS Vector DB    |
| Android        | Kotlin, Jetpack Compose        |
| DI             | Hilt                           |
| Architecture   | MVVM                           |
| Server         | FastAPI + Uvicorn (for RAG)    |

---

## 📊 Evaluation Snapshot

| Metric                              | Transfer Flow | Zelle Flow |
|-------------------------------------|---------------|-------------|
| Intent Detection Accuracy           | 100%          | 90%         |
| Avg. Time to First Response         | 1.2s          | 1.1s        |
| Avg. Completion Time                | 8.4s          | 9.4s        |
| Success Rate                        | 100%          | 90%         |
| Mid-flow Edits Supported            | ✅             | ✅           |
| RAG Response Accuracy (manual check)| ✅ Relevant    | ✅ Relevant  |
| Fallback for Unsupported Queries    | ✅             | ✅           |

---

## 🆚 Industry Comparison: Bank of America (Erica)

Screenshots included in the repo show that:
- Erica only provides **static Zelle/wire options**
- No intelligent flow or context-aware conversation
- Users are redirected out of chat into full screens

✅ In contrast, **BankGPT** offers:
- Intent detection
- Step-by-step interaction
- Structured JSON to UI mapping
- Inline confirmations without leaving chat

---

## 🔮 Future Scope

- ✅ Real API integration
- 🔐 Security, encryption, and PII handling
- 💼 Expand actions to include card controls, investments, fraud alerts
- 📚 RAG with multi-PDF support (T&C, fee policies)
- 🧑‍🦯 Accessibility improvements (voice input/output, multi-language)
- 📊 User testing for metrics like trust and flow completion

---

## 🚀 Getting Started

1. Clone the repository:

    ```bash
    git clone https://github.com/sahilsood/BankGPT.git
    ```

2. Open the project in **Android Studio**.

3. Set up your `.env` or use your OpenAI API key directly.

4. To run the **RAG server** locally:

    ```bash
    cd rag-server
    source venv/bin/activate
    uvicorn rag_server:app --reload --port 8003
    ```

5. Run the app on an emulator or physical Android device.

---

## 🧑‍🎓 Academic Note

This project was developed as a part of the **ISEM 699: Applied Project** at Harrisburg University. It aims to demonstrate practical application of LLMs in enhancing digital banking experiences.

---

## 🙌 Acknowledgements

- [OpenAI](https://openai.com/)
- [LangChain](https://www.langchain.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- Harrisburg University – MS ISEM Program

---

💬 Feel free to star ⭐ the repo or reach out if you're working on similar applied AI projects!
