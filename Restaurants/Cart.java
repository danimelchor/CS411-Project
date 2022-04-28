package Restaurants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Cart {
  private HashMap<Item, Integer> items = new HashMap<Item, Integer>();
  private Restaurant restaurant;

  public Cart(Restaurant _restaurant) {
    restaurant = _restaurant;
  }

  public Restaurant getRestaurant() {
    return restaurant;
  }

  public HashMap<Item, Integer> getItems() {
    return items;
  }

  public void addItem(Item item) {
    // Add an item to order or increase quantity if it already exists

    int quantity = 1;
    if (items.containsKey(item)) {
      quantity = items.get(item) + 1;
    }

    items.put(item, quantity);
  }

  public void removeItem(Item item) {
    items.remove(item);
  }

  public double getTotalPrice() {
    // Adds the price of all the items in the cart
    double totalPrice = 0;

    for (Entry<Item, Integer> entry : items.entrySet()) {
      Item item = (Item) entry.getKey();
      int quantity = (int) entry.getValue();
      totalPrice += quantity * item.getPrice();
    }

    return totalPrice;
  }

  public ArrayList<Item> displayItems() {
    // Prints all items in cart in the following format
    // X. [NAME] (QUANTITY)
    ArrayList<Item> orderedItems = new ArrayList<>();

    for (Entry<Item, Integer> entry : items.entrySet()) {
      Item item = (Item) entry.getKey();
      int quantity = (int) entry.getValue();

      int idx = orderedItems.size();
      orderedItems.add(item);
      System.out.println(idx + ". " + item.getName() + " (" + quantity + ")");
    }
    System.out.println();
    return orderedItems;
  }

  public void editItem(Item item, int newQuantity) {
    // Edits the item quantity or removes if 0
    if (newQuantity > 0)
      items.put(item, newQuantity);
    else
      removeItem(item);
  }
}
