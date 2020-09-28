package challenge.cloudkitchen;

import org.json.simple.JSONObject;
import challenge.cloudkitchen.Constants.*;

/**
 * This class contains metadata of an order when it's placed by clients. The order
 * is identified by a UUID (<i>id</i>) or a short version of it (<i>shortId</i>),
 * which is the last segment of the UUID. Upon being initiated, the order's constructor
 * will create the <i>shortId</i>.
 *
 * Currently support orders of three temperatures: hot, cold and frozen.
 */
public class Order {
    String id;
    // shortId is the last segment of the full ID. We could use it for identifying an order on output console
    // for better readability
    String shortId;
    String name;
    OrderTemperature temp;
    double shelfLife;
    double decayRate;
    Integer timeArrived;
    Integer timePickedUp;

    public Order(String id, String name, String temp, double shelfLife, double decayRate) {
        this.id = id;
        this.name = name;
        this.temp = getOrderTemperature(temp);
        this.shelfLife = shelfLife;
        this.decayRate = decayRate;
        this.shortId = id.split("-")[4];
        timeArrived = null;
        timePickedUp = null;
    }

    OrderTemperature getOrderTemperature(String temp) {
        if (temp.equals("hot")) {
            return OrderTemperature.HOT;
        }
        if (temp.equals("cold")) {
            return OrderTemperature.COLD;
        }
        if (temp.equals("frozen")) {
            return OrderTemperature.FROZEN;
        }
        return null;
    }

    public String getId() {
        return this.id;
    }

    public String getShortId() {
        return this.shortId;
    }

    public Constants.OrderTemperature getTemp() {
        return temp;
    }

    public String getShortIdWithTemp() {
        return this.shortId + "(" + this.temp + ")";
    }

    public Integer getTimeArrived() {
        return timeArrived;
    }

    public Integer getTimePickedUp() {
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