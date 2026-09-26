# Car Rental Management System

A full-stack web application for managing car rental operations built with Spring Boot 3.2.5 and React.

## Features

### Authentication & Authorization
- Admin login/logout with token-based authentication
- Session persists across page reloads (localStorage)
- Protected API endpoints (401 without valid token)
- Registration for new admin accounts

### Car Management
- **CRUD operations** — Add, edit, view, and delete cars
- **Status tracking** — Available, Rented, Under Maintenance
- **Search** — Filter by car name, brand, or plate number
- **Stats dashboard** — Total cars, available for rent, currently rented counts

### Rental Management
- **Rent a car** — Select a car, choose customer, set pickup/return dates
- **Auto-calculated costs** — Daily rate × number of days
- **Receipt generation** — Confirmation with receipt number, downloadable as .txt file
- **Rental history** — View all past and current rentals with full details

### Customer Management
- **CRUD operations** — Add, edit, view, and delete customer records
- **Fields** — Name, email, phone, license number, address

### Payment Tracking
- **Auto-created on rental** — Each rental generates a PENDING payment
- **Status workflow** — Admin can update: PENDING → COMPLETED or FAILED
- **Full CRUD** — Manual payment entry and management

### Branch Management
- **CRUD operations** — Add, edit, view, and delete branches

### User Interface
- **Modern responsive design** — Clean UI with color-coded status badges
- **URL routing** — `/cars`, `/rentals`, `/customers`, `/payments`, `/branches`
- **Search & filter** — Find cars quickly
- **Error handling** — User-friendly error messages

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2.5, Spring Data JPA, Hibernate |
| Frontend | React 18, React Router 6 |
| Database | PostgreSQL |
| Build | Maven (backend), npm (frontend) |
| Auth | Custom token (in-memory store) |

## Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+

## Setup

### 1. Database

```sql
CREATE DATABASE carrental_db;
```

### 2. Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/carrental_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

### 3. Start Backend

```bash
cd backend
mvn spring-boot:run
```

Server starts on `http://localhost:8085`.

### 4. Start Frontend

```bash
cd frontend
npm install
npm start
```

App opens at `http://localhost:3004`.

## Default Login

- **Username:** `admin`
- **Password:** `admin123`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Admin login |
| POST | `/api/auth/register` | Register admin |
| GET | `/api/auth/validate` | Validate token |
| POST | `/api/auth/logout` | Logout |
| GET/POST | `/api/cars` | List / Create cars |
| GET/PUT/DELETE | `/api/cars/{id}` | Get / Update / Delete car |
| GET/POST | `/api/rentals` | List / Create rental |
| GET/PUT/DELETE | `/api/rentals/{id}` | Get / Update / Delete rental |
| GET/POST | `/api/customers` | List / Create customer |
| GET/PUT/DELETE | `/api/customers/{id}` | Get / Update / Delete customer |
| GET/POST | `/api/payments` | List / Create payment |
| GET/PUT/DELETE | `/api/payments/{id}` | Get / Update / Delete payment |
| GET/POST | `/api/branches` | List / Create branch |
| GET/PUT/DELETE | `/api/branches/{id}` | Get / Update / Delete branch |

All endpoints except `/api/auth/**` require `Authorization: Bearer <token>` header.

## Project Structure

```
backend/
├── src/main/java/com/oop/carrental/
│   ├── config/       # CORS, Auth interceptor
│   ├── controller/   # REST controllers
│   ├── dto/          # Data transfer objects
│   ├── entity/       # JPA entities
│   ├── exception/    # Error handling
│   ├── repository/   # Data access
│   └── service/      # Business logic
└── src/main/resources/
    └── application.properties

frontend/
├── src/
│   ├── components/   # React components
│   ├── App.js       # Main app with routing
│   ├── api.js       # API client
│   └── index.js     # Entry point
└── package.json
```

## Screenshots

*(Add screenshots here for your presentation)*
