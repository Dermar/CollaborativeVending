package SOFT2412.A2;

import java.time.LocalDateTime;
import java.util.*;
import java.io.*;
import java.lang.NumberFormatException;

public class UserInterface {
    public static Scanner scan = new Scanner(System.in);
    public static VendingMachine vm = new VendingMachine();
    // Current User (null if guest user)
    public static User currentUser = new Customer("", "", "");
    // HashMap of all valid commands and their brief description
    public static final Map<String, String> allCommandBriefs = new HashMap<String, String>() {{
        put("buy", "Allows any user to buy a product from the vending machine.");
        put("signup", "Allows any user to create an account for the machine.");
        put("login", "Allows any user to login to their account in the machine.");
        put("logout", "Allows any user to log out of their account.");
        put("help", "Gives information on how to use the application.");
        put("menu", "Shows you everything you can buy in the vending machine.");
        put("exit", "Exits the application.");
    }};
    public static final Map<String, String> cashierCommandBriefs = new HashMap<String, String>(){{
        put("editChange", "Allows you to modify the number of notes/coins in the vending machine. Don't steal! ;)");
        put("summaryChange", "A summary of all the money inside the vending machine.");
        put("summarySuccessful", "A summary of all the transactions made, with the time that they were made.");
    }};

    public static final Map<String, String> sellerCommandBriefs = new HashMap<String, String>(){{
        put("editItems", "Allows you to edit any quality of any item in the vending machine.");
        put("summaryItems", "A list of the current available items.");
        put("summaryQuantities", "A summary of the item codes, names, and quantities of all sold items.");
    }};

    public static final Map<String, String> ownerCommandBriefs = new HashMap<String, String>(){{
        putAll(cashierCommandBriefs);
        putAll(sellerCommandBriefs);
        put("addEmployee", "Allows you to add a seller or cashier account for your employee.");
        put("removeEmployee", "Allows you to remove a seller or cashier. Also saves you an awkward conversation.");
        put("summaryUsers", "Shows you a list of all the users in the vending machine.");
        put("summaryCancelled", "Shows you a list of all the cancelled transactions.");
    }};

    // HashMap of all valid commands and their usage
    public static final Map<String, String> allCommandUsage = new HashMap<String, String>() {{
        put("buy", "\nAllows any user to buy a product from the vending machine. Logged in users can save a card to their account when used.\n" +
        "\nUsage: buy <payment method> <amount> <product code> [currency]\n" +
        "<payment method> -> card or cash\n<amount>         -> amount of the product\n" +
        "<product code>   -> code of the desired product\n[currency]       -> Currency denomination of given payment (Optional argument given only when paying by cash) OR username and pin of user when paying by card\n" +
        "\nExample of usage: buy cash 4 se $5*2 50c*5\n" +
                "Example of usage: buy card 1 mm cardname 111111\n");
        // add in help for paying by card
        put("sell", "Allows a vending machine owner to sell a product.");
        put("signup", "Allows a vending machine owner to sell a product.\n" +
        "Usage: signup <type> <name> <username> <password>\n" +
        "<type>     -> type of user (cashier, customer, owner, seller)\n" +
        "<name>     -> name of the user\n" +
        "<username> -> username of the user, has to be unique\n" +
        "<password> -> password of the user\n" +
        "\nExample of usage: signup customer John myusername mypassword\n");
        put("login", "\nAllows any user to login to their account in the machine.\n" +
        "\nUsage: login <username> <password>\n" +
        "<username> -> username registered upon signup\n" +
        "<password> -> password registered upon signup\n" +
        "\nExample of usage: login myusername mypassword\n");
        put("logout", "\nAllows any user to log out of their account.\n" +
        "\nUsage: logout\n");
        put("help", "\nIf you got this far, you already know how to use the help command!\n" +
        "\nUsage: help <command>\n" + "\nExample of usage: help buy\n");
        put("exit", "\nExits the application.\n" +
        "\nUsage: exit\n");
    }};

