const express = require('express');
const { pool } = require('../config/db');

const router = express.Router();
const errBody = (status, error, message) => ({
  timestamp: new Date().toISOString(), status, error, message,
});

const toDTO = (r) => ({
  id: Number(r.id),
  firstName: r.first_name,
  lastName: r.last_name,
  email: r.email,
  phone: r.phone,
  licenseNumber: r.license_number,
  address: r.address,
  createdAt: r.created_at,
  updatedAt: r.updated_at,
});

router.get('/', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM customers ORDER BY id');
    return res.json(r.rows.map(toDTO));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.get('/:id', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM customers WHERE id=$1', [req.params.id]);
    if (r.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Customer not found with id: ${req.params.id}`));
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.post('/', async (req, res) => {
  try {
    const b = req.body || {};
    const r = await pool.query(
      `INSERT INTO customers (first_name, last_name, email, phone, license_number, address)
       VALUES ($1,$2,$3,$4,$5,$6) RETURNING *`,
      [b.firstName, b.lastName, b.email || null, b.phone, b.licenseNumber, b.address || null]
    );
    return res.status(201).json(toDTO(r.rows[0]));
  } catch (e) {
    if (e.code === '23505') return res.status(409).json(errBody(409, 'Data Conflict', 'A customer with this license number already exists.'));
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.put('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM customers WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Customer not found with id: ${req.params.id}`));
    const b = req.body || {};
    const r = await pool.query(
      `UPDATE customers SET first_name=$1, last_name=$2, email=$3, phone=$4, license_number=$5, address=$6,
        updated_at=CURRENT_TIMESTAMP WHERE id=$7 RETURNING *`,
      [b.firstName, b.lastName, b.email || null, b.phone, b.licenseNumber, b.address || null, req.params.id]
    );
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    if (e.code === '23505') return res.status(409).json(errBody(409, 'Data Conflict', 'A customer with this license number already exists.'));
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM customers WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Customer not found with id: ${req.params.id}`));
    await pool.query('DELETE FROM customers WHERE id=$1', [req.params.id]);
    return res.status(204).end();
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

module.exports = router;
