package challenge.cloudkitchen;

import challenge.cloudkitchen.Constants.*;
import java.util.Random;

import static challenge.cloudkitchen.Constants.OVERFLOW_SHELF_CAPACITY;
import static challenge.cloudkitchen.Constants.SHELF_DECAY_MODIFIER_OVERFLOW;

/**
 * <p>OverflowShelf contains orders of all temperature. An order is put on overflow shelf when there's no
 * room in the shelf of its temperature.</p>
 * <p>Overflow shelf supports method to remove a random order from itself when there's no room for incoming orders.</p>
 * <p>An order could be moved from overflow shelf to a single temperature shelf to make room</p>
 */
public class OverflowShelf extends Shelf {

    public OverflowShelf() {
        super();
        capacity = OVERFLOW_SHELF_CAPACITY;
        shelfDecayModifier = SHELF_DECAY_MODIFIER_OVERFLOW;
    }

    String getShelfName() {
        return ShelfType.OVERFLOW.name() + " shelf";
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
        System.out.println("Order removed from " + getShelfName() + " to clear space: " + order.getShortId());
        return order;
    }
}
