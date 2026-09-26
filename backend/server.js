const express = require('express');
const cors = require('cors');
require('dotenv').config();

const { pool, initDb } = require('./config/db');
const { authMiddleware } = require('./middleware/auth');

const authRoutes = require('./routes/auth');
const carRoutes = require('./routes/cars');
const rentalRoutes = require('./routes/rentals');
const customerRoutes = require('./routes/customers');
const paymentRoutes = require('./routes/payments');
const branchRoutes = require('./routes/branches');

const app = express();
const PORT = parseInt(process.env.PORT || '8085', 10);
const FRONTEND_ORIGIN = process.env.FRONTEND_ORIGIN || 'http://localhost:3004';

app.use(cors({
  origin: FRONTEND_ORIGIN,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['*'],
  credentials: true,
}));
app.use(express.json());

// Auth routes are public
app.use('/api/auth', authRoutes);

// Protected routes
app.use('/api/cars', authMiddleware, carRoutes);
app.use('/api/rentals', authMiddleware, rentalRoutes);
app.use('/api/customers', authMiddleware, customerRoutes);
app.use('/api/payments', authMiddleware, paymentRoutes);
app.use('/api/branches', authMiddleware, branchRoutes);

// Health check (public, handy for testing)
app.get('/api/health', (req, res) => res.json({ status: 'UP' }));

// 404 for unknown API routes
app.use('/api', (req, res) => res.status(404).json({
  timestamp: new Date().toISOString(),
  status: 404,
  error: 'Not Found',
  message: `No handler for ${req.method} ${req.originalUrl}`,
}));

// Global error handler
// eslint-disable-next-line no-unused-vars
app.use((err, req, res, next) => {
  console.error(err);
  res.status(500).json({
    timestamp: new Date().toISOString(),
    status: 500,
    error: 'Internal Server Error',
    message: err.message || 'An unexpected error occurred.',
  });
});

initDb()
  .then(() => {
    app.listen(PORT, () => {
      console.log(`CarRental Express backend running on http://localhost:${PORT}`);
    });
  })
  .catch((e) => {
    console.error('Failed to init DB:', e);
    process.exit(1);
  });

process.on('SIGINT', async () => {
  try { await pool.end(); } catch (_) {}
  process.exit(0);
});
