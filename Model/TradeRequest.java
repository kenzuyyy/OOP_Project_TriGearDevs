package Model;

import java.sql.Timestamp;

public class TradeRequest {
    private int id;
    private String seedName;
    private String desiredTrade;
    private String offer;
    private Timestamp requestDate;

    public TradeRequest(int id, String seedName, String desiredTrade, String offer, Timestamp requestDate) {
        this.id = id;
        this.seedName = seedName;
        this.desiredTrade = desiredTrade;
        this.offer = offer;
        this.requestDate = requestDate;
    }

    public int getId() {
        return id;
    }

    public String getSeedName() {
        return seedName;
    }

    public String getDesiredTrade() {
        return desiredTrade;
    }

    public String getOffer() {
        return offer;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }
}