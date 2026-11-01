## Feature 2 : Export Products to CSV

### Objective
Read the products stored in the database **page by page** and write them to a new CSV file, so that the full table can be exported without ever loading it entirely in memory.

### Input
- **Source:** database tables `product` and `product_category`
- **Read strategy:** paginated, ordered by `id` so that pages are stable and no row is read twice or skipped

### Output
- **File:** `output/products-export.csv`
- **Format:** CSV with a header row, comma-separated, UTF-8 encoded
- **Columns:** same as the import file (`name,description,price,category`), so the export can be re-imported by Feature 1
- **Example:**

```
name,description,price,category
Bracelet V895,Rose Gold bracelet crafted with durable materials,1461.67,JEWELRY
Organic Pasta E741,Organic pasta crafted with easy maintenance,20.99,GROCERY
Rustic Vase T696,Rustic vase crafted with long-lasting performance,52.51,HOME
```
