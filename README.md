# Car Rental Management System

A full-stack web application for managing car rental operations built with the **PERN stack (PostgreSQL, Express, React, Node.js)**.

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
- **Fields** — First name, last name, email, phone, license number, address

### Payment Tracking
- **Auto-created on rental** — Each rental generates a PENDING payment
- **Status workflow** — Admin can update: PENDING → PAID or FAILED
- **Full CRUD** — Manual payment entry and management

### Branch Management
- **CRUD operations** — Add, edit, view, and delete branches

### User Interface
- **Modern responsive design** — Clean UI with color-coded status badges
- **URL routing** — `/cars`, `/rentals`, `/customers`, `/payments`, `/branches`
- **Search & filter** — Find cars quickly
- **Error handling** — User-friendly error messages

## Tech Stack (PERN)

| Layer | Technology |
|-------|-----------|
| Database | PostgreSQL 14+ (`pg` driver v8) |
| Backend | Node.js 18+, Express 4, `cors`, `dotenv`, `uuid` |
| Frontend | React 18, React Router 7, Axios, react-scripts 5 |
| Auth | Custom token (in-memory store in `backend/middleware/auth.js`) |
| Build | npm (backend + frontend) |

> Note: legacy Java / Spring Boot code still exists under `backend/src/` + `backend/pom.xml` for reference only. It is **not used**. The active backend is `backend/server.js` (Express).

## Prerequisites

- Node.js 18+
- PostgreSQL 14+
- npm

No Java / Maven required for the current stack.

## Setup

### 1. Database

The backend auto-creates the database and tables on startup via `backend/config/db.js` (`initDb()`), including seed data (admin + branches + customers + cars).

Just make sure PostgreSQL is running and `backend/.env` credentials are correct:

```env
PORT=8085
DB_HOST=localhost
DB_PORT=5432
DB_NAME=carrental_db
DB_USER=postgres
DB_PASSWORD=YOUR_PASSWORD
FRONTEND_ORIGIN=http://localhost:3004
```

For a manual setup, you can also run:

```bash
psql -U postgres -f database/schema.sql
```

### 2. Start Backend (Express)

```bash
cd backend
npm install
npm start
# or for watch mode:
# npm run dev
```

Server starts on `http://localhost:8085`.
Health check: `GET http://localhost:8085/api/health` → `{ "status": "UP" }`.

### 3. Start Frontend (React)

```bash
cd frontend
npm install
npm start
```

App opens at `http://localhost:3004` (see `frontend/.env`).

To build for production:

```bash
npm run build
```

## Default Login

- **Username:** `admin`
- **Password:** `admin123`

Seeded automatically in `backend/config/db.js` on first run.

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

All endpoints except `/api/auth/**` and `/api/health` require `Authorization: Bearer <token>` header.

## Project Structure

```
backend/
├── server.js           # Express entry point
├── config/
│   └── db.js           # pg Pool + initDb (auto-create tables + seed)
├── middleware/
│   └── auth.js         # Token auth middleware (in-memory store)
├── routes/             # auth, cars, rentals, customers, payments, branches
├── .env                # PORT, DB_*, FRONTEND_ORIGIN
└── package.json        # start / dev scripts

frontend/
├── build/              # Production build output
├── .env                # PORT=3004
└── package.json        # React 18 + react-router-dom + axios + react-scripts

database/
└── schema.sql          # Manual SQL schema + seed (optional, auto-run otherwise)
```
