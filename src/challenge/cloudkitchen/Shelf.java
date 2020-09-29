package challenge.cloudkitchen;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains metadata of a shelf and various methods implementing shelf's functionalities.
 * A shelf could be one of 2 types: single temperature or overflow.
 * Single temperature shelf could be of hot, cold or frozen temperature.
 *
 * <p>A shelf object provides methods for adding orders, removing orders,
 * cleaning up delivered or wasted orders.</p>
 */
public abstract class Shelf {

    List<Order> currentOrders;
    int shelfDecayModifier;
    int capacity;

    public Shelf() {
        currentOrders = new ArrayList<>();
    }

    public List<Order> getCurrentOrders() {
        return currentOrders;
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
     * Add order to shelf. If the order is already on shelf or doesn't have arrival or pick-up time,
     * it won't be added again.
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
        if (order.getTimeArrived() == null || order.getTimePickedUp() == null) {
            return false;
        }
        if (currentOrders.size() < capacity) {
            currentOrders.add(order);
            System.out.println("Order added to " + getShelfName() + ": " + order.getShortIdWithTemp());
            return true;
        }
        return false;
    }

    /**
     * Remove order from shelf.
     *
     * @param order
     * @return true if order was removed successfully, false otherwise.
     */
    public boolean remove(Order order) {
        System.out.println("Order removed from " + getShelfName() + ": " + order.getShortIdWithTemp());
        return currentOrders.remove(order);
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
            System.out.println("Orders wasted and removed from " + getShelfName() + ": " + wastedStr);
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
            System.out.println("Orders delivered and removed from " + getShelfName() + ": " + deliveredStr);
        }
    }

    /**
     * Get a string representation of all orders currently on shelf.
     *
     * @return a string representation of shelf's content.
     */
    public String getShelfContent() {
        String str = " Occupancy (" + currentOrders.size() + "/" + capacity + "): ";
        for (Order order : currentOrders) {
            str += order.getShortId() + " ";
        }
        if (currentOrders.size() == 0) {
            str += "None";
        }
        return getShelfName() + str;
    }

    /**
     * Get a string representation of shelf's name (hot, cold, frozen, overflow).
     *
     * @return shelf's name.
     */
    abstract String getShelfName();

    /**
     * Remove a random order from shelf to clear space for incoming orders.
     *
     * @return order that was removed.
     */
    public abstract Order removeRandomOrder();
}