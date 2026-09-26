const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  host: process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.DB_PORT || '5432', 10),
  database: process.env.DB_NAME || 'carrental_db',
  user: process.env.DB_USER || 'postgres',
  password: process.env.DB_PASSWORD || 'kali',
});

pool.on('error', (err) => {
  console.error('Postgres pool error:', err.message);
});

async function initDb() {
  // Ensure database exists (connect to postgres db for creation check)
  const { Client } = require('pg');
  const adminClient = new Client({
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT || '5432', 10),
    database: 'postgres',
    user: process.env.DB_USER || 'postgres',
    password: process.env.DB_PASSWORD || 'kali',
  });
  try {
    await adminClient.connect();
    const dbName = process.env.DB_NAME || 'carrental_db';
    const exists = await adminClient.query('SELECT 1 FROM pg_database WHERE datname=$1', [dbName]);
    if (exists.rowCount === 0) {
      // Quote identifier safely (db name is trusted from .env, allow alnum + underscore)
      await adminClient.query(`CREATE DATABASE "${dbName.replace(/"/g, '')}"`);
      console.log(`Database ${dbName} created.`);
    }
  } catch (e) {
    console.error('DB creation check failed:', e.message);
  } finally {
    try { await adminClient.end(); } catch (_) {}
  }

  const crypto = require('crypto');
  const sha256 = (s) => crypto.createHash('sha256').update(s).digest('hex');

  await pool.query(`
    CREATE TABLE IF NOT EXISTS admins (
      id SERIAL PRIMARY KEY,
      username VARCHAR(100) NOT NULL UNIQUE,
      password VARCHAR(255) NOT NULL,
      full_name VARCHAR(255),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS branches (
      id SERIAL PRIMARY KEY,
      name VARCHAR(100) NOT NULL,
      location VARCHAR(100) NOT NULL,
      address VARCHAR(255),
      phone VARCHAR(30),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS customers (
      id SERIAL PRIMARY KEY,
      first_name VARCHAR(50) NOT NULL,
      last_name VARCHAR(50) NOT NULL,
      email VARCHAR(100),
      phone VARCHAR(30) NOT NULL,
      license_number VARCHAR(50) NOT NULL UNIQUE,
      address VARCHAR(255),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS cars (
      id SERIAL PRIMARY KEY,
      car_name VARCHAR(100) NOT NULL,
      plate_number VARCHAR(20) NOT NULL UNIQUE,
      brand VARCHAR(50) NOT NULL,
      model VARCHAR(50) NOT NULL,
      manufacture_year INTEGER NOT NULL,
      rental_price_per_day DECIMAL(10,2) NOT NULL,
      status VARCHAR(20) NOT NULL DEFAULT 'Available',
      branch_id BIGINT REFERENCES branches(id),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS rentals (
      id SERIAL PRIMARY KEY,
      car_id BIGINT REFERENCES cars(id),
      customer_id BIGINT REFERENCES customers(id),
      car_name VARCHAR(100),
      plate_number VARCHAR(20),
      customer_name VARCHAR(100) NOT NULL,
      customer_phone VARCHAR(30) NOT NULL,
      pickup_date DATE NOT NULL,
      return_date DATE NOT NULL,
      rental_days INTEGER NOT NULL,
      price_per_day DECIMAL(10,2) NOT NULL,
      total_cost DECIMAL(10,2) NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS payments (
      id SERIAL PRIMARY KEY,
      rental_id BIGINT NOT NULL REFERENCES rentals(id) ON DELETE CASCADE,
      amount DECIMAL(10,2) NOT NULL,
      payment_date DATE NOT NULL,
      payment_method VARCHAR(20) NOT NULL,
      status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `);

  // Seed admin
  const adminCount = await pool.query('SELECT COUNT(*)::int AS c FROM admins');
  if (adminCount.rows[0].c === 0) {
    await pool.query('INSERT INTO admins (username, password, full_name) VALUES ($1,$2,$3)', [
      'admin',
      sha256('admin123'),
      'System Administrator',
    ]);
    console.log('Seeded admin / admin123');
  }

  // Seed branches
  const branchCount = await pool.query('SELECT COUNT(*)::int AS c FROM branches');
  if (branchCount.rows[0].c === 0) {
    await pool.query(
      `INSERT INTO branches (name, location, address, phone) VALUES
       ('Main Branch','Dar es Salaam','123 India Street, Dar es Salaam','+255-712-000-111'),
       ('Arusha Branch','Arusha','45 Africa Avenue, Arusha','+255-712-000-222')`
    );
  }

  // Seed customers
  const custCount = await pool.query('SELECT COUNT(*)::int AS c FROM customers');
  if (custCount.rows[0].c === 0) {
    await pool.query(
      `INSERT INTO customers (first_name,last_name,email,phone,license_number,address) VALUES
       ('Juma','Mohamed','juma@email.com','+255-712-345-678','DL-2022-001','12 Kinondoni, Dar es Salaam'),
       ('Aisha','Salim','aisha@email.com','+255-713-456-789','DL-2023-002','45 Mbezi, Dar es Salaam')`
    );
  }

  // Seed cars
  const carCount = await pool.query('SELECT COUNT(*)::int AS c FROM cars');
  if (carCount.rows[0].c === 0) {
    await pool.query(
      `INSERT INTO cars (car_name,plate_number,brand,model,manufacture_year,rental_price_per_day,status,branch_id) VALUES
       ('Toyota Axio','TZA-101X','Toyota','Axio',2022,45000.00,'Available',1),
       ('Honda Fit','TZA-202Y','Honda','Fit',2023,35000.00,'Available',1),
       ('Suzuki Swift','TZA-303Z','Suzuki','Swift',2024,30000.00,'Rented',2)`
    );
  }
}

module.exports = { pool, initDb };
