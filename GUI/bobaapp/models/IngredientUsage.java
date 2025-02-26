package bobaapp.models;

public class IngredientUsage {
    private int ingredientId;
    private int quantityUsed;
    
    public IngredientUsage(int ingredientId, int quantityUsed) {
        this.ingredientId = ingredientId;
        this.quantityUsed = quantityUsed;
    }
    
    public int getIngredientId() {
        return ingredientId;
    }
    
    public int getQuantityUsed() {
        return quantityUsed;
    }
}