    public static final Map<String, String> cashierCommandUsage = new HashMap<String, String>(){{
        put("editChange", "\nAllows you to modify the number of notes/coins in the vending machine. Don't steal! ;)\n" +
                "Usage: editChange <cashAmount> <quantity>\n" +
                "\n<cashAmount>  -> Australian cash amounts, with dollars represented as $num and cents as numc e.g $20 and 20c" +
                "\n<quantity>    -> number of this cash value that you want there to be" +"\nExample of usage: editChange $50 10\n");
        put("summaryChange", "\nA summary of all the money inside the vending machine.\nUsage: summaryChange\n");
        put("summarySuccessful", "\nA summary of all the transactions made, with the time that they were made.\nUsage: summarySuccessful\n");
    }};
    public static final Map<String, String> sellerCommandUsage = new HashMap<String, String>(){{
        put("editItems", "\nAllows you to edit any attribute of any item in the vending machine. \nUsage: editItems <change type> <item code> <new value>\n" +
                "<change type>  -> the type of change you want to make to the item. You can change the item's \"name\", \"category\", \"code\", \"price\", or \"quantity\".\n" +
                "<item code>    -> the name of the item that you want to edit.\n" +
                "<new value>    -> the new value that you want this item to have now. If you're changing the price, please just place\n" +
                "Example of usage: editItems name M&M R&R");
        put("summaryItems", "\nA list of the current available items.\nUsage: summaryItems");
        put("summaryQuantities", "\nA summary of the item codes, names, and quantities of all sold items.\n Usage: summaryQuantities\n");
    }};
    public static final Map<String, String> ownerCommandUsage = new HashMap<String, String>(){{
        putAll(sellerCommandUsage);
        putAll(cashierCommandUsage);
        put("summaryUsers", "\nShows you a list of all the users in the vending machine.\nUsage: summaryUsers\n");
        put("summaryCancelled", "\nShows you a list of all the cancelled transactions.\nUsage: summaryCancelled\n");
        put("addEmployee", "\nAllows you to add a seller or cashier account for your employee." +
                "\nUsage: addEmployee <type> <name> <username> <password>\n" +
                "<type>     -> type of user (cashier, seller)\n" +
                "<name>     -> name of the user\n" +
                "<username> -> your employee's username to be registered upon signup\n" +
                "<password> -> your employee's password to be registered upon signup\n" +
                "\nExample of usage: addEmployee cashier Namie sampleusername samplepassword\n");
        put("removeEmployee", "\n\"Allows you to remove a seller or cashier. Also saves you an awkward conversation.\nUsage: removeEmployee <username>" +
                "\n<username>  -> the username of the cashier or seller you want to remove\n" + "Example of usage: removeEmployee FrankieFlew\n");

    }};

