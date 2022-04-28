package Restaurants;

/**
 * Item
 */
public class Item {
  private String name;
  private double price;

  public Item(String _name, double _price) {
    name = _name;
    price = _price;
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double _price) {
    // Sets the item price if not negative or zero
    if (_price > 0) {
      price = _price;
    }
  }
}