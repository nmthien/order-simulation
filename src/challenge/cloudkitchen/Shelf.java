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
        currentOrders = new ArrayList<Order>();
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
     *
     * @param order
     * @return
     */
    public boolean add(Order order) {
        if (currentOrders.size() < capacity) {
            currentOrders.add(order);
            System.out.println("Order added to " + shelfType.name() + ": " + order.getShortIdWithTemp());
            return true;
        }
        return false;
    }

    public boolean remove(Order order) {
        System.out.println("Order removed from " + shelfType.name() + ": " + order.getShortIdWithTemp());
        return currentOrders.remove(order);
    }

    public List<Order> getCurrentOrders() {
        return currentOrders;
    }

    public Order removeRandomOrder() {
        int ind = new Random().nextInt(currentOrders.size());
        Order order = currentOrders.get(ind);
        currentOrders.remove(ind);
        System.out.println("Order removed from " + shelfType.name() + " to clear space: " + order.getShortId());
        return order;
    }

    double computeInherentValue(Order order, int time) {
        int orderAge = time - order.timeArrived;
        return (order.shelfLife - orderAge - order.decayRate * orderAge * shelfDecayModifier) / order.shelfLife;
    }

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

    public String getShelfContent() {
        String str = shelfType.name() + " Occupancy (" + currentOrders.size() + "/" + capacity + "): ";
        for (Order order : currentOrders) {
            str += order.getShortId() + " ";
        }
        return str;
    }
}