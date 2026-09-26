const express = require('express');
const { pool } = require('../config/db');

const router = express.Router();
const errBody = (status, error, message) => ({
  timestamp: new Date().toISOString(), status, error, message,
});

const toDTO = (r) => ({
  id: Number(r.id),
  carId: r.car_id != null ? Number(r.car_id) : null,
  customerId: r.customer_id != null ? Number(r.customer_id) : null,
  carName: r.car_name,
  plateNumber: r.plate_number,
  customerName: r.customer_name,
  customerPhone: r.customer_phone,
  pickupDate: r.pickup_date,
  returnDate: r.return_date,
  rentalDays: r.rental_days != null ? Number(r.rental_days) : null,
  pricePerDay: r.price_per_day != null ? parseFloat(r.price_per_day) : null,
  totalCost: r.total_cost != null ? parseFloat(r.total_cost) : null,
  createdAt: r.created_at,
});

router.get('/', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM rentals ORDER BY id');
    return res.json(r.rows.map(toDTO));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.get('/:id', async (req, res) => {
  try {
    const r = await pool.query('SELECT * FROM rentals WHERE id=$1', [req.params.id]);
    if (r.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Rental not found with id: ${req.params.id}`));
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

// POST /api/rentals — rentCar logic (matches RentalServiceImpl.rentCar)
router.post('/', async (req, res) => {
  const client = await pool.connect();
  try {
    const b = req.body || {};
    if (!b.carId) return res.status(400).json(errBody(400, 'Bad Request', 'carId is required.'));
    await client.query('BEGIN');
    const carRes = await client.query('SELECT * FROM cars WHERE id=$1', [b.carId]);
    if (carRes.rowCount === 0) {
      await client.query('ROLLBACK');
      return res.status(404).json(errBody(404, 'Not Found', `Car not found with id: ${b.carId}`));
    }
    const car = carRes.rows[0];
    if (car.status !== 'Available') {
      await client.query('ROLLBACK');
      return res.status(400).json(errBody(400, 'Bad Request', 'Car is not available for rent'));
    }
    await client.query('UPDATE cars SET status=$1, updated_at=CURRENT_TIMESTAMP WHERE id=$2', ['Rented', car.id]);

    const ins = await client.query(
      `INSERT INTO rentals (car_id, customer_id, car_name, plate_number, customer_name, customer_phone,
        pickup_date, return_date, rental_days, price_per_day, total_cost)
       VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11) RETURNING *`,
      [
        car.id,
        b.customerId || null,
        car.car_name,
        car.plate_number,
        b.customerName,
        b.customerPhone,
        b.pickupDate,
        b.returnDate,
        b.rentalDays,
        car.rental_price_per_day,
        b.totalCost,
      ]
    );
    const saved = ins.rows[0];
    await client.query(
      'INSERT INTO payments (rental_id, amount, payment_date, payment_method, status) VALUES ($1,$2,CURRENT_DATE,$3,$4)',
      [saved.id, b.totalCost, 'CASH', 'PENDING']
    );
    await client.query('COMMIT');
    return res.status(201).json(toDTO(saved));
  } catch (e) {
    try { await client.query('ROLLBACK'); } catch (_) {}
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  } finally {
    client.release();
  }
});

// PUT /api/rentals/:id — matches RentalServiceImpl.update (only customer/date/cost fields)
router.put('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM rentals WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Rental not found with id: ${req.params.id}`));
    const b = req.body || {};
    const r = await pool.query(
      `UPDATE rentals SET customer_id=$1, customer_name=$2, customer_phone=$3, pickup_date=$4,
        return_date=$5, rental_days=$6, total_cost=$7 WHERE id=$8 RETURNING *`,
      [b.customerId || null, b.customerName, b.customerPhone, b.pickupDate, b.returnDate, b.rentalDays, b.totalCost, req.params.id]
    );
    return res.json(toDTO(r.rows[0]));
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

router.delete('/:id', async (req, res) => {
  try {
    const cur = await pool.query('SELECT * FROM rentals WHERE id=$1', [req.params.id]);
    if (cur.rowCount === 0) return res.status(404).json(errBody(404, 'Not Found', `Rental not found with id: ${req.params.id}`));
    await pool.query('DELETE FROM payments WHERE rental_id=$1', [req.params.id]);
    await pool.query('DELETE FROM rentals WHERE id=$1', [req.params.id]);
    return res.status(204).end();
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

module.exports = router;
