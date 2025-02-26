package bobaapp.models;

public class Modification {
    private int modMenuId;
    private String name;
    private double price;
    private String category; // To categorize modifications (e.g., "Ice", "Sugar", "Toppings")
    
    public Modification(int modMenuId, String name, double price, String category) {
        this.modMenuId = modMenuId;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    
    public int getModMenuId() {
        return modMenuId;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getCategory() {
        return category;
    }
    
    @Override
    public String toString() {
        return name + (price > 0 ? " (+$" + String.format("%.2f", price) + ")" : "");
    }
}
