package challenge.cloudkitchen;

import challenge.cloudkitchen.Constants.ShelfType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static challenge.cloudkitchen.Constants.*;

/**
 * This class contains metadata of a shelf and various methods implementing shelf's functionalities.
 * A shelf could be one of 4 types: hot, cold, frozen or overflow; distinguished by its <i>shelfType</i>.
 * <p>A shelf object provides various methods for adding orders to, removing orders from itself,
 * cleaning up delivered or wasted orders.</p>
 */
public class Shelf {

    ShelfType shelfType;
    List<Order> currentOrders;
    int shelfDecayModifier;
    int capacity;

    public Shelf(ShelfType type) {
        this(type, type == ShelfType.OVERFLOW ? OVERFLOW_SHELF_CAPACITY : SINGLE_TEMPERATURE_SHELF_CAPACITY);
    }

    public Shelf(ShelfType type, int capacity) {
        this.shelfType = type;
        this.shelfDecayModifier =
                type == ShelfType.OVERFLOW ? SHELF_DECAY_MODIFIER_OVERFLOW : SHELF_DECAY_MODIFIER_SINGLE_TEMPERATURE;
        this.capacity = capacity;
        currentOrders = new ArrayList<>();
    }

    public ShelfType getShelfType() {
        return shelfType;
    }

    /**
     * Check if shelf still has room for new orders.
     *
     * @return true if shelf has room, false otherwise.
     */
    public boolean isAvailable() {
        return currentOrders.size() < capacity;
    }

    /**
     * Add order to shelf. If the order is already on shelf, it won't be added again.
     *
     * @param order the order to be added
     * @return true if order was added successfully, false otherwise.
     */
    public boolean add(Order order) {
        for (Order o : currentOrders) {
            if (o.getId().equals(order.getId())) {
                return false;
            }
        }
        if (!matchTemperature(order)) {
            return false;
        }
        if (currentOrders.size() < capacity) {
            currentOrders.add(order);
            System.out.println("Order added to " + shelfType.name() + ": " + order.getShortIdWithTemp());
            return true;
        }
        return false;
    }

    /**
     * Check if an order's temperature matches shelf's temperature.
     *
     * @param order the order to check
     * @return true if shelf is overflow shelf or the temperature match; false otherwise.
     */
    boolean matchTemperature(Order order) {
        OrderTemperature orderTemperature = order.getTemp();
        if (shelfType != ShelfType.OVERFLOW) {
            if (shelfType == ShelfType.HOT && orderTemperature != OrderTemperature.HOT) {
                return false;
            }
            if (shelfType == ShelfType.COLD && orderTemperature != OrderTemperature.COLD) {
                return false;
            }
            if (shelfType == ShelfType.FROZEN && orderTemperature != OrderTemperature.FROZEN) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove order from shelf.
     *
     * @param order
     * @return true if order was removed successfully, false otherwise.
     */
    public boolean remove(Order order) {
        System.out.println("Order removed from " + shelfType.name() + ": " + order.getShortIdWithTemp());
        return currentOrders.remove(order);
    }

    public List<Order> getCurrentOrders() {
        return currentOrders;
    }

    /**
     * Remove a random order from shelf to clear space for incoming orders.
     *
     * @return order that was removed.
     */
    public Order removeRandomOrder() {
        if (currentOrders.size() == 0) {
            return null;
        }
        int ind = new Random().nextInt(currentOrders.size());
        Order order = currentOrders.get(ind);
        currentOrders.remove(ind);
        System.out.println("Order removed from " + shelfType.name() + " to clear space: " + order.getShortId());
        return order;
    }

    /**
     * Compute the inherent value of an order on shelf.
     *
     * @param order
     * @param time current simulated time.
     * @return inherent value of the order.
     */
    double computeInherentValue(Order order, int time) {
        int orderAge = time - order.timeArrived;
        return (order.shelfLife - orderAge - order.decayRate * orderAge * shelfDecayModifier) / order.shelfLife;
    }

    /**
     * Clean up all orders with inherent value less than or equal to 0.
     *
     * @param time current simulated time.
     */
    public void cleanUpWastedOrders(int time) {
        List<Order> wasted = new ArrayList<>();
        String wastedStr = "";
        for (Order order : currentOrders) {
            double inherentValue = computeInherentValue(order, time);
            if (inherentValue <= 0) {
                wastedStr +=
                        order.getShortId() + "(after " + (time - order.getTimeArrived()) + "s) ";
                wasted.add(order);
            }
        }
        if (!wasted.isEmpty()) {
            currentOrders.removeAll(wasted);
            System.out.println("Orders wasted and removed from " + shelfType.name() + ": " + wastedStr);
        }
    }

    /**
     * Clean up all delivered orders.
     *
     * @param time current simulated time.
     */
    public void cleanUpDeliveredOrders(int time) {
        List<Order> delivered = new ArrayList<>();
        String deliveredStr = "";
        for (Order order : currentOrders) {
            if (order.getTimePickedUp() <= time) {
                deliveredStr +=
                        order.getShortId() + "(after " + (time - order.getTimeArrived()) + "s) ";
                delivered.add(order);
            }
        }
        if (!delivered.isEmpty()) {
            currentOrders.removeAll(delivered);
            System.out.println("Orders delivered and removed from " + shelfType.name() + ": " + deliveredStr);
        }
    }

    /**
     * Get a string representation of all orders currently on shelf.
     *
     * @return a string representation of shelf's content.
     */
    public String getShelfContent() {
        String str = shelfType.name() + " Occupancy (" + currentOrders.size() + "/" + capacity + "): ";
        for (Order order : currentOrders) {
            str += order.getShortId() + " ";
        }
        if (currentOrders.size() == 0) {
            str += "None";
        }
        return str;
    }
}