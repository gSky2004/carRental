const express = require('express');
const { pool } = require('../config/db');

const router = express.Router();
const errBody = (status, error, message) => ({
  timestamp: new Date().toISOString(), status, error, message,
});

const toDTO = (r) => ({
  id: Number(r.id),
  carName: r.car_name,
  plateNumber: r.plate_number,
  brand: r.brand,
  model: r.model,
  manufactureYear: r.manufacture_year != null ? Number(r.manufacture_year) : null,
  rentalPricePerDay: r.rental_price_per_day != null ? parseFloat(r.rental_price_per_day) : null,
  status: r.status,
  branchId: r.branch_id != null ? Number(r.branch_id) : null,
  createdAt: r.created_at,
  updatedAt: r.updated_at,
});

function conflictMessage(e) {
  const msg = (e && e.constraint) || (e && e.message) || '';
  if (msg.includes('plate_number')) return 'A car with this plate number already exists.';
  return 'A record with this data already exists.';
}

router.get('/', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM cars ORDER BY id');
    return res.json(r.rows.map(toDTO));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.get('/:id', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM cars WHERE id=$1', [req.params.id]);
    if (r.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Car not found with id: ${req.params.id}`));
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.post('/', async (req, res) => {
  try {
    const b = req.body || {};
    const r = await pool.query(
      `INSERT INTO cars (car_name, plate_number, brand, model, manufacture_year, rental_price_per_day, status, branch_id)
       VALUES ($1,$2,$3,$4,$5,$6,$7,$8) RETURNING *`,
      [b.carName, b.plateNumber, b.brand, b.model, b.manufactureYear, b.rentalPricePerDay, b.status || 'Available', b.branchId || null]
    );
    return res.status(201).json(toDTO(r.rows[0]));
  } catch (e) {
    if (e.code === '23505') return res.status(409).json(errBody(409, 'Data Conflict', conflictMessage(e)));
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.put('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM cars WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Car not found with id: ${req.params.id}`));
    const b = req.body || {};
    const r = await pool.query(
      `UPDATE cars SET car_name=$1, plate_number=$2, brand=$3, model=$4, manufacture_year=$5,
        rental_price_per_day=$6, status=$7, branch_id=$8, updated_at=CURRENT_TIMESTAMP
       WHERE id=$9 RETURNING *`,
      [b.carName, b.plateNumber, b.brand, b.model, b.manufactureYear, b.rentalPricePerDay, b.status, b.branchId || null, req.params.id]
    );
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    if (e.code === '23505') return res.status(409).json(errBody(409, 'Data Conflict', conflictMessage(e)));
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM cars WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Car not found with id: ${req.params.id}`));
    if (cur.rows[0].status === 'Rented') {
      return res.status(400).json(errBody(400, 'Bad Request', 'Cannot delete: car is currently rented. Please process the return first.'));
    }
    await pool.query('UPDATE rentals SET car_id=NULL WHERE car_id=$1', [req.params.id]);
    await pool.query('DELETE FROM cars WHERE id=$1', [req.params.id]);
    return res.status(204).end();
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

module.exports = router;
