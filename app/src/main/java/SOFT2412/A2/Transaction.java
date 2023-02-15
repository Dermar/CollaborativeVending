package SOFT2412.A2;

import java.time.LocalDateTime;
import java.util.*;
import java.io.*;
import java.nio.file.*;
// date and time, item sold, amount of money paid, returned change and payment method
public class Transaction {

    private Food itemSold;
    private LocalDateTime time;
    private double paid;
    private double change;
    private String paymentMethod;
    private String userName; // this can be 'anonymous' or the user's name
    private String state; // this is whether the transaction was successful or cancelled (and for what reason)
    public static List<Transaction> anonTransactions = new ArrayList<>();
    public static Map<User, List<Transaction>> userTransactions = new HashMap<>();
    public static List<Transaction> cancelTransactions = new ArrayList<>();

    public Transaction(String userName, Food itemSold, LocalDateTime timeSold, double paid, double change, String paymentMethod, String state){
        if (userName.equals("")){
            this.userName = "anonymous";
        }
        else{
            this.userName = userName;
        }
        this.itemSold = itemSold;
        this.time = timeSold;
        this.paid = paid;
        this.paymentMethod = paymentMethod;
        this.state = state;
        this.change = change;

        if (this.userName.equals("anonymous")){
            anonTransactions.add(this);
        }
        else{
            // If this is a logged-in user, then add them to userTransactions depending on whether they've been added before or not
            User thisUser = User.getUserByName(userName);
            if (userTransactions.containsKey(thisUser)){
                userTransactions.get(thisUser).add(this);
            }
            else{
                userTransactions.put(thisUser, new ArrayList<Transaction>(Arrays.asList(this)));
            }

        }

    }

    // Constructor for cancelled transactions
    public Transaction(String userName, LocalDateTime timeCancelled, String reasonCancelled){
        if (userName.equals("")){
            this.userName = "anonymous";
        }
        else{
            this.userName = userName;
        }

        this.time = timeCancelled;
        this.state = reasonCancelled;

        cancelTransactions.add(this);

    }

    // Read in transactions from transactions.txt and update anonTransactions and userTransactions on startup
    public static void loadTransactions(VendingMachine vm){
        anonTransactions = new ArrayList<>();
        userTransactions = new HashMap<>();
        cancelTransactions = new ArrayList<>();
        try {
            // Look into transactions.txt and load the anonTransactions and userTransactions data structures
            File f = new File("./src/main/resources/transactions.txt");
            Scanner scan = new Scanner(f);

            while (scan.hasNextLine()) {
                // Each line in transactions is of the form userName, itemSold, timeSold, paid, change, paymentMethod, state
                String[] line = scan.nextLine().split(", ");

                // Make a new transaction which will then put itself into the correct transactions list (see constructors)
                // Check whether you're dealing with a cancelled transaction or a normal transaction
                if (line.length == 7) {
                    new Transaction(line[0], vm.searchByItemCode(line[1]), LocalDateTime.parse(line[2]), Double.parseDouble(line[3].split("\\$")[1]), Double.parseDouble(line[4].split("\\$")[1]), line[5], line[6]);
                }
                else if (line.length == 3){
                    new Transaction("", LocalDateTime.parse(line[1]), line[2]);
                }
            }
        }
        catch(FileNotFoundException fn){System.out.println(fn);}

    }

    // Appends a string representation of a transaction to transactions.txt
    public static void writeTransaction(Transaction newT){

        try{
            String transactionLine = "";
            // If the given transaction was successful then write this line normally:
            if (newT.getItemSold() != null) {
                transactionLine = newT.getUserName() + ", " + newT.getItemSold().getItemCode() + ", " + newT.getTimeSold() + ", $" + newT.getPaid() + ", $" + newT.getChange() + ", " + newT.getPaymentMethod() + ", " + newT.getState() + "\n";
            }
            // Otherwise we're dealing with a cancelled transaction and we don't have as much info
            else{
                transactionLine = newT.getUserName() + ", " + newT.getTimeSold() + ", " + newT.getState() + "\n";
            }
            Files.write(Paths.get("./src/main/resources/transactions.txt"), transactionLine.getBytes(), StandardOpenOption.APPEND);
        }
        catch(IOException fn){System.out.println(fn);}
    }


    public Food getItemSold(){return itemSold;}
    public LocalDateTime getTimeSold(){return time;}
    public double getPaid(){return paid;}
    public String getPaymentMethod(){return paymentMethod;}
    public String getUserName(){return userName;}
    public String getState(){return state;}
    public double getChange(){return change;}
}
