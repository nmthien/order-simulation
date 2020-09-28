package challenge.cloudkitchen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShelfTest {

    Shelf overflowShelf;
    Shelf hotShelf;
    Order frozenOrder1, coldOrder1, hotOrder1, frozenOrder2, hotOrder2;

    @Before
    public void setup() {
        frozenOrder1 = new Order("0-0-0-0-1", "order1", "frozen", 300, 0.61);
        frozenOrder1.arrive(0);
        frozenOrder1.setTimePickedUp(10);
        coldOrder1 = new Order("0-0-0-0-2", "order2", "cold", 10, 0.19);
        coldOrder1.arrive(0);
        coldOrder1.setTimePickedUp(10);
        hotOrder1 = new Order("0-0-0-0-3", "order3", "hot", 251, 0.22);
        hotOrder1.arrive(0);
        hotOrder1.setTimePickedUp(10);
        frozenOrder2 = new Order("0-0-0-0-4", "order4", "frozen", 251, 0.22);
        frozenOrder2.arrive(0);
        frozenOrder2.setTimePickedUp(10);
        hotOrder2 = new Order("0-0-0-0-4", "order4", "hot", 251, 0.22);
        hotOrder2.arrive(0);
        hotOrder2.setTimePickedUp(10);
        overflowShelf = new Shelf(Constants.ShelfType.OVERFLOW);
        hotShelf = new Shelf(Constants.ShelfType.HOT);
    }

    @Test
    public void testComputeInherentValue() {
        frozenOrder1.arrive(0);
        hotOrder1.arrive(1);
        Assert.assertEquals(0.9408, overflowShelf.computeInherentValue(frozenOrder1, 8), 0.0001);
        Assert.assertEquals(0.9570, hotShelf.computeInherentValue(frozenOrder1, 8), 0.0001);
        Assert.assertEquals(0.9598, overflowShelf.computeInherentValue(hotOrder1, 8), 0.0001);
        Assert.assertEquals(0.9659, hotShelf.computeInherentValue(hotOrder1, 8), 0.0001);
    }

    @Test
    public void testCleanUpWastedOrders() {
        frozenOrder1.arrive(0);
        coldOrder1.arrive(0);
        overflowShelf.add(frozenOrder1);
        overflowShelf.add(coldOrder1);
        overflowShelf.cleanUpWastedOrders(10);
        Assert.assertEquals(1, overflowShelf.getCurrentOrders().size());
        Assert.assertEquals(frozenOrder1, overflowShelf.getCurrentOrders().get(0));
    }

    @Test
    public void testCleanUpDeliveredOrders() {
        // All delivered orders in currentOrders list of a shelf should be removed
        frozenOrder1.arrive(0);
        frozenOrder1.setTimePickedUp(10);
        coldOrder1.arrive(0);
        coldOrder1.setTimePickedUp(15);
        hotOrder1.arrive(1);
        hotOrder1.setTimePickedUp(8);
        overflowShelf.add(frozenOrder1);
        overflowShelf.add(coldOrder1);
        overflowShelf.add(hotOrder1);
        overflowShelf.cleanUpDeliveredOrders(10);
        Assert.assertEquals(1, overflowShelf.getCurrentOrders().size());
        Assert.assertEquals(coldOrder1, overflowShelf.getCurrentOrders().get(0));
    }

    @Test
    public void testRemoveRandomOrder() {
        // Removing a random order returns the removed order
        overflowShelf.add(frozenOrder1);
        overflowShelf.add(coldOrder1);
        overflowShelf.add(hotOrder1);
        Order order = overflowShelf.removeRandomOrder();
        Assert.assertEquals(2, overflowShelf.currentOrders.size());
        Assert.assertNotEquals(order.getId(), overflowShelf.currentOrders.get(0).getId());
        Assert.assertNotEquals(order.getId(), overflowShelf.currentOrders.get(1).getId());

        // Removing a random order when list of order is empty should return null
        overflowShelf.currentOrders.clear();
        order = overflowShelf.removeRandomOrder();
        Assert.assertNull(order);
    }

    @Test
    public void testAdd() {
        // Successfully add an order to shelf
        hotShelf.add(hotOrder1);
        boolean added = hotShelf.add(hotOrder2);
        Assert.assertTrue(added);
        Assert.assertEquals(2, hotShelf.currentOrders.size());

        // Failed to add an order because it doesn't have arrive or pick up time
        hotShelf.currentOrders.clear();
        Order newOrder = new Order("0-0-0-0-3", "new order", "hot", 251, 0.22);
        Assert.assertFalse(hotShelf.add(newOrder));
        Assert.assertEquals(0, hotShelf.getCurrentOrders().size());

        // Failed to add an order because shelf has reached capacity
        hotShelf.currentOrders.clear();
        hotShelf.capacity = 1;
        hotShelf.add(hotOrder1);
        added = hotShelf.add(hotOrder2);
        Assert.assertFalse(added);
        Assert.assertEquals(1, hotShelf.currentOrders.size());

        // Failed to add an order because order's already on shelf
        hotShelf.currentOrders.clear();
        hotShelf.capacity = 2;
        hotShelf.add(hotOrder1);
        added = hotShelf.add(hotOrder1);
        Assert.assertFalse(added);
        Assert.assertEquals(1, hotShelf.currentOrders.size());

        // Failed to add an order because order's temperature is different from shelf's
        hotShelf.currentOrders.clear();
        hotShelf.capacity = 2;
        added = hotShelf.add(frozenOrder1);
        Assert.assertFalse(added);
        Assert.assertEquals(0, hotShelf.currentOrders.size());
    }

    @Test
    public void testRemove() {
        // Successfully remove an order from shelf
        overflowShelf.add(frozenOrder1);
        overflowShelf.add(coldOrder1);
        overflowShelf.add(hotOrder1);
        boolean removed = overflowShelf.remove(coldOrder1);
        Assert.assertTrue(removed);
        Assert.assertEquals(2, overflowShelf.currentOrders.size());

        // Removal failed because order is not on shelf
        removed = overflowShelf.remove(coldOrder1);
        Assert.assertFalse(removed);
        Assert.assertEquals(2, overflowShelf.currentOrders.size());
    }
}
