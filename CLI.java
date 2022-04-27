import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import Database.Database;

import Restaurants.Cart;
import Restaurants.Item;
import Restaurants.Order;
import Restaurants.OrderStatus;
import Restaurants.Restaurant;
import Users.RestaurantOwner;
import Users.BasicUser;
import Users.Client;
import Users.MallAdmin;

public class CLI {
  Database db = new Database();
  private int path = 0;

  public static final String WHITE = "\u001B[0m";
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";

  // Either one should have a value if logged in
  BasicUser user;

  // If a client starts a new cart or order
  Cart cart;
  Order order;

  // List of commands
  String[] commands = {
      "--- MAIN PAGE ---\n 1. Sign in\n 2. Register\n 3. Exit\n-----------------",
      "--- CLIENT PAGE ---\n 1. Show restaurants\n 2. Start a new order\n 3. Show items in cart\n 4. Add item to order\n 5. Edit item in order\n 6. Checkout & Pay\n 7. Check order status\n 8. Log out\n-------------------",
      "--- RESTAURANT OWNER PAGE ---\n 1. Show restaurant menu\n 2. Add item to menu\n 3. Edit item in menu\n 4. Delete Item \n 5. Show restaurant state\n 6. Change restaurant state\n 7. Log out\n-----------------------------",
      "--- ADMIN PAGE ---\n 1. Get restaurant sales and statistics\n 2. Log out\n-----------------------------"
  };

  public static void printError(String err) {
    System.out.println(RED + err + WHITE);
  }

  public static void printSuccess(String text) {
    System.out.println(GREEN + text + WHITE);
  }

  public static void printBold(String text) {
    System.out.println("\033[1m" + text + "\033[0m");
  }

  public void logout() {
    path = 0;
    user = null;
    cart = null;
    order = null;
  }

  public boolean mainCmds(int cmd, Scanner scanner) {
    boolean shouldExit = false;

    switch (cmd) {
      case 1:
        // Get username, password and if its a restaurant owner
        System.out.print("Username: ");
        String signInUserName = scanner.next();
        System.out.print("Password: ");
        String signInPassword = scanner.next();
        System.out.print("Are you a restaurant owner [y/n]: ");
        boolean signInIsRestaurantOwner = scanner.next().equals("y");

        if (!signInIsRestaurantOwner) {
          System.out.print("Are you an admin? [y/n]: ");
          boolean signInIsAdmin = scanner.next().equals("y");
          if (!signInIsAdmin) {
            // If it is a client, authenticate
            user = db.authenticateClient(signInUserName, signInPassword);
            path = 1;
          } else {
            // If it is an admin, authenticate
            user = db.authenticateAdmin(signInUserName, signInPassword);
            path = 3;
          }
        } else {
          // If it is a restaurant owner, authenticate
          user = db.authenticateRestaurantOwner(signInUserName, signInPassword);
          path = 2;
        }

        if (user == null) {
          printError("Login details are incorrect, try again.");
          path = 0;

        } else {
          // Print success message and navigate to correct path
          printSuccess("Successfully logged in as " + signInUserName);
        }
        break;
      case 2:
        // Get username, password and if its a restaurant owner
        System.out.print("Username: ");
        String registerUserName = scanner.next();
        System.out.print("Password: ");
        String registerPassword = scanner.next();
        System.out.print("Are you a restaurant owner [y/n]: ");
        boolean registerIsRestaurantOwner = scanner.next().equals("y");

        boolean success;

        if (!registerIsRestaurantOwner) {
          System.out.print("Are you an admin? [y/n]: ");
          boolean registerIsAdmin = scanner.next().equals("y");

          if (!registerIsAdmin) {

            Client newClient = new Client(registerUserName, registerPassword);
            success = db.addClient(registerUserName, newClient);
            user = newClient;
            // Move to clients page
            path = 1;

          } else {
            MallAdmin newAdmin = new MallAdmin(registerUserName, registerPassword);
            success = db.addAdmin(registerUserName, newAdmin);
            user = newAdmin;
            // Move to admins page
            path = 3;
          }
        } else {
          // Get restaurants name
          System.out.print("What is the name of your restaurant?: ");
          String registerRestaurantName = scanner.next();

          // Grab restaurant by name
          Restaurant restaurant = new Restaurant(registerRestaurantName);

          // Register
          RestaurantOwner newRestaurantOwner = new RestaurantOwner(registerUserName, registerPassword, restaurant);
          user = newRestaurantOwner;
          success = db.addOwner(registerUserName, newRestaurantOwner);
          db.addRestaurant(registerRestaurantName, restaurant);

          // Move to restaurant owners page
          path = 2;
        }

        if (success)
          printSuccess("Successfully registered with username '" + registerUserName + "'");
        else {
          path = 0;
          logout();
          printError("A user already exists with username '" + registerUserName + "'");
        }
        break;
      case 3:
        shouldExit = true;
        break;
    }

    return shouldExit;
  }

