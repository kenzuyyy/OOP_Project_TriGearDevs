package Model;

public class Seed {
    private int id; // PK from seeds table
    private int seedId; // FK to seed_master.id
    private String seedName; // from seed_master
    private int quantity;
    private int farmerId; // FK to users.id
    private String status;
    private String desiredTrade;
    private String farmerUsername; // for Admin view joins

    // ---------------- Constructor for FarmerDAO (with farmerId) ----------------
    public Seed(int id, String seedName, int quantity, int farmerId, String status, String desiredTrade) {
        this.id = id;
        this.seedName = seedName;
        this.quantity = quantity;
        this.farmerId = farmerId;
        this.status = status;
        this.desiredTrade = desiredTrade;
    }

    // ---------------- Constructor for AdminDAO (with farmerUsername)
    // ----------------
    public Seed(int id, String seedName, int quantity, String status, String desiredTrade, String farmerUsername) {
        this.id = id;
        this.seedName = seedName;
        this.quantity = quantity;
        this.status = status;
        this.desiredTrade = desiredTrade;
        this.farmerUsername = farmerUsername;
    }

    // ---------------- Constructor with seedId (FK reference) ----------------
    public Seed(int id, int seedId, String seedName, int quantity, int farmerId,
            String status, String desiredTrade) {
        this.id = id;
        this.seedId = seedId;
        this.seedName = seedName;
        this.quantity = quantity;
        this.farmerId = farmerId;
        this.status = status;
        this.desiredTrade = desiredTrade;
    }

    // ---------------- Getters ----------------
    public int getId() {
        return id;
    }

    public int getSeedId() {
        return seedId;
    } // ✅ Now defined

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

    public String getFarmerUsername() {
        return farmerUsername;
    }

    // ---------------- Setters ----------------
    public void setId(int id) {
        this.id = id;
    }

    public void setSeedId(int seedId) {
        this.seedId = seedId;
    }

    public void setSeedName(String seedName) {
        this.seedName = seedName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setFarmerId(int farmerId) {
        this.farmerId = farmerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDesiredTrade(String desiredTrade) {
        this.desiredTrade = desiredTrade;
    }

    public void setFarmerUsername(String farmerUsername) {
        this.farmerUsername = farmerUsername;
    }
}