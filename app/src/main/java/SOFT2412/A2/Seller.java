package SOFT2412.A2;
import java.io.*;
import java.util.*;

public class Seller extends User{

    public Seller(String name, String username, String password) {
        super(name, username, password);
    }

    public static void editItemDetail(String detailType, String itemCode, String newDetail) {
        switch (detailType) {
            case "name":
                UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, newDetail, 0);
                for (Food food : UserInterface.vm.getInventory().keySet()) {
                    if (food.getName().equals(itemCode)) {
                        food.setName(newDetail);
                    }
                }
                break;

            case "code":
                UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, newDetail, 2);
                for (Food food : UserInterface.vm.getInventory().keySet()) {
                    if (food.getItemCode().equals(itemCode)) {
                        food.setItemCode(newDetail);
                    }
                }
                break;

            case "category":
                UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, newDetail, 1);
                for (Food food : UserInterface.vm.getInventory().keySet()) {
                    if (food.getItemCode().equals(itemCode)) {
                        food.setCategory(newDetail);
                    }
                }
                break;

            case "price":
                for (Food food : UserInterface.vm.getInventory().keySet()) {
                    if (food.getItemCode().equals(itemCode)) {
                        food.setCost(Double.parseDouble(newDetail));
                    }
                }
                UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, newDetail, 3);
                break;
            
            case "quantity":
                if (Integer.parseInt(newDetail) > 15) {
                    System.out.println("Error: Maximum quantity is 15.");
                } else if (Integer.parseInt(newDetail) < 0) {
                    System.out.println("Error: Please enter a valid quantity.");
                } else {
                    UserInterface.vm.updateLine("./src/main/resources/inventory.txt", itemCode, newDetail, 4);
                    UserInterface.vm.loadInventory();
                }
                break;
            default:
                System.out.println("We could not find the change type you were trying to use. Please input \"help editItems\" for more help on this command.");
        }
    }
    
    public static void itemsSummary() {
        UserInterface.displaySnacks(UserInterface.vm.getInventory());
    }

    public static String getSummary() {
        String summary = "";
        summary += "--------------------------------------------\n";
        summary += "|  Snack Name  | Item Code | Quantity Sold |\n";
        summary += "--------------------------------------------\n";
        try{
            File file = new File("./src/main/resources/quantities.txt");
            Scanner scan = new Scanner(file);

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] parts =  line.split(", ");
                String name = parts[0];
                String code = parts[1];
                String quantitySold = parts[2];
                summary += ("|" + UserInterface.foodDetailString(14, name)) +  UserInterface.foodDetailString(11, code) +
                    (UserInterface.foodDetailString(15,  quantitySold)  + "\n");
            }
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        summary += "--------------------------------------------\n";
        return summary;
    }
}
