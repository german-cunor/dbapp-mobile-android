package db;

public class Product {
    private int Id;
    private String Name;
    private double Price;
    private int Stock;
    private String Description;

    // Getters and setters
    public int getId() { return Id; }
    public void setId(int id) { this.Id = id; }

    public String getName() { return Name; }
    public void setName(String name) { this.Name = name; }

    public double getPrice() { return Price; }
    public void setPrice(double price) { this.Price = price; }

    public int getStock() { return Stock; }
    public void setStock(int stock) { this.Stock = stock; }

    public String getDescription() { return Description; }
    public void setDescription(String description) { this.Description = description; }
}
