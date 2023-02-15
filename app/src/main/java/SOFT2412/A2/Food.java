package SOFT2412.A2;

public class Food {
    private String name;
    private String category;
    private String itemCode;
    private double cost;

    public Food(String name, String category, String itemCode, double cost){
        this.name = name;
        this.category = category;
        this.itemCode = itemCode;
        this.cost = cost;

    }


    // Getters and setters
    public String getCategory(){return category;}
    public String getName(){return name;}
    public String getItemCode(){return itemCode;}
    public double getCost(){return cost;}
    public void setCategory(String newCat){category = newCat;}
    public void setName(String newName){name = newName;}
    public void setItemCode(String newCode){itemCode = newCode;}
    public void setCost(double newCost){cost = newCost;}

}
