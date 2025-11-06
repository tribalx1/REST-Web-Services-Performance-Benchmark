#!/usr/bin/env python3
"""
Data Generation Script for REST Benchmark
Generates:
- 2,000 categories (CAT0001..CAT2000)
- 100,000 items (~50 items per category)
"""

import csv
import random
from datetime import datetime, timedelta

# Configuration
NUM_CATEGORIES = 2000
NUM_ITEMS = 100000
ITEMS_PER_CATEGORY = NUM_ITEMS // NUM_CATEGORIES

# Sample data for realistic names
CATEGORY_PREFIXES = [
    "Electronics", "Clothing", "Home", "Sports", "Books", "Toys", "Beauty",
    "Garden", "Automotive", "Food", "Health", "Office", "Pet", "Music",
    "Baby", "Jewelry", "Shoes", "Tools", "Arts", "Industrial"
]

CATEGORY_SUFFIXES = [
    "Products", "Essentials", "Collection", "Supplies", "Equipment", "Accessories",
    "Items", "Goods", "Materials", "Components", "Series", "Line", "Range"
]

ITEM_ADJECTIVES = [
    "Premium", "Deluxe", "Pro", "Advanced", "Basic", "Classic", "Modern",
    "Vintage", "Eco", "Smart", "Ultra", "Super", "Mega", "Mini", "Compact",
    "Professional", "Standard", "Essential", "Ultimate", "Elite"
]

ITEM_NOUNS = [
    "Widget", "Gadget", "Device", "Tool", "Component", "Module", "Unit",
    "System", "Kit", "Set", "Package", "Bundle", "Item", "Product", "Article"
]

def generate_categories():
    """Generate category data"""
    categories = []
    now = datetime.now()
    
    for i in range(1, NUM_CATEGORIES + 1):
        code = f"CAT{i:04d}"
        prefix = random.choice(CATEGORY_PREFIXES)
        suffix = random.choice(CATEGORY_SUFFIXES)
        name = f"{prefix} {suffix} {i}"
        updated_at = now - timedelta(days=random.randint(0, 365))
        
        categories.append({
            'code': code,
            'name': name,
            'updated_at': updated_at.strftime('%Y-%m-%d %H:%M:%S')
        })
    
    return categories

def generate_items(categories):
    """Generate item data"""
    items = []
    now = datetime.now()
    item_id = 1
    
    for category_idx, category in enumerate(categories):
        category_id = category_idx + 1
        
        # Generate approximately ITEMS_PER_CATEGORY items per category
        # Add some randomness: ±10 items
        num_items = ITEMS_PER_CATEGORY + random.randint(-10, 10)
        
        for j in range(num_items):
            sku = f"SKU{item_id:06d}"
            adj = random.choice(ITEM_ADJECTIVES)
            noun = random.choice(ITEM_NOUNS)
            name = f"{adj} {noun} {item_id}"
            
            # Price between $5.00 and $999.99
            price = round(random.uniform(5.0, 999.99), 2)
            
            # Stock between 0 and 500
            stock = random.randint(0, 500)
            
            updated_at = now - timedelta(days=random.randint(0, 365))
            
            items.append({
                'sku': sku,
                'name': name,
                'price': price,
                'stock': stock,
                'category_id': category_id,
                'updated_at': updated_at.strftime('%Y-%m-%d %H:%M:%S')
            })
            
            item_id += 1
            
            # Break if we've reached the total number of items
            if item_id > NUM_ITEMS:
                break
        
        if item_id > NUM_ITEMS:
            break
    
    return items

def write_csv(filename, data, fieldnames):
    """Write data to CSV file"""
    with open(filename, 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(data)

def main():
    print("Generating benchmark data...")
    
    # Generate categories
    print(f"Generating {NUM_CATEGORIES} categories...")
    categories = generate_categories()
    write_csv('categories.csv', categories, ['code', 'name', 'updated_at'])
    print(f"✓ Written {len(categories)} categories to categories.csv")
    
    # Generate items
    print(f"Generating ~{NUM_ITEMS} items...")
    items = generate_items(categories)
    write_csv('items.csv', items, ['sku', 'name', 'price', 'stock', 'category_id', 'updated_at'])
    print(f"✓ Written {len(items)} items to items.csv")
    
    print("\nData generation complete!")
    print(f"Categories: {len(categories)}")
    print(f"Items: {len(items)}")
    print(f"Average items per category: {len(items) / len(categories):.1f}")

if __name__ == '__main__':
    main()
