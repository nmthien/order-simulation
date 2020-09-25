package challenge.cloudkitchen;

import org.json.simple.JSONObject;

public class Order {
    String id;
    // shortId is the last segment of the full ID. We could use it for identifying an order on output console
    // for better readability
    String shortId;
    String name;
    Constants.ShelfType temp;
    double shelfLife;
    double decayRate;
    int timeArrived;
    int timePickedUp;

    public Order(String id, String name, String temp, double shelfLife, double decayRate) {
        this.id = id;
        this.name = name;
        this.temp = getOrderTemperature(temp);
        this.shelfLife = shelfLife;
        this.decayRate = decayRate;
        this.shortId = id.split("-")[4];
    }

    Constants.ShelfType getOrderTemperature(String temp) {
        if (temp.equals("hot")) {
            return Constants.ShelfType.HOT;
        }
        if (temp.equals("cold")) {
            return Constants.ShelfType.COLD;
        }
        if (temp.equals("frozen")) {
            return Constants.ShelfType.FROZEN;
        }
        return null;
    }

    public String getId() {
        return this.id;
    }

    public String getShortId() {
        return this.shortId;
    }

    public Constants.ShelfType getTemp() {
        return temp;
    }

    public String getShortIdWithTemp() {
        return this.shortId + "(" + this.temp + ")";
    }

    public int getTimeArrived() {
        return timeArrived;
    }

    public int getTimePickedUp() {
        return timePickedUp;
    }

    public void setTimePickedUp(int timePickedUp) {
        this.timePickedUp = timePickedUp;
    }

    public void arrive(int time) {
        this.timeArrived = time;
    }

    /**
     * Create an Order object from a JSONObject.
     *
     * @param o the JSONObject to be parsed.
     * @return Order object parsed from JSONObject.
     */
    public static Order fromJsonObject(JSONObject o) {
        return new Order(
                (String)o.get("id"),
                (String)o.get("name"),
                (String)o.get("temp"),
                Double.parseDouble(o.get("shelfLife").toString()),
                Double.parseDouble(o.get("decayRate").toString()));
    }
}