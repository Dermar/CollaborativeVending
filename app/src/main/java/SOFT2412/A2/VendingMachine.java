package SOFT2412.A2;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
public class VendingMachine {

    // A hashmap of the form: foodType: quantity in the vending machine
    private HashMap<Food, Integer> inventory = new HashMap<Food, Integer>();
    // A hashmap that records all the cash in the form cashType: quantity
    private HashMap<String, Integer> cash = new LinkedHashMap<String, Integer>();


    public VendingMachine(){
        // Load in the cash and the inventory from "inventory.txt" and "cash.txt" files using methods in case we need to reload them
        // load in the inventory from "inventory.txt"
        loadInventory();
        // load in the cash numbers from "cash.txt"
        loadCash();
        // Load in the card details from "creditCards.json"
        Card.loadCards();
        // Users need to be laoded in at the beginning for methods to work
        User.loadUsers();
        // Saved transactions from previous use are loaded in
        Transaction.loadTransactions(this);

    }

    public void loadCash(){
        try{
            File cashFile = new File("./src/main/resources/cash.txt");
            Scanner scan1 = new Scanner(cashFile);
            while (scan1.hasNextLine()){
                String[] line = scan1.nextLine().split(", ");
                cash.put(line[0], Integer.valueOf(line[1]));
            }
            scan1.close();
        }
        catch(FileNotFoundException fe){}
    }

    public void loadInventory(){
        inventory.clear();
        try{
            File invenFile = new File("./src/main/resources/inventory.txt");
            Scanner scan2 = new Scanner(invenFile);
            while (scan2.hasNextLine()){
                String[] line = scan2.nextLine().split(", ");
                inventory.put(new Food(line[0], line[1], line[2], Double.parseDouble(line[3])), Integer.valueOf(line[4]));
            }
            scan2.close();
        }
        catch(FileNotFoundException fe){}
    }

    // I'm following Frank's structure so user input will be of the form:
    // buyer cash 3 mw 50c*3 $5*3
    // i.e. userType paymentType quantity itemCode givenMoney
    // GivenMoney can have a variable length so it's simply all the inputs after the itemCode
    public String payByCash(int quantity, String itemCode, String givenMoney){
        loadCash();
        loadInventory();
        // Check that we have enough stock for the purchase
        if (!checkStock(searchByItemCode(itemCode), quantity)){
            throw new NoSuchElementException();
        }
        double toPay = calculateToPay(itemCode, quantity);
        String[] givenCash = givenMoney.split(" ");
        double paid = calculateGivenCash(givenCash);

        // Check that they've given enough
        if (toPay > paid && Math.abs(toPay - paid) >= 0.00001){
            loadCash();
            throw new ArithmeticException();
        }

        DecimalFormat df = new DecimalFormat("0.00");
        BigDecimal change = new BigDecimal(paid - toPay);

        // Calculate change
        Map<String, Integer> changeCash = calculateChange(change);

        // Process final result summary
        String changeBreakdown = "";
        for (Map.Entry<String, Integer> payment: changeCash.entrySet()){
            changeBreakdown += (" (" + payment.getKey() + "*" + payment.getValue() + ")");
        }

        String resultString = "Transaction successful!\n" + "Paid: $" + df.format(paid) + "\nDue: $" + df.format(toPay) + "\nChange: $" + df.format(change);
        if (changeBreakdown != ""){
            resultString += ("\n\nChange Breakdown: \n" + changeBreakdown);
        }
        updateItem(itemCode, quantity);
        updateTotalSold(itemCode, quantity);

        //Now that the transaction has been confirmed, update the cash.txt file to reflect the cash hashmap
        for (Map.Entry<String, Integer> cashItem: cash.entrySet()){
            updateLine("./src/main/resources/cash.txt", cashItem.getKey(), String.valueOf(cashItem.getValue()), 1);
        }

        return resultString;
    }

