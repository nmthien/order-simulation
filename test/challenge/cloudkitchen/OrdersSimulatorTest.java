package challenge.cloudkitchen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrdersSimulatorTest {

    List<Order> orders;
    OrdersSimulator sim;
    Order frozenOrder1;
    Order coldOrder1;
    Order hotOrder1;
    Order frozenOrder2;
    Order hotOrder2;

    @Before
    public void setup() {
        frozenOrder1 = new Order("0-0-0-0-1", "order1", "frozen", 300, 0.61);
        frozenOrder1.arrive(0);
        frozenOrder1.setTimePickedUp(10);
        coldOrder1 = new Order("0-0-0-0-2", "order2", "cold", 269, 0.19);
        coldOrder1.arrive(0);
        coldOrder1.setTimePickedUp(10);
        hotOrder1 = new Order("0-0-0-0-3", "order3", "hot", 251, 0.22);
        hotOrder1.arrive(0);
        hotOrder1.setTimePickedUp(10);
        frozenOrder2 = new Order("0-0-0-0-4", "order4", "frozen", 251, 0.22);
        frozenOrder2.arrive(0);
        frozenOrder2.setTimePickedUp(10);
        hotOrder2 = new Order("0-0-0-0-5", "order5", "hot", 251, 0.22);
        hotOrder2.arrive(0);
        hotOrder2.setTimePickedUp(10);
        orders = Arrays.asList(frozenOrder1, coldOrder1, hotOrder1, frozenOrder2, hotOrder2);
        sim = new OrdersSimulator(2, orders);
        sim.frozenShelf.add(frozenOrder1);
        sim.coldShelf.add(coldOrder1);
        sim.hotShelf.add(hotOrder1);
        sim.overflowShelf.add(frozenOrder2);
    }

    @Test
    public void testGetOrdersIdsStr() {
        Assert.assertEquals("1(FROZEN) 2(COLD) 3(HOT) 4(FROZEN) 5(HOT) ", sim.getOrdersIdsStr(orders));
    }

    @Test
    public void testGetMovableOrder() {
        // Successfully get an order to move from overflow to frozen shelf
        // 2 orders on overflowShelf: frozenOrder1 (arrived at 0), frozenOrder2 (arrived at 0).
        // At time 10, inherent values of frozenOrder1 and frozenOrder2 are 0.926 and 0.943.
        // Getting movable order from this shelf should return frozenOrder1
        sim.overflowShelf.currentOrders.clear();
        sim.overflowShelf.currentOrders.add(frozenOrder1);
        sim.overflowShelf.currentOrders.add(frozenOrder2);
        Order order = sim.getMovableOrder(10);
        Assert.assertEquals(frozenOrder1, order);

        // Not able to get an order to move because the corresponding shelf is full
        sim.frozenShelf.capacity = 1;
        order = sim.getMovableOrder(10);
        Assert.assertNull(order);
    }

    @Test
    public void testGetShelf() {
        Assert.assertEquals(sim.frozenShelf, sim.getShelf(frozenOrder1));
        Assert.assertEquals(sim.coldShelf, sim.getShelf(coldOrder1));
        Assert.assertEquals(sim.hotShelf, sim.getShelf(hotOrder1));
    }

    @Test
    public void testGetNextOrderBatch() {
        List<Order> orders = sim.getNextOrderBatch(1);
        Assert.assertEquals(2, orders.size());
        Assert.assertEquals(coldOrder1, orders.get(0));
        Assert.assertEquals(hotOrder1, orders.get(1));
    }

    @Test
    public void testMakeRoomOnOverflowShelf() {
        // Make room by moving order to another shelf
        sim.makeRoomOnOverflowShelf(10);
        Assert.assertEquals(0, sim.overflowShelf.getCurrentOrders().size());

        // Make room by remove a random order
        sim.overflowShelf.add(frozenOrder2);
        sim.overflowShelf.add(hotOrder2);
        sim.hotShelf.capacity = 1;
        sim.makeRoomOnOverflowShelf(10);
        Assert.assertEquals(1, sim.overflowShelf.getCurrentOrders().size());
    }

    @Test
    public void testIngest() {
        // Successfully ingest two new orders to single temperature shelves.
        Order newHotOrder1 = new Order("0-0-0-new-1", "new order 1", "hot", 251, 0.22);
        Order newColdOrder1 = new Order("0-0-0-new-2", "new order 2", "cold", 251, 0.22);
        List<Order> batch = Arrays.asList(newHotOrder1, newColdOrder1);
        sim.ingest(batch, 0);
        Assert.assertEquals(2, sim.hotShelf.getCurrentOrders().size());
        Assert.assertEquals(2, sim.coldShelf.getCurrentOrders().size());
        Assert.assertEquals("0-0-0-new-1", sim.hotShelf.getCurrentOrders().get(1).getId());
        Assert.assertEquals("0-0-0-new-2", sim.coldShelf.getCurrentOrders().get(1).getId());

        // Successfully ingest a new order by moving an order from overflow shelf to single temperature shelf
        // hotShelf (full): hotOrder1
        // overflowShelf (full): frozenOrder1
        // ingesting hotOrder2 requires moving frozenOrder1 to frozenShelf and adding hotOrder2 to overflow shelf
        sim.hotShelf.currentOrders.clear();
        sim.coldShelf.currentOrders.clear();
        sim.frozenShelf.currentOrders.clear();
        sim.overflowShelf.currentOrders.clear();
        sim.hotShelf.add(hotOrder1);
        sim.hotShelf.capacity = 1;
        sim.overflowShelf.add(frozenOrder1);
        sim.overflowShelf.capacity = 1;
        batch = Arrays.asList(hotOrder2);
        sim.ingest(batch, 0);
        Assert.assertEquals(1, sim.frozenShelf.getCurrentOrders().size());
        Assert.assertEquals(frozenOrder1, sim.frozenShelf.getCurrentOrders().get(0));
        Assert.assertEquals(1, sim.overflowShelf.getCurrentOrders().size());
        Assert.assertEquals(hotOrder2, sim.overflowShelf.getCurrentOrders().get(0));

        // Successfully ingest a new order by removing an order from overflow shelf
        // hotShelf (full): hotOrder1
        // frozenShelf (full): frozenOrder2
        // overflowShelf (full): frozenOrder1
        // ingesting hotOrder2 requires removing frozenOrder1 and adding hotOrder2 to overflow shelf
        sim.hotShelf.currentOrders.clear();
        sim.coldShelf.currentOrders.clear();
        sim.frozenShelf.currentOrders.clear();
        sim.overflowShelf.currentOrders.clear();
        sim.hotShelf.add(hotOrder1);
        sim.hotShelf.capacity = 1;
        sim.frozenShelf.add(frozenOrder2);
        sim.frozenShelf.capacity = 1;
        sim.overflowShelf.add(frozenOrder1);
        sim.overflowShelf.capacity = 1;
        batch = Arrays.asList(hotOrder2);
        sim.ingest(batch, 0);
        Assert.assertEquals(1, sim.hotShelf.getCurrentOrders().size());
        Assert.assertEquals(hotOrder1, sim.hotShelf.getCurrentOrders().get(0));
        Assert.assertEquals(1, sim.frozenShelf.getCurrentOrders().size());
        Assert.assertEquals(frozenOrder2, sim.frozenShelf.getCurrentOrders().get(0));
        Assert.assertEquals(1, sim.overflowShelf.getCurrentOrders().size());
        Assert.assertEquals(hotOrder2, sim.overflowShelf.getCurrentOrders().get(0));
    }
}
