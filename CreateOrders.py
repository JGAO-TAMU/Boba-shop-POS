import random
import datetime

# ------------------------------
# CONFIGURATION
# ------------------------------
NUM_WEEKS = 52           # α = 52 weeks
TARGET_SALES = 1_000_000 # β ≈ $1M
PEAK_DAYS = 2            # φ = 2 peak days
NUM_MENU_ITEMS = 20      # δ = 20 different menu items

# You can tweak these numbers to control how many orders you generate daily, 
# average drink price, etc. The script tries to approximate $1M in total sales.
BASE_DAILY_ORDERS = 150  
PEAK_MULTIPLIER = 10      # On peak days, ~3x the normal daily volume
AVG_DRINK_PRICE = 5.0    # average base price for a drink
MOD_PROBABILITY = 0.4    # 40% chance a drink has modifications
MAX_MODS_PER_DRINK = 2   # up to 2 modifications on a single drink

# We assume there's already an employee with ID=1 in your database
EMPLOYEE_ID = 1

# Starting about a year ago until now
end_date = datetime.datetime.now()
start_date = end_date - datetime.timedelta(weeks=NUM_WEEKS)

# Define menu items with updated prices
menu_items = [
    ("Classic Milk Tea", 4.50), ("Taro Milk Tea", 4.75), ("Thai Milk Tea", 4.50),
    ("Honeydew Milk Tea", 4.75), ("Matcha Milk Tea", 4.75), ("Brown Sugar Milk Tea", 5.00),
    ("Wintermelon Milk Tea", 4.50), ("Oolong Milk Tea", 4.50), ("Jasmine Green Milk Tea", 4.50),
    ("Earl Grey Milk Tea", 4.50), ("Strawberry Fruit Tea", 4.75), ("Mango Fruit Tea", 4.75),
    ("Passion Fruit Green Tea", 4.75), ("Lychee Black Tea", 4.75), ("Peach Oolong Tea", 4.75),
    ("Grapefruit Green Tea", 4.75), ("Lemon Black Tea", 4.50), ("Kiwi Fruit Tea", 4.75),
    ("Coconut Milk Tea", 4.75), ("Almond Milk Tea", 4.75)
]

# Update modifications with new prices and remove unavailable ones
possible_mods = [
    ("Boba Pearls", 0.50),
    ("Pudding", 0.50),
    ("Grass Jelly", 0.50),
    ("Lychee Jelly", 0.50),
    ("Mango Bits", 0.50),
    ("Strawberry Bits", 0.50),
    ("Ice", 0.00),
    ("Peach Bits", 0.50)
]

# Replace the random peak days section with fixed dates
peak_days = {
    datetime.date(2024, 11, 30),
    datetime.date(2025, 1, 25)
}

# For linking modifications to inventory (ModRequiresInventory), 
# you need known ingredientIDs from your Inventory table.
# For demonstration, let's assume your Inventory table has ingredientIDs from 1..N.
# We'll just pick random IDs to associate with each modification.
# If you know your actual ingredientIDs, you can adjust accordingly.
MAX_INGREDIENT_ID = 35  # Suppose we have 50 items in Inventory
def random_inventory_id():
    return random.randint(1, MAX_INGREDIENT_ID)

# ------------------------------
# DATA GENERATION
# ------------------------------
orders_rows = [["orderID", "timestamp", "price", "employeeID"]]
drinks_rows = [["drinkID", "name","orderID"]]
modifications_rows = [["modID", "drinkID", "modmenuID"]]

order_id_counter = 1
drink_id_counter = 1
mod_id_counter = 1
total_sales_so_far = 0.0

# We'll iterate day by day from start_date to end_date
current_date = start_date
while total_sales_so_far < TARGET_SALES or current_date <= end_date:
    # Decide how many orders today
    # If it's a peak day, multiply by PEAK_MULTIPLIER
    is_peak = (current_date.date() in peak_days)
    daily_orders = BASE_DAILY_ORDERS
    if is_peak:
        daily_orders = int(daily_orders * PEAK_MULTIPLIER)

    # We'll create 'daily_orders' worth of Orders for this date
    for _ in range(daily_orders):
        # Create random time in the day
        random_time = datetime.time(
            hour=random.randint(6, 20),
            minute=random.randint(0, 59),
            second=random.randint(0, 59)
        )
        timestamp = datetime.datetime.combine(current_date.date(), random_time)

        # Add order row with initial price of 0
        orders_rows.append([
            order_id_counter,
            timestamp.strftime('%Y-%m-%d %H:%M:%S'),
            0,
            EMPLOYEE_ID
        ])

        current_order_id = order_id_counter
        order_id_counter += 1
        order_total = 0.0

        # Decide how many drinks in this order
        num_drinks = random.randint(1, 5)
        for _d in range(num_drinks):
            # Choose a random menu item
            drink_idx, (drink_name, base_cost) = random.choice(list(enumerate(menu_items)))
            drink_idx+=1
            cost = base_cost
            total_cost = base_cost
            
            # Add drink row with initial costs
            current_drink = [
                drink_id_counter,
                current_order_id,
                drink_idx
            ]
            drinks_rows.append(current_drink)

            current_drink_id = drink_id_counter
            drink_id_counter += 1

            if random.random() < MOD_PROBABILITY:
                # Choose random number of modifications
                mod_idx, (mod_name, mod_extra_cost) = random.choice(list(enumerate(possible_mods)))
                mod_idx+=1
                mod_cost = mod_extra_cost
                total_cost += mod_extra_cost
                mod_id_counter += 1

                # Add modification row with initial costs
                current_mod = [
                    mod_id_counter,
                    current_drink_id,
                    mod_idx,
                ]
                modifications_rows.append(current_mod)

            order_total += total_cost

        # Update the order's final price
        orders_rows[current_order_id][2] = round(order_total, 2)
        total_sales_so_far += order_total

    current_date += datetime.timedelta(days=1)

# Write to CSV files
import csv

def write_csv(filename, rows):
    with open(filename, 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerows(rows)

write_csv('orders.csv', orders_rows)
write_csv('drinks.csv', drinks_rows)
write_csv('modifications.csv', modifications_rows)

print(f"Script complete! Generated:")
print(f"- {len(orders_rows)-1} orders")
print(f"- {len(drinks_rows)-1} drinks")
print(f"- {len(modifications_rows)-1} modifications")
print(f"Total sales reached: ${round(total_sales_so_far,2):,.2f}")
print("Wrote data to CSV files: orders.csv, drinks.csv, modifications.csv")