package mushop.carts.entities;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;
import java.util.UUID;

@Introspected
public class Item {

    private String id;

    private String itemId;

    private int quantity;

    private BigDecimal unitPrice;

    public Item() {
        id = UUID.randomUUID().toString();
        quantity = 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Item [id=" + id +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                "]";
    }
}
