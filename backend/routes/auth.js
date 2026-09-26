const express = require('express');
const crypto = require('crypto');
const { v4: uuidv4 } = require('uuid');
const { pool } = require('../config/db');
const { tokenStore, getTokenFromHeader } = require('../middleware/auth');

const router = express.Router();
const sha256 = (s) => crypto.createHash('sha256').update(s || '').digest('hex');
const errBody = (status, error, message) => ({
  timestamp: new Date().toISOString(),
  status,
  error,
  message,
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body || {};
    const r = await pool.query('SELECT * FROM admins WHERE username=$1', [username]);
    if (r.rowCount === 0) {
      return res.status(401).json(errBody(401, 'Unauthorized', 'Invalid username or password.'));
    }
    const admin = r.rows[0];
    if (admin.password !== sha256(password)) {
      return res.status(401).json(errBody(401, 'Unauthorized', 'Invalid username or password.'));
    }
    const token = uuidv4();
    tokenStore.set(token, admin.id);
    return res.json({ token, fullName: admin.full_name, username: admin.username });
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

// POST /api/auth/register
router.post('/register', async (req, res) => {
  try {
    const { username, password, fullName } = req.body || {};
    if (!username || !password) {
      return res.status(401).json(errBody(401, 'Unauthorized', 'Username and password are required.'));
    }
    const exists = await pool.query('SELECT 1 FROM admins WHERE username=$1', [username]);
    if (exists.rowCount > 0) {
      return res.status(401).json(errBody(401, 'Unauthorized', 'Username already taken.'));
    }
    const ins = await pool.query(
      'INSERT INTO admins (username, password, full_name) VALUES ($1,$2,$3) RETURNING *',
      [username, sha256(password), fullName || null]
    );
    const saved = ins.rows[0];
    const token = uuidv4();
    tokenStore.set(token, saved.id);
    return res.json({ token, fullName: saved.full_name, username: saved.username });
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

// POST /api/auth/logout
router.post('/logout', (req, res) => {
  const token = getTokenFromHeader(req);
  if (token) tokenStore.delete(token);
  return res.status(200).end();
});

// GET /api/auth/validate
router.get('/validate', async (req, res) => {
  const token = getTokenFromHeader(req);
  if (!token) return res.status(401).end();
  const adminId = tokenStore.get(token);
  if (!adminId) return res.status(401).end();
  try {
    const r = await pool.query('SELECT * FROM admins WHERE id=$1', [adminId]);
    if (r.rowCount === 0) {
      tokenStore.delete(token);
      return res.status(401).end();
    }
    const admin = r.rows[0];
    return res.json({ token, fullName: admin.full_name, username: admin.username });
  } catch (e) {
    return res.status(500).json(errBody(500, 'Internal Server Error', e.message));
  }
});

module.exports = router;
