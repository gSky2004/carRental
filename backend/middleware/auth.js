// In-memory token store: token -> adminId (matches old Spring AuthServiceImpl)
const tokenStore = new Map();

function getTokenFromHeader(req) {
  const h = req.headers['authorization'] || req.headers['Authorization'];
  if (h && h.startsWith('Bearer ')) return h.substring(7);
  return null;
}

function unauthorized(res) {
  return res.status(401).json({
    timestamp: new Date().toISOString(),
    status: 401,
    error: 'Unauthorized',
    message: 'Please log in to continue.',
  });
}

function authMiddleware(req, res, next) {
  if (req.method === 'OPTIONS') return next();
  if (req.path.startsWith('/api/auth/') || req.originalUrl.startsWith('/api/auth/')) return next();
  const token = getTokenFromHeader(req);
  if (token && tokenStore.has(token)) return next();
  return unauthorized(res);
}

module.exports = { tokenStore, getTokenFromHeader, unauthorized, authMiddleware };
