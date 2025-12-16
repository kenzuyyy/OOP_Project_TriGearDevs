package Model;

public class Seed {
    private int id;
    private String seedName;
    private int quantity;
    private int farmerId;
    private String status;
    private String desiredTrade; // new field

    public Seed(int id, String seedName, int quantity, int farmerId, String status, String desiredTrade) {
        this.id = id;
        this.seedName = seedName;
        this.quantity = quantity;
        this.farmerId = farmerId;
        this.status = status;
        this.desiredTrade = desiredTrade;
    }

    // Backward-compatible constructor
    public Seed(int id, String seedName, int quantity, int farmerId, String status) {
        this(id, seedName, quantity, farmerId, status, "");
    }

    public int getId() {
        return id;
    }

    public String getSeedName() {
        return seedName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getFarmerId() {
        return farmerId;
    }

    public String getStatus() {
        return status;
    }

    public String getDesiredTrade() {
        return desiredTrade;
    }

    public void setDesiredTrade(String desiredTrade) {
        this.desiredTrade = desiredTrade;
    }
}