  public void userCommands(int cmd, Scanner scanner) {
    /*
     * 1. Show restaurants
     * 2. Start a new order
     * 3. Add item to order
     * 5. Edit item in order
     * 6. Checkout & pay
     * 7. Check order status
     * 8. Log out
     */
    switch (cmd) {
      case 1:
        ArrayList<String> restaurantNames = db.getAllRestaurantNames();
        printBold("Restaurant list:");
        for (int i = 0; i < restaurantNames.size(); i++) {
          String rName = restaurantNames.get(i);
          Restaurant r = db.getRestaurantByName(rName);
          String state = r.getIsClosed() ? "closed" : "open";
          String color = r.getIsClosed() ? RED : GREEN;
          System.out.println(color + i + ". " + restaurantNames.get(i) + " [" + state + "]" + WHITE);
        }
        break;
      case 2:
        System.out.print("Choose a restaurant: ");
        int newOrderRestaurantId = scanner.nextInt();
        String newOrderRestaurantName = db.getAllRestaurantNames().get(newOrderRestaurantId);
        Restaurant newOrderRestaurant = db.getRestaurantByName(newOrderRestaurantName);

        if (newOrderRestaurant.getIsClosed()) {
          printError("Sorry! " + newOrderRestaurantName + "is currently closed.");
        } else {
          cart = new Cart(newOrderRestaurant);
          printSuccess("Successfully started a new cart for " + newOrderRestaurantName);
        }

        break;
      case 3:
        if (cart == null) {
          printError("You haven't started an order yet!");
        } else {
          printBold("Your cart:");
          cart.displayItems();
        }
        break;
      case 4:
        if (cart == null) {
          printError("You haven't started an order yet!");
        } else {
          Restaurant addItemToOrderRestaurant = cart.getRestaurant();
          addItemToOrderRestaurant.displayMenu();
          System.out.print("Choose an item to add to your order: ");
          int addItemToOrderItemIdx = scanner.nextInt();
          Item addItemToOrderItem = addItemToOrderRestaurant.getItem(addItemToOrderItemIdx);
          cart.addItem(addItemToOrderItem);
          printSuccess("Successfully added " + addItemToOrderItem.getName() + " to order");
          printSuccess("New price " + cart.getTotalPrice() + "$");
        }
        break;
      case 5:
        if (cart == null) {
          printError("You haven't started an order yet!");
        } else {
          printBold("Items in cart:");
          ArrayList<Item> editItemsCart = cart.displayItems();
          System.out.print("Choose an item to edit: ");
          int editItemIdx = scanner.nextInt();
          System.out.print("Choose the new quantity (0 deletes it): ");
          int editItemNewQuantity = scanner.nextInt();

          Item editItemItem = editItemsCart.get(editItemIdx);
          cart.editItem(editItemItem, editItemNewQuantity);
          printSuccess("Successfully edited your cart");
          printSuccess("New price " + cart.getTotalPrice() + "$");
        }
        break;
      case 6:
        if (cart == null) {
          printError("You haven't started an order yet!");
        } else {
          printBold("Checkout:");

          // Get card details
          System.out.print("Enter your card number: ");
          scanner.next(); // We dont actually need these details
          System.out.print("Enter your card expiry date: ");
          scanner.next(); // We dont actually need these details
          System.out.print("Enter your card CVV: ");
          scanner.next(); // We dont actually need these details

          order = new Order(cart);
          Restaurant placeOrderRestaurant = cart.getRestaurant();
          placeOrderRestaurant.addNewOrder(order);
          printSuccess("Successfully placed your order in position #" + placeOrderRestaurant.getNumOfOrders());
          order = null;
          cart = null;
        }
        break;
      case 7:
        if (order == null) {
          printError("You need to checkout first!");
        } else {
          OrderStatus checkStatusStatus = order.getStatus();
          if (checkStatusStatus == OrderStatus.ORDERED)
            printSuccess("Your order was just ordered");
          else if (checkStatusStatus == OrderStatus.PREPARING)
            printSuccess("Your order is being prepared");
          else
            printSuccess("Your order is ready");
        }
        break;
      case 8:
        logout();
        break;
    }

  }

