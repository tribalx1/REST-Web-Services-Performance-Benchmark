#!/usr/bin/env python3
"""
Simplified CSV generator for JMeter - can run without database connection
Generates test data files needed for JMeter load testing
"""

import csv
import json
import random

# Configuration
NUM_CATEGORIES = 2000
NUM_ITEMS = 100000
CSV_SAMPLES = 1000

def generate_all_csv_files():
    """Generate all CSV files needed for JMeter"""
    
    print("=" * 60)
    print("Generating JMeter Test Data Files")
    print("=" * 60)
    
    # 1. Category IDs
    print("\n[1/5] Generating category_ids.csv...")
    category_ids = random.sample(range(1, NUM_CATEGORIES + 1), CSV_SAMPLES)
    with open('category_ids.csv', 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['categoryId'])
        for cid in category_ids:
            writer.writerow([cid])
    print(f"      [OK] {CSV_SAMPLES} category IDs")
    
    # 2. Item IDs
    print("\n[2/5] Generating item_ids.csv...")
    item_ids = random.sample(range(1, NUM_ITEMS + 1), CSV_SAMPLES)
    with open('item_ids.csv', 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(['itemId'])
        for iid in item_ids:
            writer.writerow([iid])
    print(f"      [OK] {CSV_SAMPLES} item IDs")
    
    # 3. Category Payloads
    print("\n[3/5] Generating category_payloads.csv...")
    with open('category_payloads.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['payload'])
        for i in range(CSV_SAMPLES):
            payload = {
                "code": f"TEST{random.randint(10000, 99999)}",
                "name": f"Test Category {random.randint(1, 10000)}"
            }
            writer.writerow([json.dumps(payload)])
    print(f"      [OK] {CSV_SAMPLES} category payloads (~0.5-1KB each)")
    
    # 4. Item Payloads 1KB
    print("\n[4/5] Generating item_payloads_1kb.csv...")
    products = ["Laptop", "Phone", "Tablet", "Monitor", "Keyboard", "Mouse", "Desk", "Chair"]
    with open('item_payloads_1kb.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['payload'])
        for i in range(CSV_SAMPLES):
            payload = {
                "sku": f"SKU{random.randint(100000, 999999)}",
                "name": f"{random.choice(products)} - Model {random.randint(100, 999)}",
                "price": round(random.uniform(9.99, 999.99), 2),
                "stock": random.randint(0, 500),
                "categoryId": random.randint(1, NUM_CATEGORIES)
            }
            writer.writerow([json.dumps(payload)])
    print(f"      [OK] {CSV_SAMPLES} item payloads (~1KB each)")
    
    # 5. Item Payloads 5KB
    print("\n[5/5] Generating item_payloads_5kb.csv...")
    long_desc = ("Premium product with advanced features and superior quality. " * 200)
    with open('item_payloads_5kb.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['payload'])
        for i in range(CSV_SAMPLES):
            payload = {
                "sku": f"SKU{random.randint(100000, 999999)}",
                "name": f"Professional {random.choice(products)} - Edition {random.randint(1000, 9999)}",
                "price": round(random.uniform(49.99, 2999.99), 2),
                "stock": random.randint(0, 500),
                "categoryId": random.randint(1, NUM_CATEGORIES),
                "description": long_desc
            }
            writer.writerow([json.dumps(payload)])
    print(f"      [OK] {CSV_SAMPLES} item payloads (~5KB each)")
    
    print("\n" + "=" * 60)
    print("[SUCCESS] ALL CSV FILES GENERATED!")
    print("=" * 60)
    print("\nFiles created in current directory:")
    print("  - category_ids.csv")
    print("  - item_ids.csv")
    print("  - category_payloads.csv")
    print("  - item_payloads_1kb.csv")
    print("  - item_payloads_5kb.csv")
    print("\nReady for JMeter load testing!")

if __name__ == "__main__":
    generate_all_csv_files()
