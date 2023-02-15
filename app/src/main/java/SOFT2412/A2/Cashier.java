package SOFT2412.A2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Cashier extends User{
    public Cashier(String name, String username, String password) {
        super(name, username, password);
    }

    // Power of cashier and owner
    public static String getTransactionSummary(){
        String allTransactions="";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        allTransactions += "--------------------------------------------------------------------\n";
        allTransactions += "|  Snack Name  |  Paid  | Change | Payment Method |       Time     |\n";
        allTransactions += "--------------------------------------------------------------------\n";
        for (List<Transaction> tList: Transaction.userTransactions.values()){
            for (Transaction t: tList) {

                allTransactions += ("|" + UserInterface.foodDetailString(14, t.getItemSold().getName()) +  UserInterface.foodDetailString(8, "$" + String.valueOf(t.getPaid())) + UserInterface.foodDetailString(8, "$" + String.valueOf(t.getChange()))
                        + UserInterface.foodDetailString(16, t.getPaymentMethod()) + UserInterface.foodDetailString(16, t.getTimeSold().format(formatter)) + "\n");
            }
        }

        for (Transaction t: Transaction.anonTransactions){
            allTransactions += ("|" + UserInterface.foodDetailString(14, t.getItemSold().getName()) +  UserInterface.foodDetailString(8, "$" + String.valueOf(t.getPaid())) + UserInterface.foodDetailString(8, "$" + String.valueOf(t.getChange()))
                    + UserInterface.foodDetailString(16, t.getPaymentMethod()) + UserInterface.foodDetailString(16, t.getTimeSold().format(formatter)) + "\n");
        }
        allTransactions += "--------------------------------------------------------------------\n";
        return allTransactions;
    }

    // Edit the change and update cash.txt
    public static void editChange(String cashAmount, int quantity) {
        if (quantity > 15 || quantity < 0){
            System.out.println("Apologies, but you cannot have less than 0 or more than 15 items in this vending machine.");
            return;
        }
        UserInterface.vm.updateLine("./src/main/resources/cash.txt", cashAmount, Integer.toString(quantity), 1);
        UserInterface.vm.loadCash();
    }

    public static String displayAvailableChange() {
        String summary = "";
        summary += "------------------------\n";
        summary += "| Coin/Note | Quantity |\n";
        summary += "------------------------\n";
        for (String cashAmount : UserInterface.vm.getCash().keySet()) {
            String quantity = Integer.toString(UserInterface.vm.getCash().get(cashAmount));
            summary += ("|" + UserInterface.foodDetailString(11, cashAmount)) +  (UserInterface.foodDetailString(10, quantity) + "\n");
        }
        summary += "------------------------\n";
        return summary;
    }

}
