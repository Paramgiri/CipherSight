# CipherSight

## Cybersecurity Awareness Learning Desktop Application

CipherSight is a desktop application developed in Java to help users understand important cybersecurity concepts through interactive demonstrations and educational modules. The project is created **100% for educational and awareness purposes** and is not designed to attack, hack, or exploit any computer systems or websites.

The idea behind CipherSight came from combining two different project concepts—an **Encryption Visualizer** and a **Hidden Text Detector**. Since both projects shared the common objective of spreading cybersecurity awareness, they were merged into a single application called **CipherSight**. This approach provides users with multiple cybersecurity learning modules in one place through a simple and interactive interface.

---

# Features

The application contains a Dashboard and five educational cybersecurity modules.

## Dashboard

The dashboard serves as the home screen of the application. It provides an overview of the project, explains its purpose, and allows users to navigate to different cybersecurity modules.

## Module 1 – Caesar Cipher (Shift Encryption)

This module demonstrates the Caesar Cipher, one of the oldest and simplest encryption techniques. Users can understand how characters are shifted using a selected key and observe the encryption and decryption process.

## Module 2 – SHA-256 Hash Generator

This module demonstrates the SHA-256 hashing algorithm. Users can generate secure one-way hashes and understand how hashing is used for data integrity, password storage, and digital security.

## Module 3 – Avalanche Effect Demonstration

This module explains the Avalanche Effect, where even a very small change in the input produces a completely different cryptographic hash. It helps users understand why modern cryptographic algorithms are highly secure.

## Module 4 – Hidden Character Finder

This module detects hidden or invisible characters that may exist inside copied text. It helps users understand how hidden characters can sometimes cause formatting issues or be misused.

## Module 5 – Homograph Attack Checker

This module demonstrates homograph attacks by checking webpage names that appear visually similar to legitimate websites. For example, a fake domain may look almost identical to "facebook.com" while actually being a different domain. The purpose of this module is to spread awareness about phishing and deceptive website names.

## About the Developer

The application also includes a dedicated section containing the developer's basic information.

---

# Technologies Used

* Java 21
* Java Swing (Graphical User Interface)
* Java Security (`java.security.MessageDigest`) for SHA-256 hashing
* Object-Oriented Programming (OOP)
* Single-file Java application (`CipherSight.java`)

---

# Build Information

* Programming Language: Java 21
* User Interface: Java Swing
* Project Type: Desktop Application
* Source File: `CipherSight.java`
* Executable File: `CipherSight.jar`
* Platform: Cross-platform (Windows, Linux, and macOS with Java installed)

---

# How to Run

### Option 1 – Run the JAR File

1. Make sure Java 21 (or a compatible Java version) is installed.
2. Download the `CipherSight.jar` file.
3. Run the application using:

```bash
java -jar CipherSight.jar
```

### Option 2 – Run the Source Code

1. Open the project in your preferred Java IDE (such as IntelliJ IDEA).
2. Open `CipherSight.java`.
3. Compile and run the program.

---

# Project Structure

```
CipherSight
│
├── Dashboard
│
├── Modules
│   ├── Caesar Cipher
│   ├── SHA-256 Hash Generator
│   ├── Avalanche Effect
│   ├── Hidden Character Finder
│   └── Homograph Attack Checker
│
└── About the Developer
```

---

# Developer

**Name:** Paramgiri Goswami

**Course:** Diploma in Computer Engineering

**Year:** Third Year

---

# Educational Purpose

This project has been developed as part of an GTU 2 week internship program for educational purposes. Its objective is to improve cybersecurity awareness by demonstrating fundamental cybersecurity concepts through interactive examples. It is not intended for offensive security activities or unauthorized access to any systems.