    public Map<String, Integer> calculateChange(BigDecimal change){
        Map<String, Integer> changeCash = new LinkedHashMap<String, Integer>();
        int prevChange = -1;
        int currChange = 0;
        // This is a disgusting line but basically it's giving change to the customer while there is still change to be given (change > 0)
        while (change.subtract(new BigDecimal(0.0001)).compareTo(new BigDecimal(0)) == 1){
            // If we haven't added any new change this round, then we can't figure out a way to give them change so return
            if (prevChange == currChange){
                // If there was no possible change then we should return all the change to the cash hashmap
                loadCash();
                loadInventory();
                throw new IllegalStateException();
            }
            prevChange = currChange;
            // Check every cash value to try to add a coin or note to the change
            for (String cashType: cash.keySet()){
                BigDecimal value;

                // Check how much this cash type is worth according to whether it's a dollar or cent
                if (Character.toString(cashType.charAt(0)).equals("$")){
                    value = new BigDecimal(cashType.substring(1));
                }
                else{
                    value = BigDecimal.valueOf(Double.parseDouble(cashType.substring(0, cashType.length() - 1)) / 100);
                }

                // Round to the nearest 5 cents
                change = round(change, new BigDecimal("0.05"), RoundingMode.HALF_UP);
                // Try to add it to the list of change as many times as you can
                while (change.compareTo(value) >= 0 && cash.get(cashType) > 0){
                    cash.put(cashType, cash.get(cashType) - 1);
                    change = change.subtract(value);
                    currChange++;

                    if (!changeCash.containsKey(cashType)){
                        changeCash.put(cashType, 1);
                    }
                    else{
                        changeCash.put(cashType, changeCash.get(cashType) + 1);
                    }

                }
            }
        }
        return changeCash;
    }

    public double calculateToPay(String itemCode, int quantity){
        Food toBuy = searchByItemCode(itemCode);
        return toBuy.getCost() * quantity;
    }

    public double calculateGivenCash(String[] givenCash){
        double paid = 0;
        // Go through every cash item and calculate how much money has been given
        for (String typeOfCash: givenCash){
            String[] thisCash = typeOfCash.split("\\*");
            if (thisCash[0].equals("")){
                throw new ArithmeticException();
            }

            // Add the given cash into our list of cash
            cash.put(thisCash[0], cash.get(thisCash[0]) + 1);
            // Check if the input type given is a dollar (starts with $)
            if (Character.toString(thisCash[0].charAt(0)).equals("$")) {
                paid += (Double.parseDouble(thisCash[0].substring(1)) * Double.parseDouble(thisCash[1]));
            }
            // Otherwise it's cents
            else {
                paid += ((Double.parseDouble(thisCash[0].substring(0, thisCash[0].length() - 1)) / 100) * Double.parseDouble(thisCash[1]));
            }
        }
        return paid;
    }

    public String payByCard(int quantity, String itemCode) {
        updateItem(itemCode, quantity);
        updateTotalSold(itemCode, quantity);
        Food item = searchByItemCode(itemCode);
        return "Transaction successful! User received " + quantity + " " + item.getName() + "(s)!";
    }

    public Food searchByItemCode(String itemCode){
        for (Food f: inventory.keySet()){
            if (f.getItemCode().equals(itemCode)){
                return f;
            }
        }
        return null;
    }

    // This method rounds a BigDecimal to the given number of d.p
    public static BigDecimal round(BigDecimal value, BigDecimal increment, RoundingMode roundingMode) {
        if (increment.signum() == 0) {
            // 0 increment does not make much sense, but prevent division by 0
            return value;
        } else {
            BigDecimal divided = value.divide(increment, 0, roundingMode);
            BigDecimal result = divided.multiply(increment);
            return result;
        }
    }

    public boolean checkStock(Food item, int quantity) {
        if (inventory.get(item) < quantity) {
            return false;
        }
        return true;
    }


    //When a user makes a transaction, update the quantity and/or cash
    public void updateItem(String itemCode, int quantity) {
        Food foodItem = searchByItemCode(itemCode);
        inventory.put(foodItem, inventory.get(foodItem) - quantity);
        updateLine("./src/main/resources/inventory.txt", itemCode, Integer.toString(inventory.get(foodItem)), 4);
    }

