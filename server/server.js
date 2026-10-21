const express = require('express');
const cors = require('cors');
const { Pool } = require('pg'); // postgres sql database

const pool = new Pool({
  user: 'postgres',
  host: 'localhost',
  database: 'smart_pantry',
  password: '12345',  
  port: 5432,
});

const app = express();
app.use(cors());
app.use(express.json());

const PANTRY_SELECT =
  "SELECT id, name, quantity, unit, to_char(expiry_date, 'YYYY-MM-DD') AS expiry_date FROM pantry_items";


function mapPantry(r) {
  return { id: r.id, name: r.name, quantity: Number(r.quantity),
           unit: r.unit, expiryDate: r.expiry_date }; // Maps database fornmat to API format
}

// READ (GET)
// Gets all pantry items
// Endpoint: GET /api/pantry
app.get('/api/pantry', async (req, res) => {
  try {
    const result = await pool.query(`${PANTRY_SELECT} ORDER BY name`);
    res.json(result.rows.map(mapPantry));
  } catch (err) { console.error(err); res.status(500).json({ error: 'Database error' }); }
});

// CREATE (POST)
// Add a new pantry item
// Endpoint: POST /api/pantry
app.post('/api/pantry', async (req, res) => {
  const { name, quantity, unit, expiryDate } = req.body;
  if (!name || !name.trim() || !(Number(quantity) > 0) || !unit) {
    return res.status(400).json({ error: 'name, quantity (> 0) and unit are required' });
  }
  try {
    const result = await pool.query(
      `INSERT INTO pantry_items (name, quantity, unit, expiry_date)
       VALUES ($1, $2, $3, $4)
       RETURNING id, name, quantity, unit, to_char(expiry_date, 'YYYY-MM-DD') AS expiry_date`,
      [name.trim(), quantity, unit, expiryDate || null]);
    res.status(201).json(mapPantry(result.rows[0]));
  } catch (err) { console.error(err); res.status(500).json({ error: 'Database error' }); }
});

// UPDATE (PUT)
// Update an existing pantry item
// Endpoint: PUT /api/pantry/:id
app.put('/api/pantry/:id', async (req, res) => {
  const { name, quantity, unit, expiryDate } = req.body;
  if (!name || !name.trim() || !(Number(quantity) > 0) || !unit) {
    return res.status(400).json({ error: 'name, quantity (> 0) and unit are required' });
  }
  try {
    const result = await pool.query(
      `UPDATE pantry_items SET name = $1, quantity = $2, unit = $3, expiry_date = $4
       WHERE id = $5
       RETURNING id, name, quantity, unit, to_char(expiry_date, 'YYYY-MM-DD') AS expiry_date`,
      [name.trim(), quantity, unit, expiryDate || null, req.params.id]);
    if (result.rowCount === 0) return res.status(404).json({ error: 'Item not found' });
    res.json(mapPantry(result.rows[0]));
  } catch (err) { console.error(err); res.status(500).json({ error: 'Database error' }); }
});

// DELETE
// Remove pantry item by ID
// Endpoint: DELETE /api/pantry/:id
app.delete('/api/pantry/:id', async (req, res) => {
  try {
    const result = await pool.query('DELETE FROM pantry_items WHERE id = $1', [req.params.id]);
    if (result.rowCount === 0) return res.status(404).json({ error: 'Item not found' });
    res.status(204).end();
  } catch (err) { console.error(err); res.status(500).json({ error: 'Database error' }); }
});

// READ RECIPES
// Get recipes and their ingredients
// Endpoint: GET /api/recipes
app.get('/api/recipes', async (req, res) => {
  try {
    const recipes = await pool.query('SELECT id, name, steps FROM recipes ORDER BY name');
    const ingredients = await pool.query(
      'SELECT recipe_id, name, quantity, unit FROM recipe_ingredients ORDER BY id');
    const byId = {};
    for (const r of recipes.rows) byId[r.id] = { id: r.id, name: r.name, steps: r.steps, ingredients: [] };
    for (const i of ingredients.rows) {
      if (byId[i.recipe_id]) byId[i.recipe_id].ingredients.push(
        { name: i.name, quantity: Number(i.quantity), unit: i.unit });
    }
    res.json(Object.values(byId));
  } catch (err) { console.error(err); res.status(500).json({ error: 'Database error' }); }
});

const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => console.log(`API listening on http://localhost:${PORT}`));