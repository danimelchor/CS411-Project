package Restaurants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Restaurant {
  private String name;
  private ArrayList<Item> menu;
  private Queue<Order> orders;
  private boolean isClosed;

  public Restaurant(String _name) {
    name = _name;
    menu = new ArrayList<Item>();
    orders = new LinkedList<Order>();
    isClosed = false;
  }

  public void closeRestaurant() {
    isClosed = true;
  }

  public void openRestaurant() {
    isClosed = false;
  }

  public boolean getIsClosed() {
    return isClosed;
  }

  public String getRestaurantName() {
    return name;
  }

  public void addNewOrder(Order _order) {
    orders.add(_order);
  }

  public void addNewItem(String name, double price) {
    Item newItem = new Item(name, price);
    menu.add(newItem);
  }

  public void addNewItem(Item newItem) {
    menu.add(newItem);
  }

  public void displayMenu() {
    // Displays the restaurant's menu in the following format:
    // [Restaurant name] menu items:
    // X. [Item name] - $[Item price]

    System.out.println(name + " menu items:");
    for (int i = 0; i < menu.size(); i++) {
      Item item = menu.get(i);
      System.out.println(i + ". " + item.getName() + " - $" + item.getPrice());
    }
    System.out.println();
  }

  public Item getItem(int idx) {
    return menu.get(idx);
  }

  public int getNumOfOrders() {
    return orders.size();
  }

  public double getTotalSales() {
    // Adds the price of all orders for this restaurant
    double total = 0;
    for (Order o : orders) {
      total += o.getTotalPrice();
    }
    return total;
  }

  public void setItem(Item i, int itemIdx) {
    menu.set(itemIdx, i);
  }

  public void deleteItem(int itemIdx) {
    menu.remove(itemIdx);
  }
}
