-- server/seed.sql
-- Ingredient names are stored in canonical lowercase singular form.

INSERT INTO recipes (name, steps) VALUES
('Tomato Omelette',
 '1. Beat the eggs with a pinch of salt. 2. Fry the chopped tomato in oil for 2 minutes. 3. Pour in the eggs and cook until set.'),
('Tomato Pasta',
 '1. Boil the pasta until tender. 2. Simmer the tomatoes with crushed garlic in oil for 10 minutes. 3. Combine, season and serve.'),
('Garlic Rice',
 '1. Rinse the rice. 2. Fry sliced garlic in oil until golden. 3. Add rice and water, cover and simmer for 15 minutes.'),
('Cheese Omelette',
 '1. Beat the eggs. 2. Melt butter in a pan, add eggs and cook gently. 3. Sprinkle grated cheese over half, fold and serve.'),
('Potato Wedges',
 '1. Cut potatoes into wedges. 2. Toss with oil and salt. 3. Bake at 200C for 30 minutes, turning once.'),
('Mashed Potatoes',
 '1. Boil potatoes until soft. 2. Drain and mash with warm milk and butter. 3. Season to taste.'),
('Tuna Sandwich',
 '1. Mix tuna with mayonnaise. 2. Spread between two slices of bread. 3. Serve immediately.'),
('Cheese Toast',
 '1. Butter the bread. 2. Top with sliced cheese. 3. Grill until the cheese melts.'),
('Banana Smoothie',
 '1. Peel and slice the bananas. 2. Blend with milk and yogurt until smooth. 3. Serve chilled.'),
('Pancakes',
 '1. Whisk flour, egg, milk and sugar into a batter. 2. Melt butter in a pan. 3. Fry ladlefuls until golden on both sides.'),
('Chicken Stir Fry',
 '1. Slice the chicken. 2. Fry onion in oil, add chicken and cook through. 3. Add sliced carrot and stir fry for 5 minutes.'),
('Chicken Rice',
 '1. Brown the chicken in oil. 2. Add chopped onion and garlic, then the rice. 3. Add water, cover and simmer for 20 minutes.'),
('Carrot Soup',
 '1. Fry chopped onion until soft. 2. Add sliced carrots and water. 3. Simmer for 20 minutes, blend and season.'),
('Spinach Omelette',
 '1. Wilt the spinach in butter. 2. Pour over beaten eggs. 3. Cook until set and season with salt.'),
('Cheesy Pasta Sauce',
 '1. Boil the pasta. 2. Make a roux with butter and flour, then whisk in milk. 3. Stir in cheese, combine with pasta and serve.'),
('Lemon Rice',
 '1. Cook the rice and cool slightly. 2. Heat oil and toss the rice through it. 3. Squeeze over lemon juice and add salt.'),
('Potato Omelette',
 '1. Fry diced potato and onion in oil until soft. 2. Pour over beaten eggs. 3. Cook until set and fold.'),
('Tuna Pasta',
 '1. Boil the pasta. 2. Fry garlic and tomato in oil. 3. Stir in the tuna and pasta and heat through.'),
('Onion Soup',
 '1. Fry sliced onions in butter until caramelised. 2. Add water and simmer for 20 minutes. 3. Top with bread and cheese and grill.'),
('French Toast',
 '1. Whisk egg, milk and sugar. 2. Soak the bread in the mixture. 3. Fry in butter until golden on both sides.')
ON CONFLICT (name) DO NOTHING; -- tells postgres to skip recipes that already exist and insert the remaining recipes.

INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit)
SELECT r.id, v.name, v.quantity::numeric, v.unit::text
FROM (VALUES
  ('Tomato Omelette',       'egg',            2,   'piece'),
  ('Tomato Omelette',       'tomato',         1,   'piece'),
  ('Tomato Omelette',       'oil',            1,   'tbsp'),

  ('Tomato Pasta',          'pasta',          250, 'g'),
  ('Tomato Pasta',          'tomato',         3,   'piece'),
  ('Tomato Pasta',          'garlic',         2,   'piece'),
  ('Tomato Pasta',          'oil',            2,   'tbsp'),

  ('Garlic Rice',           'rice',           300, 'g'),
  ('Garlic Rice',           'garlic',         4,   'piece'),
  ('Garlic Rice',           'oil',            2,   'tbsp'),

  ('Cheese Omelette',       'egg',            3,   'piece'),
  ('Cheese Omelette',       'cheese',         50,  'g'),
  ('Cheese Omelette',       'butter',         10,  'g'),

  ('Potato Wedges',         'potato',         4,   'piece'),
  ('Potato Wedges',         'oil',            2,   'tbsp'),
  ('Potato Wedges',         'salt',           1,   'tsp'),

  ('Mashed Potatoes',       'potato',         4,   'piece'),
  ('Mashed Potatoes',       'milk',           100, 'ml'),
  ('Mashed Potatoes',       'butter',         20,  'g'),

  ('Tuna Sandwich',         'bread',          2,   'piece'),
  ('Tuna Sandwich',         'tuna',           100, 'g'),
  ('Tuna Sandwich',         'mayonnaise',     2,   'tbsp'),

  ('Cheese Toast',          'bread',          2,   'piece'),
  ('Cheese Toast',          'cheese',         40,  'g'),
  ('Cheese Toast',          'butter',         10,  'g'),

  ('Banana Smoothie',       'banana',         2,   'piece'),
  ('Banana Smoothie',       'milk',           200, 'ml'),
  ('Banana Smoothie',       'yogurt',         100, 'g'),

  ('Pancakes',              'flour',          200, 'g'),
  ('Pancakes',              'egg',            2,   'piece'),
  ('Pancakes',              'milk',           250, 'ml'),
  ('Pancakes',              'sugar',          2,   'tbsp'),
  ('Pancakes',              'butter',         20,  'g'),

  ('Chicken Stir Fry',      'chicken breast', 300, 'g'),
  ('Chicken Stir Fry',      'carrot',         2,   'piece'),
  ('Chicken Stir Fry',      'onion',          1,   'piece'),
  ('Chicken Stir Fry',      'oil',            2,   'tbsp'),

  ('Chicken Rice',          'chicken breast', 250, 'g'),
  ('Chicken Rice',          'rice',           200, 'g'),
  ('Chicken Rice',          'onion',          1,   'piece'),
  ('Chicken Rice',          'garlic',         2,   'piece'),
  ('Chicken Rice',          'oil',            2,   'tbsp'),

  ('Carrot Soup',           'carrot',         4,   'piece'),
  ('Carrot Soup',           'onion',          1,   'piece'),
  ('Carrot Soup',           'water',          500, 'ml'),
  ('Carrot Soup',           'salt',           1,   'tsp'),

  ('Spinach Omelette',      'egg',            2,   'piece'),
  ('Spinach Omelette',      'spinach',        100, 'g'),
  ('Spinach Omelette',      'butter',         10,  'g'),
  ('Spinach Omelette',      'salt',           1,   'tsp'),

  ('Cheesy Pasta Sauce',    'pasta',          200, 'g'),
  ('Cheesy Pasta Sauce',    'milk',           150, 'ml'),
  ('Cheesy Pasta Sauce',    'cheese',         60,  'g'),
  ('Cheesy Pasta Sauce',    'butter',         20,  'g'),
  ('Cheesy Pasta Sauce',    'flour',          20,  'g'),

  ('Lemon Rice',            'rice',           250, 'g'),
  ('Lemon Rice',            'lemon',          1,   'piece'),
  ('Lemon Rice',            'oil',            2,   'tbsp'),
  ('Lemon Rice',            'salt',           1,   'tsp'),

  ('Potato Omelette',       'egg',            3,   'piece'),
  ('Potato Omelette',       'potato',         2,   'piece'),
  ('Potato Omelette',       'onion',          1,   'piece'),
  ('Potato Omelette',       'oil',            1,   'tbsp'),

  ('Tuna Pasta',            'pasta',          200, 'g'),
  ('Tuna Pasta',            'tuna',           100, 'g'),
  ('Tuna Pasta',            'tomato',         2,   'piece'),
  ('Tuna Pasta',            'oil',            1,   'tbsp'),
  ('Tuna Pasta',            'garlic',         2,   'piece'),

  ('Onion Soup',            'onion',          3,   'piece'),
  ('Onion Soup',            'water',          600, 'ml'),
  ('Onion Soup',            'butter',         20,  'g'),
  ('Onion Soup',            'bread',          1,   'piece'),
  ('Onion Soup',            'cheese',         30,  'g'),

  ('French Toast',          'bread',          3,   'piece'),
  ('French Toast',          'egg',            2,   'piece'),
  ('French Toast',          'milk',           100, 'ml'),
  ('French Toast',          'sugar',          1,   'tbsp'),
  ('French Toast',          'butter',         10,  'g')
) AS v(recipe_name, name, quantity, unit)
JOIN recipes r ON r.name = v.recipe_name
WHERE NOT EXISTS (
    SELECT 1
    FROM recipe_ingredients ri
    WHERE ri.recipe_id = r.id
      AND ri.name = v.name
      AND ri.quantity = v.quantity::numeric
      AND ri.unit = v.unit::text
);