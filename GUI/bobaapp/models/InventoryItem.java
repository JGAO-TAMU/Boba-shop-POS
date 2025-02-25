package bobaapp.models;

public class InventoryItem {
    private int ingredientID;
    private String name;
    private int quantity;

    public InventoryItem(int ingredientID, String name, int quantity) {
        this.ingredientID = ingredientID;
        this.name = name;
        this.quantity = quantity;
    }

    public int getIngredientID() { return ingredientID; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
}
