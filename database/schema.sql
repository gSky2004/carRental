CREATE DATABASE carrental_db;

-- \c carrental_db;

CREATE TABLE branches (

    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE customers (

    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(30) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE cars (

    id BIGSERIAL PRIMARY KEY,
    car_name VARCHAR(100) NOT NULL,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    manufacture_year INTEGER NOT NULL,
    rental_price_per_day DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Available',
    branch_id BIGINT REFERENCES branches(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE rentals (

    id BIGSERIAL PRIMARY KEY,
    car_id BIGINT NOT NULL REFERENCES cars(id),
    customer_id BIGINT REFERENCES customers(id),
    car_name VARCHAR(100),
    plate_number VARCHAR(20),
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(30) NOT NULL,
    pickup_date DATE NOT NULL,
    return_date DATE NOT NULL,
    rental_days INTEGER NOT NULL,
    price_per_day DECIMAL(10, 2) NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE payments (

    id BIGSERIAL PRIMARY KEY,
    rental_id BIGINT NOT NULL REFERENCES rentals(id),
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PAID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

INSERT INTO branches (name, location, address, phone) VALUES

    ('Main Branch', 'Dar es Salaam', '123 India Street, Dar es Salaam', '+255-712-000-111'),
    ('Arusha Branch', 'Arusha', '45 Africa Avenue, Arusha', '+255-712-000-222');

INSERT INTO customers (first_name, last_name, email, phone, license_number, address) VALUES

    ('Juma', 'Mohamed', 'juma@email.com', '+255-712-345-678', 'DL-2022-001', '12 Kinondoni, Dar es Salaam'),
    ('Aisha', 'Salim', 'aisha@email.com', '+255-713-456-789', 'DL-2023-002', '45 Mbezi, Dar es Salaam');

INSERT INTO cars (car_name, plate_number, brand, model, manufacture_year, rental_price_per_day, status, branch_id) VALUES

    ('Toyota Axio', 'TZA-101X', 'Toyota', 'Axio', 2022, 45000.00, 'Available', 1),
    ('Honda Fit', 'TZA-202Y', 'Honda', 'Fit', 2023, 35000.00, 'Available', 1),
    ('Suzuki Swift', 'TZA-303Z', 'Suzuki', 'Swift', 2024, 30000.00, 'Rented', 2);
