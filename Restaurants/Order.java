package Restaurants;

import java.util.HashMap;

public class Order {
  private OrderStatus status;
  private HashMap<Item, Integer> items;
  private double totalPrice;

  public Order(Cart cart) {
    items = cart.getItems();
    totalPrice = cart.getTotalPrice();
    status = OrderStatus.ORDERED;
  }

  public OrderStatus getStatus() {
    // Returns the currect order status and
    // moves order to next step (for simulation reasons)

    OrderStatus currStatus = status;

    if (status == OrderStatus.ORDERED)
      status = OrderStatus.PREPARING;
    else if (status == OrderStatus.PREPARING)
      status = OrderStatus.READY;

    return currStatus;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public HashMap<Item, Integer> getItems() {
    return items;
  }

  public void setStatus(OrderStatus _status) {
    // Sets the order status
    status = _status;
  }
}
