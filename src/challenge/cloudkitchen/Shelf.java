package challenge.cloudkitchen;

import challenge.cloudkitchen.Constants.ShelfType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static challenge.cloudkitchen.Constants.*;

public class Shelf {

    ShelfType shelfType;
    List<Order> currentOrders;
    int shelfDecayModifier;
    int capacity;

    public Shelf(ShelfType type) {
        this(type, type == ShelfType.OVERFLOW ? OVERFLOW_SHELF_CAPACITY : SINGLE_TEMPERATURE_SHELF_CAPACITY);
        currentOrders = new ArrayList<>();
    }

    public Shelf(ShelfType type, int capacity) {
        this.shelfType = type;
        if (type == ShelfType.OVERFLOW) {
            this.shelfDecayModifier = SHELF_DECAY_MODIFIER_OVERFLOW;
        } else {
            this.shelfDecayModifier = SHELF_DECAY_MODIFIER_SINGLE_TEMPERATURE;
        }
        this.capacity = capacity;
    }

    public ShelfType getShelfType() {
        return shelfType;
    }

    public boolean isAvailable() {
        return currentOrders.size() < capacity;
    }

    /**
     * Add order to shelf.
     *
     * @param order
     * @return true if order was added successfully, false otherwise.
     */
    public boolean add(Order order) {
        if (currentOrders.size() < capacity) {
            currentOrders.add(order);
            System.out.println("Order added to " + shelfType.name() + ": " + order.getShortIdWithTemp());
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
                wastedStr += order.getShortId() + " ";
                wasted.add(order);
            }
        }
        if (!wasted.isEmpty()) {
            for (Order order : wasted) {
                currentOrders.remove(order);
            }
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
            if (order.getTimePickedUp() == time) {
                deliveredStr += order.getShortId() + " ";
                delivered.add(order);
            }
        }
        if (!delivered.isEmpty()) {
            for (Order order : delivered) {
                currentOrders.remove(order);
            }
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
        return str;
    }
}