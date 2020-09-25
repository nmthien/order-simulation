package challenge.cloudkitchen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShelfTest {

    Shelf overflowShelf;
    Shelf hotShelf;
    Order order1;
    Order order2;
    Order order3;
    Order order4;

    @Before
    public void setup() {
        order1 = new Order("0-0-0-0-1", "order1", "frozen", 300, 0.61);
        order2 = new Order("0-0-0-0-2", "order2", "cold", 10, 0.19);
        order3 = new Order("0-0-0-0-3", "order3", "hot", 251, 0.22);
        order4 = new Order("0-0-0-0-4", "order4", "frozen", 251, 0.22);
        overflowShelf = new Shelf(Constants.ShelfType.OVERFLOW);
        hotShelf = new Shelf(Constants.ShelfType.HOT);
    }

    @Test
    public void testComputeInherentValue() {
        order1.arrive(0);
        order3.arrive(1);
        Assert.assertEquals(0.9408, overflowShelf.computeInherentValue(order1, 8), 0.0001);
        Assert.assertEquals(0.9570, hotShelf.computeInherentValue(order1, 8), 0.0001);
        Assert.assertEquals(0.9598, overflowShelf.computeInherentValue(order3, 8), 0.0001);
        Assert.assertEquals(0.9659, hotShelf.computeInherentValue(order3, 8), 0.0001);
    }

    @Test
    public void testCleanUpWastedOrders() {
        order1.arrive(0);
        order2.arrive(0);
        overflowShelf.add(order1);
        overflowShelf.add(order2);
        overflowShelf.cleanUpWastedOrders(10);
        Assert.assertEquals(1, overflowShelf.getCurrentOrders().size());
        Assert.assertEquals(order1, overflowShelf.getCurrentOrders().get(0));
    }

    @Test
    public void testCleanUpDeliveredOrders() {
        order1.arrive(0);
        order1.setTimePickedUp(10);
        order2.arrive(0);
        order2.setTimePickedUp(15);
        order3.arrive(1);
        order3.setTimePickedUp(8);
        overflowShelf.add(order1);
        overflowShelf.add(order2);
        overflowShelf.add(order3);
        overflowShelf.cleanUpDeliveredOrders(10);
        Assert.assertEquals(1, overflowShelf.getCurrentOrders().size());
        Assert.assertEquals(order2, overflowShelf.getCurrentOrders().get(0));
    }

    @Test
    public void testRemoveRandomOrder() {
        hotShelf.add(order1);
        hotShelf.add(order2);
        hotShelf.add(order3);
        Order order = hotShelf.removeRandomOrder();
        Assert.assertEquals(2, hotShelf.currentOrders.size());
        Assert.assertNotEquals(order.getId(), hotShelf.currentOrders.get(0).getId());
        Assert.assertNotEquals(order.getId(), hotShelf.currentOrders.get(1).getId());
    }

    @Test
    public void testAdd() {
        hotShelf.add(order1);
        hotShelf.add(order2);
        boolean added = hotShelf.add(order3);
        Assert.assertTrue(added);
        Assert.assertEquals(3, hotShelf.currentOrders.size());
        hotShelf.capacity = 3;
        added = hotShelf.add(order4);
        Assert.assertFalse(added);
        Assert.assertEquals(3, hotShelf.currentOrders.size());
    }

    @Test
    public void testRemove() {
        overflowShelf.add(order1);
        overflowShelf.add(order2);
        overflowShelf.add(order3);
        boolean removed = overflowShelf.remove(order2);
        Assert.assertTrue(removed);
        Assert.assertEquals(2, overflowShelf.currentOrders.size());
        removed = overflowShelf.remove(order2);
        Assert.assertFalse(removed);
        Assert.assertEquals(2, overflowShelf.currentOrders.size());
    }
}
