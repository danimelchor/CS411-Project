package Database;

import java.util.ArrayList;
import java.util.HashMap;
import Users.RestaurantOwner;
import Users.Client;
import Users.MallAdmin;
import Restaurants.Restaurant;

public class Database {
  public HashMap<String, RestaurantOwner> ownerDatabase = new HashMap<>();
  public HashMap<String, Client> clientDatabase = new HashMap<>();
  public HashMap<String, Restaurant> restaurantDatabase = new HashMap<>();
  public HashMap<String, MallAdmin> adminDatabase = new HashMap<>();

  // GET METHODS
  public Client getClientByUserName(String userName) {
    return clientDatabase.get(userName);
  }

  public MallAdmin getAdminByUserName(String userName) {
    return adminDatabase.get(userName);
  }

  public Restaurant getRestaurantByName(String restName) {
    return restaurantDatabase.get(restName);
  }

  public RestaurantOwner getRestaurantOwnerByName(String userName) {
    return ownerDatabase.get(userName);
  }

  // ADD METHODS
  public boolean addClient(String userName, Client clientObject) {
    // if user is not in database, add user
    if (!clientDatabase.containsKey(userName)) {
      clientDatabase.put(userName, clientObject);
      return true;
    }
    // otherwise, skip
    else {
      return false;
    }
  }

  public boolean addAdmin(String userName, MallAdmin adminObject) {
    // if user is not in database, add user
    if (!adminDatabase.containsKey(userName)) {
      adminDatabase.put(userName, adminObject);
      return true;
    }
    // otherwise, skip
    else {
      return false;
    }
  }

  public boolean addOwner(String userName, RestaurantOwner ownerObject) {
    // if owner is not in database, add owner
    if (!ownerDatabase.containsKey(userName)) {
      ownerDatabase.put(userName, ownerObject);
      return true;
    }
    // otherwise, skip
    return false;
  }

  public boolean addRestaurant(String restaurantName, Restaurant restaurantObject) {
    // if restaurant is not in the database, add it
    if (!restaurantDatabase.containsKey(restaurantName)) {
      restaurantDatabase.put(restaurantName, restaurantObject);
      return true;
    }
    // otherwise, skip
    else {
      return false;
    }
  }

  public ArrayList<String> getAllRestaurantNames() {
    // Store every restaurant name in an indexable way for listing

    ArrayList<String> result = new ArrayList<String>();
    
    for (String key : restaurantDatabase.keySet()) {
      result.add(key);
    }

    return result;
  }

  // AUTH METHODS
  public Client authenticateClient(String userName, String password) {
    Client clientObject = getClientByUserName(userName);

    if (clientObject != null && clientObject.getUserName().equals(userName)
        && clientObject.getPassword().equals(password)) {
      return clientObject;
    }
    return null;
  }

  public RestaurantOwner authenticateRestaurantOwner(String userName, String password) {
    RestaurantOwner ownerObject = getRestaurantOwnerByName(userName);

    if (ownerObject != null && ownerObject.getUserName().equals(userName)
        && ownerObject.getPassword().equals(password)) {
      return ownerObject;
    }
    return null;
  }

  public MallAdmin authenticateAdmin(String userName, String password) {
    MallAdmin adminObject = getAdminByUserName(userName);
    if (adminObject != null && adminObject.getUserName().equals(userName)
        && adminObject.getPassword().equals(password)) {
      return adminObject;
    }
    return null;

  }

  // TESTING METHODS
  public String adminDatabaseToString() {
    String res = "{";
    for (String k : adminDatabase.keySet()) {
      MallAdmin a = adminDatabase.get(k);
      res += k + "=" + a.getUserName() + "-" + a.getPassword() + ",";
    }
    res += "}";
    return res;
  }

  public String clientDatabaseToString() {
    String res = "{";
    for (String k : clientDatabase.keySet()) {
      Client c = clientDatabase.get(k);
      res += k + "=" + c.getUserName() + "-" + c.getPassword() + ",";
    }
    res += "}";
    return res;
  }

  public String restaurantDatabaseToString() {
    String res = "{";
    for (String k : restaurantDatabase.keySet()) {
      Restaurant r = restaurantDatabase.get(k);
      res += k + "=" + r.getRestaurantName() + "-" + r.getIsClosed() + "-" + r.getNumOfOrders() + ",";
    }
    res += "}";
    return res;
  }

  public String ownerDatabaseToString() {
    String res = "{";
    for (String k : ownerDatabase.keySet()) {
      RestaurantOwner r = ownerDatabase.get(k);
      res += k + "=" + r.getUserName() + "-" + r.getPassword() + "-" + r.getRestaurant() + ",";
    }
    res += "}";
    return res;
  }
}
