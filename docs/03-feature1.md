## Feature 1 : Import Products from CSV 

### Objective
Load product data from `products.csv` into the database, validating each record against the business rules below so that only valid data is persisted.

### Input
- **File:** `src/main/resources/data/products.csv`
- **Format:** CSV with a header row, comma-separated, UTF-8 encoded
- **Example:**

```
name,description,price,category
Bracelet V895,Rose Gold bracelet crafted with durable materials,1461.67,JEWELRY
Organic Pasta E741,Organic pasta crafted with easy maintenance,20.99,GROCERY
Rustic Vase T696,Rustic vase crafted with long-lasting performance,52.51,HOME
Running Hiking Shoes R385,Running hiking shoes crafted with great value,66.46,FOOTWEAR
Gluten-Free Basmati Rice P821,Gluten-Free basmati rice crafted with easy maintenance,1.01,GROCERY
```

### Business rules
* `name` must not be null or blank
* `description` must not be null or blank
* `price` must not be negative, zero is allowed
* `category` must exist in the database table `product_category` by doing mapping on the key

### Expected Behavior
1. Read the file from the classpath (`data/products.csv`) and skip the header row
2. For each row, parse the fields and apply all four validation rules
3. If the row is valid, persist it as a product
4. If the row is invalid, skip it, log a warning

