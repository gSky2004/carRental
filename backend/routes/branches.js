const express = require('express');
const { pool } = require('../config/db');

const router = express.Router();
const errBody = (status, error, message) => ({
  timestamp: new Date().toISOString(), status, error, message,
});

const toDTO = (r) => ({
  id: Number(r.id),
  name: r.name,
  location: r.location,
  address: r.address,
  phone: r.phone,
  createdAt: r.created_at,
  updatedAt: r.updated_at,
});

router.get('/', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM branches ORDER BY id');
    return res.json(r.rows.map(toDTO));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.get('/:id', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM branches WHERE id=$1', [req.params.id]);
    if (r.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Branch not found with id: ${req.params.id}`));
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.post('/', async (req, res) => {
  try {
    const b = req.body || {};
    const r = await pool.query(
      'INSERT INTO branches (name, location, address, phone) VALUES ($1,$2,$3,$4) RETURNING *',
      [b.name, b.location, b.address || null, b.phone || null]
    );
    return res.status(201).json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.put('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM branches WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Branch not found with id: ${req.params.id}`));
    const b = req.body || {};
    const r = await pool.query(
      'UPDATE branches SET name=$1, location=$2, address=$3, phone=$4, updated_at=CURRENT_TIMESTAMP WHERE id=$5 RETURNING *',
      [b.name, b.location, b.address || null, b.phone || null, req.params.id]
    );
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM branches WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Branch not found with id: ${req.params.id}`));
    await pool.query('DELETE FROM branches WHERE id=$1', [req.params.id]);
    return res.status(204).end();
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

module.exports = router;
