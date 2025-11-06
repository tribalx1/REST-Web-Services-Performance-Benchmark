#!/usr/bin/env python3
"""
Generate CSV files for JMeter load testing
- category_ids.csv: Random category IDs
- item_ids.csv: Random item IDs
- category_payloads.csv: JSON payloads for category POST/PUT
- item_payloads_1kb.csv: JSON payloads for item POST/PUT (~1KB)
- item_payloads_5kb.csv: JSON payloads for item POST/PUT (~5KB)
"""

import csv
import json
import random
from decimal import Decimal

# Configuration
NUM_CATEGORIES = 2000
NUM_ITEMS = 100000
CSV_SAMPLES = 1000  # Number of samples for each CSV

def generate_category_ids():
    """Generate random category IDs"""
    category_ids = random.sample(range(1, NUM_CATEGORIES + 1), min(CSV_SAMPLES, NUM_CATEGORIES))
    
    with open('category_ids.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['categoryId'])
        for cat_id in category_ids:
            writer.writerow([cat_id])
    
    print(f"✓ Generated category_ids.csv with {len(category_ids)} entries")

def generate_item_ids():
    """Generate random item IDs"""
    item_ids = random.sample(range(1, NUM_ITEMS + 1), min(CSV_SAMPLES, NUM_ITEMS))
    
    with open('item_ids.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['itemId'])
        for item_id in item_ids:
            writer.writerow([item_id])
    
    print(f"✓ Generated item_ids.csv with {len(item_ids)} entries")

def generate_category_payloads():
    """Generate category JSON payloads (~0.5-1KB)"""
    
    categories = []
    for i in range(CSV_SAMPLES):
        code = f"TEST{random.randint(10000, 99999)}"
        name = f"Test Category {random.randint(1, 10000)} - {random.choice(['Electronics', 'Furniture', 'Clothing', 'Food', 'Books', 'Toys'])}"
        
        payload = {
            "code": code,
            "name": name
        }
        categories.append(json.dumps(payload))
    
    with open('category_payloads.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['payload'])
        for payload in categories:
            writer.writerow([payload])
    
    print(f"✓ Generated category_payloads.csv with {len(categories)} entries")

def generate_item_payloads_1kb():
    """Generate item JSON payloads (~1KB)"""
    
    items = []
    products = [
        "Laptop", "Smartphone", "Tablet", "Monitor", "Keyboard", "Mouse",
        "Desk", "Chair", "Lamp", "Sofa", "Table", "Cabinet",
        "Shirt", "Pants", "Shoes", "Jacket", "Hat", "Gloves",
        "Apple", "Banana", "Orange", "Bread", "Milk", "Cheese",
        "Novel", "Textbook", "Magazine", "Comic", "Dictionary",
        "Action Figure", "Puzzle", "Board Game", "Doll", "Car"
    ]
    
    brands = ["Sony", "Samsung", "Apple", "Dell", "HP", "LG", "IKEA", "Nike", "Adidas", "Generic"]
    
    for i in range(CSV_SAMPLES):
        sku = f"SKU{random.randint(100000, 999999)}"
        product = random.choice(products)
        brand = random.choice(brands)
        name = f"{brand} {product} - Model {random.randint(100, 999)}"
        price = round(random.uniform(9.99, 999.99), 2)
        stock = random.randint(0, 500)
        category_id = random.randint(1, NUM_CATEGORIES)
        
        payload = {
            "sku": sku,
            "name": name,
            "price": price,
            "stock": stock,
            "categoryId": category_id
        }
        items.append(json.dumps(payload))
    
    with open('item_payloads_1kb.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['payload'])
        for payload in items:
            writer.writerow([payload])
    
    print(f"✓ Generated item_payloads_1kb.csv with {len(items)} entries")

def generate_item_payloads_5kb():
    """Generate item JSON payloads (~5KB) with description"""
    
    items = []
    products = [
        "Laptop", "Smartphone", "Tablet", "Monitor", "Keyboard", "Mouse",
        "Desk", "Chair", "Lamp", "Sofa", "Table", "Cabinet",
        "Shirt", "Pants", "Shoes", "Jacket", "Hat", "Gloves",
        "Apple", "Banana", "Orange", "Bread", "Milk", "Cheese",
        "Novel", "Textbook", "Magazine", "Comic", "Dictionary",
        "Action Figure", "Puzzle", "Board Game", "Doll", "Car"
    ]
    
    brands = ["Sony", "Samsung", "Apple", "Dell", "HP", "LG", "IKEA", "Nike", "Adidas", "Generic"]
    
    # Generate a long description to reach ~5KB
    description_template = """
    This is a premium quality product featuring advanced technology and superior craftsmanship.
    Designed for maximum performance and durability, this item meets the highest industry standards.
    Key features include: enhanced functionality, ergonomic design, energy efficiency, and long-lasting materials.
    Perfect for both professional and personal use, backed by comprehensive warranty and support.
    Available in multiple colors and configurations to suit your specific needs and preferences.
    Technical specifications: Manufactured using state-of-the-art processes ensuring consistency and quality.
    User reviews consistently rate this product highly for its reliability, performance, and value.
    Compatible with various accessories and expandable for future upgrades and enhancements.
    Environmentally friendly production methods with sustainable materials and recyclable packaging.
    Customer satisfaction guaranteed with easy returns and dedicated customer service support team.
    """ * 15  # Repeat to reach ~5KB
    
    for i in range(CSV_SAMPLES):
        sku = f"SKU{random.randint(100000, 999999)}"
        product = random.choice(products)
        brand = random.choice(brands)
        name = f"{brand} {product} - Professional Edition Model {random.randint(1000, 9999)}"
        price = round(random.uniform(49.99, 2999.99), 2)
        stock = random.randint(0, 500)
        category_id = random.randint(1, NUM_CATEGORIES)
        
        payload = {
            "sku": sku,
            "name": name,
            "price": price,
            "stock": stock,
            "categoryId": category_id,
            "description": description_template.strip()
        }
        items.append(json.dumps(payload))
    
    with open('item_payloads_5kb.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['payload'])
        for payload in items:
            writer.writerow([payload])
    
    # Check actual size
    avg_size = sum(len(p) for p in items) / len(items)
    print(f"✓ Generated item_payloads_5kb.csv with {len(items)} entries (avg size: {avg_size:.0f} bytes)")

def main():
    print("=" * 60)
    print("Generating CSV files for JMeter load testing")
    print("=" * 60)
    
    generate_category_ids()
    generate_item_ids()
    generate_category_payloads()
    generate_item_payloads_1kb()
    generate_item_payloads_5kb()
    
    print("=" * 60)
    print("✓ All CSV files generated successfully!")
    print("=" * 60)
    print("\nGenerated files:")
    print("  - category_ids.csv")
    print("  - item_ids.csv")
    print("  - category_payloads.csv")
    print("  - item_payloads_1kb.csv")
    print("  - item_payloads_5kb.csv")
    print("\nThese files are ready to be used with JMeter test plans.")

if __name__ == "__main__":
    main()
