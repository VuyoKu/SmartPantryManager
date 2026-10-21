INSERT INTO recipes (name, steps) VALUES
('Tomato Omelette',
 '1. Beat the eggs with salt. 2. Fry the tomato in oil for 2 minutes. 3. Pour in the eggs and cook until set.'),
('Tomato Pasta',
 '1. Boil pasta until tender. 2. Simmer tomatoes with garlic and oil for 10 minutes. 3. Combine and season.'),
('Garlic Rice',
 '1. Rinse rice. 2. Fry garlic in oil until golden. 3. Add rice and water, simmer covered for 15 minutes.');

INSERT INTO recipe_ingredients (recipe_id, name, quantity, unit) VALUES
((SELECT id FROM recipes WHERE name = 'Tomato Omelette'), 'egg',    2, 'piece'),
((SELECT id FROM recipes WHERE name = 'Tomato Omelette'), 'tomato', 1, 'piece'),
((SELECT id FROM recipes WHERE name = 'Tomato Omelette'), 'oil',    1, 'tbsp'),
((SELECT id FROM recipes WHERE name = 'Tomato Pasta'),    'pasta',  250, 'g'),
((SELECT id FROM recipes WHERE name = 'Tomato Pasta'),    'tomato', 3, 'piece'),
((SELECT id FROM recipes WHERE name = 'Tomato Pasta'),    'garlic', 2, 'piece'),
((SELECT id FROM recipes WHERE name = 'Tomato Pasta'),    'oil',    2, 'tbsp'),
((SELECT id FROM recipes WHERE name = 'Garlic Rice'),     'rice',   300, 'g'),
((SELECT id FROM recipes WHERE name = 'Garlic Rice'),     'garlic', 4, 'piece'),
((SELECT id FROM recipes WHERE name = 'Garlic Rice'),     'oil',    2, 'tbsp');