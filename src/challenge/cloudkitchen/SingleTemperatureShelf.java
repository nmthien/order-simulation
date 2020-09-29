package challenge.cloudkitchen;

import challenge.cloudkitchen.Constants.*;

import static challenge.cloudkitchen.Constants.SHELF_DECAY_MODIFIER_SINGLE_TEMPERATURE;
import static challenge.cloudkitchen.Constants.SINGLE_TEMPERATURE_SHELF_CAPACITY;

/**
 * SingleTemperatureShelf contains orders of a single temperature. Adding an order of different temperature than
 * a shelf's would result in failure.
 */
public class SingleTemperatureShelf extends Shelf {

    Temperature temperature;

    public SingleTemperatureShelf(Temperature type) {
        super();
        temperature = type;
        capacity = SINGLE_TEMPERATURE_SHELF_CAPACITY;
        shelfDecayModifier = SHELF_DECAY_MODIFIER_SINGLE_TEMPERATURE;
    }

    @Override
    String getShelfName() {
        return temperature.name() + " shelf";
    }

    /**
     * Remove a random order from shelf to clear space for incoming orders.
     *
     * @return order that was removed.
     */
    @Override
    public Order removeRandomOrder() {
        throw new UnsupportedOperationException("Cannot remove random order from single temperature shelf");
    }

    /**
     * Add order to shelf. If the order is already on shelf or doesn't have arrival or pick-up time,
     * it won't be added again.
     *
     * @param order the order to be added
     * @return true if order was added successfully, false otherwise.
     */
    public boolean add(Order order) {
        if (temperature != order.getTemp()) {
            return false;
        }
        return super.add(order);
    }
}