    public void buy(List<String> input){
        if (!validateInput(input)) {
            // Record the cancelled transaction
            Transaction t = new Transaction(UserInterface.currentUser.getName(), LocalDateTime.now(), "Cancelled due to incorrect user input");
            Transaction.writeTransaction(t);
            System.out.println("\nWe apologise. Please check that was the correct format. Type 'help buy' for help or 'exit' to quit the program.");
            return;
        }

        // Call user into a loop of buying items until they exit
        if (input.get(0).equals("cash")){
            String cashInput = "";
            // Find the string that includes the cash input
            for (int i = 3; i < input.size() ; i++){
                cashInput += (input.get(i) + " ");
            }
            if (cashInput.length() > 0) {
                cashInput = cashInput.substring(0, cashInput.length() - 1);
            }

            // Try to process their transaction
            try{
                String successful = vm.payByCash(Integer.valueOf(input.get(1)), input.get(2), cashInput);
                System.out.println(successful);

                String[] result = successful.split("\n");
                String paid = result[1].split(": \\$")[1];
                String change = result[3].split(": \\$")[1];
                // Record the successful transaction:
                Transaction t = new Transaction(UserInterface.currentUser.getName(), vm.searchByItemCode(input.get(2)), LocalDateTime.now(), Double.parseDouble(paid), Double.parseDouble(change), "cash", "Successful");
                Transaction.writeTransaction(t);

            }
            // If the customer has not given enough money
            catch(ArithmeticException ae){
                System.out.print("\nApologies, but that is not enough for your purchase. Please check your input.");
                double toPay = vm.calculateToPay(input.get(2), Integer.parseInt(input.get(1)));
                System.out.print(" You are to pay $" + String.format("%.2f",toPay) + ".");

                // Record the cancelled transaction
                Transaction t = new Transaction(UserInterface.currentUser.getName(), LocalDateTime.now(), "Cancelled due to insufficient payment");
                Transaction.writeTransaction(t);

                System.out.println("\nReinput your payment type, item code, quantity, and cash input in that order to continue payment. Otherwise input 'exit' to cancel your transaction.");
                return;
            }
            // If the machine can't give the right change
            catch(IllegalStateException is){
                System.out.println("\nSincere apologies. We do not have enough change to pay you back your change at this time. Please either reinput your payment or press 'exit' to cancel your transaction.");
                // Record the cancelled transaction
                Transaction t = new Transaction(UserInterface.currentUser.getName(), LocalDateTime.now(), "Cancelled due to insufficient change in vending machine");
                Transaction.writeTransaction(t);
                return;
            }
            // If the machine doesn't have enough stock for the purchase
            catch(NoSuchElementException ne){
                System.out.println("\nSincere apologies. We do not have enough stock to accommodate that purchase. Please either reinput your quantity or press 'exit' to quit the program.");
                // Record the cancelled transaction
                Transaction t = new Transaction(UserInterface.currentUser.getName(), LocalDateTime.now(), "Cancelled due to insufficient stock");
                Transaction.writeTransaction(t);
                return;
            }
        }

        if (input.get(0).equals("card")) {
            String itemQuantity = input.get(1);
            String itemCode = input.get(2);
            // Checking if card details match for anonymous users
            if (currentUser.getName().equals("")) {
                String cardName = input.get(3);
                String cardNumber = input.get(4);
                if (!Card.checkCardDetails(cardName, cardNumber)) {
                    System.out.println("\nWe were unable to match your card, please try again. You can add a new card by logging in.");
                    Transaction t = new Transaction(UserInterface.currentUser.getName(), LocalDateTime.now(), "Cancelled due to unmatched card details");
                    Transaction.writeTransaction(t);
                    return;
                }
            }
            // Check that we have enough stock for the purchase
            if (!vm.checkStock(vm.searchByItemCode(itemCode), Integer.parseInt(itemQuantity))) {
                // Record the cancelled transaction
                Transaction t = new Transaction(UserInterface.currentUser.getName(), LocalDateTime.now(), "Cancelled due to insufficient stock");
                System.out.println("\nSincere apologies. We do not have enough stock to accommodate that purchase. Please either reinput your quantity or press 'exit' to quit the program.");
                return;
            }
            System.out.println(vm.payByCard(Integer.parseInt(itemQuantity), itemCode));
            // Record the successful transaction:
            Transaction t = new Transaction(UserInterface.currentUser.getName(), vm.searchByItemCode(itemCode), LocalDateTime.now(), vm.calculateToPay(itemCode, Integer.parseInt(itemQuantity)), 0.0, "Card", "Successful");
            Transaction.writeTransaction(t);
            if ((!currentUser.getName().equals("")) && (currentUser.getCard() == null)) {
                String cardName = input.get(3);
                String cardNumber = input.get(4);
                System.out.println("\nWould you like to save these card details to your account? Input 'yes' or 'no' to continue.");
                while (true) {
                    String saveCard = null;
                    try {
                        saveCard = App.readLine();
                    }
                    catch(InterruptedException ie) { }
                    if (saveCard == null) {
                        App.menu();
                        return;
                    }
                    if (saveCard.equals("yes")) {
                        Card userCard = new Card(cardName, cardNumber);
                        Card.updateCards(userCard);
                        User.addCard(UserInterface.currentUser, userCard);
                        System.out.println("Card details were successfully saved to your account! You no longer have to input card details for your purchases, they will be implemented automatically when you pay by card.");
                        break;
                    }
                    else if (saveCard.equals("no")) {
                        System.out.println("Card details were not saved to your account.");
                        break;
                    }
                    else {
                        System.out.println("\nWe were unable to process your request, please try again.");
                    }
                }
            }
        }
        System.out.println("\nEnjoy! If you'd like to buy anything else, please use the previous format (you can enter 'help buy' or 'help' for a refresher). Otherwise, press 'exit' to exit.");
    }

    public static void displaySnacks(Map<Food, Integer> inventory) {
        System.out.println("----------------------------------------------------------");
        System.out.println("|  Snack Name  | Category | Item Code | Quantity |  Price |");
        System.out.println("----------------------------------------------------------");
        for (Map.Entry<Food, Integer> food : inventory.entrySet()) {
            // Don't display the item if we don't have any left of it
            if (food.getValue() == 0){continue;}
            Food item = food.getKey();

            System.out.println("|" + foodDetailString(14, item.getName()) + foodDetailString(10, item.getCategory()) + foodDetailString(11, item.getItemCode())
                    + foodDetailString(10, String.valueOf(food.getValue()))  + foodDetailString(8, "$" + item.getCost()));
        }
        System.out.println("----------------------------------------------------------");

    }

    public static String foodDetailString(int maxLength, String toDisplay){
        // Calculate how many spaces should be around the current output
        int n = maxLength - toDisplay.length();
        String repeated = "";
        if (n > 0){
            repeated = new String(new char[n/2]).replace("\0", " ");
            if (n % 2 == 0) {
                return repeated + toDisplay + repeated + "|";
            }
            String odd = new String(new char[n/2 + 1]).replace("\0", " ");
            return repeated + toDisplay + odd + "|";
        }
        return toDisplay + "|";
    }

