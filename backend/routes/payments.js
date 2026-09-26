const express = require('express');
const { pool } = require('../config/db');

const router = express.Router();
const errBody = (status, error, message) => ({
  timestamp: new Date().toISOString(), status, error, message,
});

const toDTO = (r) => ({
  id: Number(r.id),
  rentalId: r.rental_id != null ? Number(r.rental_id) : null,
  amount: r.amount != null ? parseFloat(r.amount) : null,
  paymentDate: r.payment_date,
  paymentMethod: r.payment_method,
  status: r.status,
  createdAt: r.created_at,
  updatedAt: r.updated_at,
});

router.get('/', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM payments ORDER BY id');
    return res.json(r.rows.map(toDTO));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.get('/:id', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM payments WHERE id=$1', [req.params.id]);
    if (r.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Payment not found with id: ${req.params.id}`));
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.post('/', async (req, res) => {
  try {
    const b = req.body || {};
    const status = (!b.status || String(b.status).trim() === '') ? 'PENDING' : b.status;
    const r = await pool.query(
      'INSERT INTO payments (rental_id, amount, payment_date, payment_method, status) VALUES ($1,$2,$3,$4,$5) RETURNING *',
      [b.rentalId, b.amount, b.paymentDate, b.paymentMethod, status]
    );
    return res.status(201).json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.put('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM payments WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Payment not found with id: ${req.params.id}`));
    const b = req.body || {};
    const r = await pool.query(
      `UPDATE payments SET rental_id=$1, amount=$2, payment_date=$3, payment_method=$4, status=$5,
        updated_at=CURRENT_TIMESTAMP WHERE id=$6 RETURNING *`,
      [b.rentalId, b.amount, b.paymentDate, b.paymentMethod, b.status, req.params.id]
    );
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM payments WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Payment not found with id: ${req.params.id}`));
    await pool.query('DELETE FROM payments WHERE id=$1', [req.params.id]);
    return res.status(204).end();
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

module.exports = router;
