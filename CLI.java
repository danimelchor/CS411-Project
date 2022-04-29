import java.util.ArrayList;
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

import java.io.File;
import java.io.FileNotFoundException;

public class CLI {
  Database db = new Database();
  private int path = 0;

  public static final String WHITE = "\u001B[0m";
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";

  // For testing
  boolean test = false;
  boolean errorHappened = false;

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

  public void printError(String err) {
    System.out.println(RED + err + WHITE);

    if(test) errorHappened = true;
  }

  public void printSuccess(String text) {
    System.out.println(GREEN + text + WHITE);
  }

  public void printBold(String text) {
    System.out.println("\033[1m" + text + "\033[0m");
  }

  public void isTest(boolean it) {
    test = it;
  }

  public void logout() {
    path = 0;
    user = null;
    cart = null;
    order = null;
  }

  public boolean mainCommands(int cmd, CustomScanner scanner) {
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

  public void userCommands(int cmd, CustomScanner scanner) {
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

  public void restaurantOwnerCommands(int cmd, CustomScanner scanner) {
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
        System.out.println("Price of item: ");
        double itemPrice = scanner.nextDouble();

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

  public void mallAdminCommands(int cmd, CustomScanner scanner) {
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
      // For every restaurant owner
      String[] u = restaurantOwners[i];

      // Create the restaurant and store in DB
      Restaurant restaurant = new Restaurant(u[2]);
      RestaurantOwner newRestaurantOwner = new RestaurantOwner(u[0], u[1], restaurant);
      db.addOwner(u[0], newRestaurantOwner);
      db.addRestaurant(u[2], restaurant);

      // Just to have a closed restaurant
      if (i == 2)
        restaurant.closeRestaurant();

      // Add items to restaurant
      String[] itemNames = menuItems[i];
      double[] itemPrices = menuPrices[i];
      for (int j = 0; j < itemNames.length; j++) {
        restaurant.addNewItem(itemNames[j], itemPrices[j]);
      }
    }
    
    // Add mall admin
    MallAdmin newAdmin = new MallAdmin("admin", "1234");
    db.addAdmin("admin", newAdmin);
  }

  public static void run(CLI cli, CustomScanner scanner) {
    boolean shouldExit = false;
    prepopulate(cli);

    while (!shouldExit) {
      System.out.println(cli.commands[cli.path]);
      System.out.print("Select an option (number): ");
      int cmd = scanner.nextInt();
      System.out.println();
      
      switch (cli.path) {
        case 0: // run it back
          shouldExit = cli.mainCommands(cmd, scanner);
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
  }
  public static void main(String[] args) {
    // Run program on test or normal mode
    Scanner scanner = new Scanner(System.in);
    System.out.print("Is this a test [y/n]: ");
    String answer = scanner.next();

    CLI cli = new CLI();

    // Custom scanner
    CustomScanner customScanner;
    boolean isTest = answer.equals("y");

    // For tests
    ArrayList<String> inputLines = new ArrayList<String>();
    ArrayList<String> outputLines = new ArrayList<String>();


    if(!isTest) {
      customScanner = new CustomScanner(scanner);
    } else {
      // Display possible tests
      File folder = new File("../testcases/");
      File[] listOfFiles = folder.listFiles();
      System.out.println();
      System.out.println("Tests available:");
      for (File f : listOfFiles) {
        System.out.println(" - " + f.getName());
      }
      System.out.println();


      // Get test name
      System.out.print("What is the test name? ");
      String testName = scanner.next();
      scanner.close();

      // Read test files
      String inputFileName = "../testcases/" + testName + "/input.txt";
      String outputFileName = "../testcases/" + testName + "/output.txt";

      try {
        // Read input test file
        Scanner inputScanner = new Scanner(new File(inputFileName));
        while (inputScanner.hasNextLine()) inputLines.add(inputScanner.nextLine());
        inputScanner.close();

        // Read output test file
        Scanner outputScanner = new Scanner(new File(outputFileName));
        while (outputScanner.hasNextLine()) outputLines.add(outputScanner.nextLine());
        outputScanner.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      
      customScanner = new CustomScanner(inputLines);
      cli.isTest(true);
    }

    try {
      run(cli, customScanner);
    } catch(IndexOutOfBoundsException e) {
      if(isTest) {
        // Capture expected db output
        ArrayList<String> testOutputLines = new ArrayList<String>();
        testOutputLines.add(cli.errorHappened ? "true" : "false");
        testOutputLines.add(cli.db.ownerDatabaseToString());
        testOutputLines.add(cli.db.clientDatabaseToString());
        testOutputLines.add(cli.db.restaurantDatabaseToString());
        testOutputLines.add(cli.db.adminDatabaseToString());

        System.out.println();

        // Get if we are expecting an error and if it happened
        boolean expectedError = outputLines.get(0).equals("true");
        boolean errorOcurred = testOutputLines.get(0).equals("true");

        // Check if output is the same
        boolean testPassed = true;
        for (int i = 1; i < testOutputLines.size(); i++) {
          if(!testOutputLines.get(i).equals(outputLines.get(i))) {
            testPassed = false;
            break;
          }
        }

        // Make space
        for(int i = 0; i < 40; i++) System.out.println();

        // Display results
        if(testPassed && errorOcurred == expectedError) cli.printSuccess("Test passed");
        else if(!testPassed && errorOcurred && expectedError) cli.printSuccess("Test passed");
        else if (errorOcurred != expectedError) {
          cli.printError("Test failed");
          cli.printError("A runtime error ocurred.");
        }
        else {
          cli.printError("Test failed");
          System.out.println();
          cli.printError("** OUTPUT **");
          for (String s : testOutputLines) {
            cli.printError(s);
          }
          System.out.println();
          cli.printError("** EXPECTED OUTPUT **");
          for (String s : outputLines) {
            cli.printError(s);
          }
        }
      }
    } catch (Exception e) {
      // Unexpected runtime error ocurred

      // Make space
      for(int i = 0; i < 40; i++) System.out.println();
      
      cli.printError("Test failed");
      cli.printError("A runtime error ocurred.");
      cli.printError(e.toString());
    }
  }
}
