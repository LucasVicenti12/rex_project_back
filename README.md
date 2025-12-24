# 📦 Rex Project — Backend CRM - Delice

### 📌 General

This project is the backend of a CRM for the Delice enterprise. It was developed using clean architecture and following SOLID principles, for easy development and intruduction of tecnologies.

### 💫 Delice

Delice is the customer that us chosen to develop because the enterprise was needed a system to unique them customer's infomation.

### 👥 What is a CRM?

CRM - Customer relationship management is a system for unifying all customer information, which also has customer relationship history and logs, such as orders, approval records, leads and services.

---

### 🧱 Technologies used

- Kotlin
- Spring Boot
- Spring Web
- Exposed ORM
- Spring Security
- JWT
- SQL (PostgreSQL - for deploy / MySQL - for dev)
- Gradle (Kotlin DSL)
- Docker

---

### 📁 Folder Structure

```text
src
├── main
│   ├── kotlin
│   │   └── com
│   │       └── delice
│   │           └── cmr
│   │               ├── CrmApplication.kt
│   │               │
│   │               ├── core
│   │               │   ├── auth
│   │               │   │   ├── domain
│   │               │   │   |   ├── entities
│   │               │   │   |   |   └── Auth.kt
│   │               │   │   |   ├── exceptions
│   │               │   │   |   |   └── AuthException.kt
│   │               │   │   |   ├── repository
│   │               │   │   |   |   └── AuthRepository.kt (interface)
│   │               │   │   |   └── usecase
│   │               │   │   |       ├── implementation
│   │               │   │   |       |   └── AuthUseCaseImplementation.kt
│   │               │   │   |       ├── response
│   │               │   │   |       |   └── AuthResponse.kt
│   │               │   │   |       └── AuthUseCase.kt (interface)
│   │               │   │   └── infra
│   │               │   │       ├── database
│   │               │   │       |   └── AuthDatabase.kt (ORM object)
│   │               │   │       ├── repository
│   │               │   │       |   └── AuthRepositoryImplementation.kt
│   │               │   │       └── web
│   │               │   │           └── AuthWebService.kt (endpoints)
│   │               │   │
│   │               │   ├── config
│   │               │   │   ├── entities (entities of configuration)
│   │               │   │   ├── handlers (wrapped handlers for HTTP responses)
│   │               │   │   ├── service
│   │               │   │   |   ├── AuthorizationService.kt (used for provides the user to spring security context)
│   │               │   │   |   ├── SecurityFilter.kt (used once per request to recover and validate the token using the TokenService.kt)
│   │               │   │   |   └── TokenService.kt (used to validate and generate the token)
│   │               │   │   ├── web
│   │               │   │   |   └── WebPanel.kt (used for provides the routes to access the index.html)
│   │               │   │   ├── ws
│   │               │   │   |   └── WebSocketConfig.kt (used for provides web socket message broker configuration)
│   │               │   │   └── ServerConfig.kt (used to manage all project configuration access)
│   │               │   |
│   │               │   ├── mail
│   │               │   │   ├── entities
│   │               │   │   ├── queue
│   │               │   │   └── service
│   │               │   |
│   │               │   ├── notification (domain/infra)
│   │               │   |
│   │               │   ├── roles (domain/infra)
│   │               │   |
│   │               │   ├── user (domain/infra)
│   │               │   |
│   │               │   └── utils
│   │               │
│   │               ├── api (all modules that use external services from other APIs, it's also use the same structure with domain and infra)
│   │               │   
│   │               └── modules (all modules have the same struture with domain and infra)
│   │
│   └── resources
│       ├── application.properties (used to config enviroment variables and db configuration)
│       └── static
│           ├── assets (all assets from builded frontend)
│           └── index.html
│
└── test
    └── kotlin
        └── com
            └── delice
                └── crm
                    └── CrmApplicationTest.kt
