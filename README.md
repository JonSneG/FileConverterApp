# 📄 FileConverterApp - Universal Document and PDF Utility (Java Edition)

## 🌟 Overview

The **FileConverterApp** is a robust, **Java-based** application designed for versatile document conversion and comprehensive PDF management. It supports converting files between popular text formats and PDF, as well as providing essential PDF manipulation tools like splitting, merging, and security features.

## ✨ Key Features

This application offers a dual set of functionalities: **Universal Conversion** and **PDF Tools**.

### 1. Universal Conversion Engine
* **Broad Format Support:** Convert between standard text and document formats and the PDF format.
    * `TXT` ↔ `PDF`
    * `DOC` / `DOCX` ↔ `PDF`
    * `RTF` ↔ `PDF`
    * `HTML` ↔ `PDF`
* **Bi-Directional Conversion:** Easily convert files *to* PDF and *from* PDF back to the source formats.
* **Batch Processing:** Efficiently convert multiple files or an entire directory of documents with a single command.

### 2. Advanced PDF Tools
* **Split PDF:** Divide a large PDF document into smaller, separate files (e.g., split by page range).
* **Merge PDFs:** Combine multiple PDF files into one single document.
* **Security:** Add password protection to existing PDF files to restrict access or modification.

## 🚀 Installation

To get the FileConverterApp running on your machine, you will need a Java Runtime Environment (JRE) installed.

### Prerequisites
* **Java Runtime Environment (JRE)** 8 or newer.
* The application may rely on external Java libraries (e.g., Apache PDFBox, iText, etc.). These dependencies are typically handled by your build system (Maven or Gradle).

### Step 1: Clone the Repository
```bash
git clone [https://github.com/JonSneG/FileConverterApp.git](https://github.com/JonSneG/FileConverterApp.git)
cd FileConverterApp