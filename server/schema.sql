CREATE DATABASE smart_pantry;

DROP TABLE IF EXISTS recipe_ingredients, recipes, pantry_items;

CREATE TABLE pantry_items (
  id          SERIAL PRIMARY KEY,
  name        TEXT NOT NULL,
  quantity    NUMERIC NOT NULL CHECK (quantity > 0),
  unit        TEXT NOT NULL,
  expiry_date DATE
);

CREATE TABLE recipes (
  id    SERIAL PRIMARY KEY,
  name  TEXT NOT NULL UNIQUE,
  steps TEXT NOT NULL
);

CREATE TABLE recipe_ingredients (
  id        SERIAL PRIMARY KEY,
  recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
  name      TEXT NOT NULL,
  quantity  NUMERIC NOT NULL CHECK (quantity > 0),
  unit      TEXT NOT NULL
);