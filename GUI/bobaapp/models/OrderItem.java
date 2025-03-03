package bobaapp.models;

import java.util.List;

public class OrderItem {
    private MenuItem item;
    private String iceLevel;
    private String sugarLevel;
    private int iceQuantity;
    private int sugarQuantity;
    private List<String> toppings;
    private double totalPrice;

    public OrderItem(MenuItem item, String iceLevel, String sugarLevel, List<String> toppings) {
        this.item = item;
        this.iceLevel = iceLevel;
        this.sugarLevel = sugarLevel;
        this.toppings = toppings;
        this.iceQuantity = convertIceLevelToQuantity(iceLevel);
        this.sugarQuantity = convertSugarLevelToQuantity(sugarLevel);
        this.totalPrice = calculateTotalPrice();
    }

    private int convertIceLevelToQuantity(String iceLevel) {
        switch(iceLevel) {
            case "Extra Ice": return 5;
            case "Regular Ice": return 4;
            case "Light Ice": return 2;
            case "No Ice": return 0;
            default: return 4; // Default to regular ice
        }
    }

    private int convertSugarLevelToQuantity(String sugarLevel) {
        switch(sugarLevel) {
            case "100% Sugar": return 4;
            case "75% Sugar": return 3;
            case "50% Sugar": return 2;
            case "No Sugar": return 0;
            default: return 4; // Default to 100% sugar
        }
    }

    private double calculateTotalPrice() {
        // Base price + $0.50 per topping
        return item.getPrice() + ((toppings.size() - 2)* 0.50);
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getName()).append("\n");
        sb.append("  Ice: ").append(iceLevel).append("\n");
        sb.append("  Sugar: ").append(sugarLevel).append("\n");
        if (!toppings.isEmpty()) {
            sb.append("  Toppings: ").append(String.join(", ", toppings)).append("\n");
        }
        sb.append("  Price: $").append(String.format("%.2f", totalPrice));
        return sb.toString();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getIceQuantity() {
        return iceQuantity;
    }

    public int getSugarQuantity() {
        return sugarQuantity;
    }

    public MenuItem getMenuItem() {
        return item;
    }

    public List<String> getToppings() {
        return toppings;
    }
    
}