  public void restaurantOwnerCommands(int cmd, Scanner scanner) {
    /*
     * 1. Show restaurant menu
     * 2. Add item to menu
     * 3. Edit item in menu
     * 4. Delete item
     * 5. Show restaurant state
     * 6. Change restaurant state
     * 7. Log out
     */
    RestaurantOwner owner = (RestaurantOwner) user;
    Restaurant r = owner.getRestaurant();
    switch (cmd) {
      case 1:
        /* Show Restaurant menu */
        r.displayMenu();

        path = 2;
        break;
      case 2:
        /* Add item to menu */
        System.out.println("Name of item to add: ");
        String itemName = scanner.next();
        double itemPrice;
        
        System.out.println("Price of item: ");
        itemPrice = scanner.nextDouble();
         
        Item i = new Item(itemName, itemPrice);
        r.addNewItem(i);

        r.displayMenu();
        printSuccess("Item added!");
        break;
      case 3:
        /* edit item in menu */
        r.displayMenu();
        System.out.println("\nID of menu item to edit: ");
        int id = scanner.nextInt();

        while(id > r.getMenuLength() -1 || id < 0){
          System.out.println("Not a valid item ID, try again");
          id = scanner.nextInt();
        }

        System.out.println("New name: ");
        itemName = scanner.next();
        System.out.println("New price: ");
        itemPrice = scanner.nextInt();

        Item newItem = new Item(itemName, itemPrice);
        r.setItem(newItem, id);

        r.displayMenu();
        printSuccess("Item succesfully changed!");
        break;
      case 4:
        /* delete item */
        r.displayMenu();
        System.out.println("\nID of menu item to delete: ");
        id = scanner.nextInt();

        while(id > r.getMenuLength() -1 || id < 0){
            System.out.println("Not a valid item ID, try again");
            id = scanner.nextInt();
        }
        r.deleteItem(id);

        r.displayMenu();
        printSuccess("Item sucessfully deleted!");
        break;
      case 5:
        /* Show restaurant state */
        String state = r.getIsClosed() ? "closed" : "open";
        String color = r.getIsClosed() ? RED : GREEN;
        System.out.println(color + r.getRestaurantName() + " is currently " + state + WHITE);
        break;
      case 6:
        /* Change restaurant state */
        System.out.print("Do you want to open or close your restaurant? [open/close]: ");
        String newState = scanner.next();
        if (newState.equals("close"))
          r.closeRestaurant();
        else
          r.openRestaurant();

        String newStateVerb = r.getIsClosed() ? "closed" : "opened";
        printSuccess("Successfully " + newStateVerb + " " + r.getRestaurantName());
        break;
      case 7:
        /* Log out */
        logout();
        break;
    }
  }

  public void mallAdminCommands(int cmd, Scanner scanner) {
    /*
     * 1. Get sales statistics
     * 2. Log out
     */

    switch (cmd) {
      case 1:
        ArrayList<String> restaurantNames = db.getAllRestaurantNames();
        printBold("Restaurant\tTotal Orders\t  Total Sales");

        for (int i = 0; i < restaurantNames.size(); i++) {
          String rName = restaurantNames.get(i);
          Restaurant r = db.getRestaurantByName(rName);

          System.out.println(rName + "\t\t" + r.getNumOfOrders() + "\t\t$" + r.getTotalSales());
        }
        break;
      case 2:
        logout();
        break;
    }
  }

  public static void prepopulate(CLI cli) {
    /*
     * PREPOPULATE OUR DATABASE WITH EXAMPLE USERS
     */
    Database db = cli.db;

    // Restaurant owners
    String[][] restaurantOwners = {
        { "owner1", "1234", "Basho Express" },
        { "owner2", "1234", "Open Kitchen" },
        { "owner3", "1234", "McDonalds" }
    };

    String[][] menuItems = {
        { "Sushi Bowl", "Sushi Burrito", "Salad Bowl", "California Rolls" },
        { "3 Chicken Finger Basket", "Packards Corner Sandwich", "5 Finger Orange Ya Happy" },
        { "Happy Meal", "4 Piece Chicken McNuggets", "Big Mac", "Double Hamburger", "McDouble" }
    };

    double[][] menuPrices = {
        { 15.99, 8.49, 12.49, 13.29 },
        { 4.99, 12.90, 9.29 },
        { 6.99, 7.49, 7.49, 8.29, 11.55 },
    };

    for (int i = 0; i < restaurantOwners.length; i++) {
      String[] u = restaurantOwners[i];

      Restaurant restaurant = new Restaurant(u[2]);
      RestaurantOwner newRestaurantOwner = new RestaurantOwner(u[0], u[1], restaurant);
      db.addOwner(u[0], newRestaurantOwner);
      db.addRestaurant(u[2], restaurant);

      if (i == 2)
        restaurant.closeRestaurant();

      String[] itemNames = menuItems[i];
      double[] itemPrices = menuPrices[i];
      for (int j = 0; j < itemNames.length; j++) {
        restaurant.addNewItem(itemNames[j], itemPrices[j]);
      }
    }

    // Mall admins
    String[][] mallAdmins = {
        { "admin1", "1234" },
        { "admin2", "1234" }
    };

    for (int i = 0; i < mallAdmins.length; i++) {
      String[] u = mallAdmins[i];
      MallAdmin newAdmin = new MallAdmin(u[0], u[1]);
      db.addAdmin(u[0], newAdmin);
    }
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    CLI cli = new CLI();
    boolean shouldExit = false;
    prepopulate(cli);

    while (!shouldExit) {
      System.out.println(cli.commands[cli.path]);
      System.out.print("Select an option (number): ");
      int cmd = scanner.nextInt();
      System.out.println();
      switch (cli.path) {
        case 0: // run it back
          shouldExit = cli.mainCmds(cmd, scanner);
          break;
        case 1: // client
          cli.userCommands(cmd, scanner);
          break;
        case 2: // owner
          cli.restaurantOwnerCommands(cmd, scanner);
          break;
        case 3: // admin
          cli.mallAdminCommands(cmd, scanner);
          break;
      }

      System.out.println();
      System.out.println("------------------------------");
      System.out.println();

    }

    scanner.close();
  }
}