    // Update a line in a file by searching for a specific string (somewhat like a code to find the line)
    // and replacing a string on a specified index
    // If string is not in file, append to the file
    public static void updateLine(String fileName, String findString, String replacedString, int index) {
        try{
            boolean found = false;
            File file = new File(fileName);
            Scanner scan = new Scanner(file);
            StringBuffer inputBuffer = new StringBuffer();
            while (scan.hasNextLine()){
                String line = scan.nextLine();
                String[] parts =  line.split(", ");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].equals(findString)) {
                        found = true;
                        parts[index] = replacedString;
                    }
                }
                inputBuffer.append(String.join(", ", parts));
                inputBuffer.append("\n");
            }
            scan.close();
            String inputStr = inputBuffer.toString();
            FileOutputStream output = new FileOutputStream(fileName);
            output.write(inputStr.getBytes());
            output.close();

            // Error message if string is not in the file
            if (! found) {
                System.out.printf("Error: %s is not found in the records.", findString);
                System.out.println();
            }
        } catch(FileNotFoundException fe){
            fe.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    // Update quantities.txt with the format "name, itemCode, quantity sold"
    public void updateTotalSold(String itemCode, int quantity) {

        boolean hasItem = false;
        try{
            File file = new File("./src/main/resources/quantities.txt");
            Scanner scan = new Scanner(file);
            StringBuffer inputBuffer = new StringBuffer();

            while (scan.hasNextLine()){
                String line = scan.nextLine();
                if (line.contains(itemCode)) {
                    hasItem = true;
                    String[] parts =  line.split(", ");
                    int newQuantity = Integer.parseInt(parts[2]) + quantity;
                    parts[2] = Integer.toString(newQuantity);
                    inputBuffer.append(String.join(", ", parts));
                    inputBuffer.append("\n");
                } else if (! line.equals("")) {
                    inputBuffer.append(line);
                    inputBuffer.append("\n");
                }
            }
            if (! hasItem) {
                inputBuffer.append(searchByItemCode(itemCode).getName() + ", " + itemCode + ", " + Integer.toString(quantity) + "\n");
            }
            scan.close();
            String inputStr = inputBuffer.toString();
            FileOutputStream output = new FileOutputStream(file);
            output.write(inputStr.getBytes());
            output.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // Method used for testing to make cash.txt, inventory.txt, and their respective hashmaps reflect StableCash.txt and StableInventory.txt so that expected output is consistent
    public void defaulting(){
        try{
            // Default for cash.txt
            File cashFile = new File("./src/main/resources/StableCash.txt");
            Scanner scan1 = new Scanner(cashFile);
            while (scan1.hasNextLine()){
                String[] line = scan1.nextLine().split(", ");
                cash.put(line[0], Integer.valueOf(line[1]));
                updateLine("./src/main/resources/cash.txt", line[0], line[1], 1);
            }
            scan1.close();

            // Default for inventory.txt
            File invenFile = new File("./src/main/resources/StableInventory.txt");
            Scanner scan2 = new Scanner(invenFile);
            while (scan2.hasNextLine()) {
                String[] line = scan2.nextLine().split(", ");
                inventory.put(new Food(line[0], line[1], line[2], Double.parseDouble(line[3])), Integer.valueOf(line[4]));
                updateLine("./src/main/resources/inventory.txt", line[2], line[0], 0);
                updateLine("./src/main/resources/inventory.txt", line[0], line[1], 1);
                updateLine("./src/main/resources/inventory.txt", line[0], line[2], 2);
                updateLine("./src/main/resources/inventory.txt", line[0], line[3], 3);
                updateLine("./src/main/resources/inventory.txt", line[0], line[4], 4);
            }
            scan2.close();

            // Default for transactions.txt
            File transactionFile = new File("./src/main/resources/transactions.txt");
            FileOutputStream o = new FileOutputStream(transactionFile, false);
            o.write("anonymous, se, 2022-10-27T21:59:18.128234400, $2.5, $0.0, card, Successful\nanonymous, 2022-10-27T21:59:26.125755100, Cancelled due to incorrect user input\ntester, mm, 2022-10-27T21:59:18.128234400, $2.5, $0.0, card, Successful\n".getBytes());
            o.close();

            // Default for users.txt
            File usersFile = new File("./src/main/resources/users.txt");
            FileOutputStream u = new FileOutputStream(usersFile, false);
            u.write("owner, Freddy, generic, freddyisthebest\ncustomer, Mark, mark234, mypassword\ncashier, Edith, sampleusername, samplepassword\ncustomer, name, realName, password\nseller, Naomi, naomi<3, name\n".getBytes());
            u.close();

        }
        catch(FileNotFoundException fe){System.out.println(fe);}
        catch(IOException io){System.out.println(io);}
    }

    public HashMap<String, Integer> getCash() {
        return cash;
    }

    public HashMap<Food, Integer> getInventory() {
        return inventory;
    }
}
