package bobaapp.models;

import java.util.ArrayList;
import java.util.List;

public class CurrentOrder {
    private static CurrentOrder instance;
    private List<OrderItem> items;
    
    private CurrentOrder() {
        items = new ArrayList<>();
    }
    
    public static CurrentOrder getInstance() {
        if (instance == null) {
            instance = new CurrentOrder();
        }
        return instance;
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }
    
    public void clear() {
        items.clear();
    }
}
