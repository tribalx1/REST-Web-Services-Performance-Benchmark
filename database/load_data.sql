-- =============================================
-- Load Generated Data into Database
-- Run this after generating categories.csv and items.csv
-- =============================================

\echo 'Loading category data...'
\COPY category(code, name, updated_at) FROM 'categories.csv' WITH CSV HEADER;

\echo 'Loading item data...'
\COPY item(sku, name, price, stock, category_id, updated_at) FROM 'items.csv' WITH CSV HEADER;

-- Update sequences to correct values
SELECT setval('category_id_seq', (SELECT MAX(id) FROM category));
SELECT setval('item_id_seq', (SELECT MAX(id) FROM item));

-- Display counts
SELECT 'Categories loaded: ' || COUNT(*) FROM category;
SELECT 'Items loaded: ' || COUNT(*) FROM item;

-- Sample data verification
SELECT 'Sample category:';
SELECT * FROM category LIMIT 1;

SELECT 'Sample item:';
SELECT * FROM item LIMIT 1;

-- Verify distribution
SELECT 
    'Items per category - Min: ' || MIN(cnt) || ', Max: ' || MAX(cnt) || ', Avg: ' || ROUND(AVG(cnt), 2)
FROM (
    SELECT category_id, COUNT(*) as cnt 
    FROM item 
    GROUP BY category_id
) AS distribution;