    public boolean validateInput(List<String> input){

        // Stop asking for info if their info is correct
        if (input.size() <= 2 || (!input.get(0).equals("card") && !input.get(0).equals("cash"))){
            return false;
        }
        else {
            try{
                // Check that the second input is a quantity
                Integer.valueOf(input.get(1));
                // Check that the third input is a viable item code
                if (vm.searchByItemCode(input.get(2)) == null){
                    return false;
                }

                // Check that their given cash was in the correct format
                if (input.get(0).equals("cash")){
                    // check that each cash item was of the form $cash*int
                    for (int i = 3; i < input.size(); i++){
                        String[] cashGiven = input.get(i).split("\\*");
                        if (!vm.getCash().containsKey(cashGiven[0])) {
                            return false;
                        }
                        int numGiven = Integer.parseInt(cashGiven[1]);
                    }

                }
                // Check if a user that has no saved card details (anonymous or first time signup) has inputted card details
                if ((input.get(0).equals("card")) && (currentUser.getCard() == null)) {
                    if (input.size() < 5) {
                        return false;
                    }
                }

            }
            catch(NumberFormatException ne){return false;}
            catch(ArrayIndexOutOfBoundsException a) {return false;}

        }
        return true;
    }

    // Displays by default, before user chooses to log in
    public void anonymousPage() {
        List<Transaction> transactions = Transaction.anonTransactions;
        int size = transactions.size();
        int index = 1;
        System.out.println("\nThese were the last few items bought by anonymous customers:");
        if (size < 5) {
            for (int initial = size - 1; initial >= 0; initial -= 1) {
                System.out.println(index + ") " + transactions.get(initial).getItemSold().getName());
                index++;
            }
        }
        else if (size >= 5) {
            for (int initial = size - 1; initial >= size - 5; initial -= 1) {
                System.out.println(index + ") " + transactions.get(initial).getItemSold().getName());
                index++;
            }
        }
        System.out.println();
    }

    // Displays after user logs in
    public void loggedInPage() {

        List<String> tempFood = new ArrayList<String>();
        try {
            File file = new File("./src/main/resources/transactions.txt");
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] parts = line.split(", ");
                if (parts.length == 7) {
                    String name = parts[0];
                    String itemCode = parts[1];
                    if (name.equals(currentUser.getName())) {
                        tempFood.add(vm.searchByItemCode(itemCode).getName());
                    }
                }
            }
        }
        catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        int size = tempFood.size();
        int index = 1;
        if (size == 0){return;}
        System.out.println("\nThese were the last few items bought by you:");
        if (size < 5) {
            for (int initial = size - 1; initial >= 0; initial -= 1) {
                System.out.println(index + ") " + tempFood.get(initial));
                index++;
            }
        }
        else if (size >= 5) {
            for (int initial = size - 1; initial >= size - 5; initial -= 1) {
                System.out.println(index + ") " + tempFood.get(initial));
                index++;
            }
        }
    }

    // Help command
    public void help(List<String> arguments) {
        if(arguments.size() == 0) {
            Map<String, String> toPrint = new HashMap<>();
            toPrint = allCommandBriefs;

            for(String command : toPrint.keySet()){
                System.out.printf("%15s:          %s%n", command, toPrint.get(command));}
        }
        else if(arguments.size() == 1 && arguments.get(0).equals("admin")) {
            // Print out the help commands corresponding to the current user type
            System.out.println("Below is a list of your valid commands. For more information on usage, type \"help <command>\".\n");
            Map<String, String> toPrint = new HashMap<>();
            if (UserInterface.currentUser instanceof Customer){
                toPrint = allCommandBriefs;
            }
            if (UserInterface.currentUser instanceof Cashier){
                toPrint = cashierCommandBriefs;
            }
            if (UserInterface.currentUser instanceof Seller){
                toPrint = sellerCommandBriefs;
            }
            if (UserInterface.currentUser instanceof Owner){
                toPrint = ownerCommandBriefs;
            }

            for(String command : toPrint.keySet()){
                System.out.printf("%17s:          %s%n", command, toPrint.get(command));}
        }
        else {
            Map<String, String> toPrint = new HashMap<>();

            if (UserInterface.currentUser instanceof Cashier){
                toPrint = cashierCommandUsage;
            }
            if (UserInterface.currentUser instanceof Seller){
                toPrint = sellerCommandUsage;
            }
            if (UserInterface.currentUser instanceof Owner){
                toPrint = ownerCommandUsage;
            }

            outerloop:
            for(int i = 0; i < arguments.size(); i++) {
                for(String command : toPrint.keySet()) {
                    if(command.equals(arguments.get(i))) {
                        System.out.println(toPrint.get(command));
                        continue outerloop;
                    }
                }
                for(String command : allCommandUsage.keySet()) {
                    if(command.equals(arguments.get(i))) {
                        System.out.println(allCommandUsage.get(command));
                        continue outerloop;
                    }
                }
            }
        }
        if(!(UserInterface.currentUser instanceof Customer))
            System.out.println("\nPlease type \"help admin\" to view all your account-specific commands.");
    }

}
