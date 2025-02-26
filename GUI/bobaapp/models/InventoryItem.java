package bobaapp.models;

public class InventoryItem {
    private int ingredientId;
    private String name;
    private int quantity;

    public InventoryItem(int ingredientId, String name, int quantity) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.quantity = quantity;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
