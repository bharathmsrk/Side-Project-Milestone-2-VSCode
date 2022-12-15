package mainScript;

public class Item {
    private final String id;
    private final String brand;
    private final String name;
    private float price;
    private String date;

    public Item(String id, String brand, String name, String price) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.price=0;
        this.date = "";
        try {
            this.price = Float.parseFloat(price);
        } catch (Exception e) {
            System.out.println("Error" + e);
            System.out.println("Skipping item "+name+", with id "+id);
        }
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getBrand() {
        return brand;
    }
    public float getPrice() {
        return price;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }
}
