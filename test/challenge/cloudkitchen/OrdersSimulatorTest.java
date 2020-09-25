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
    Order order1;
    Order order2;
    Order order3;
    Order order4;
    Order order5;

    @Before
    public void setup() {
        orders = new ArrayList<>();
        order1 = new Order("0-0-0-0-1", "order1", "frozen", 300, 0.61);
        order2 = new Order("0-0-0-0-2", "order2", "cold", 269, 0.19);
        order3 = new Order("0-0-0-0-3", "order3", "hot", 251, 0.22);
        order4 = new Order("0-0-0-0-4", "order4", "frozen", 251, 0.22);
        order5 = new Order("0-0-0-0-5", "order5", "hot", 251, 0.22);
        orders.addAll(Arrays.asList(order1, order2, order3, order4, order5));
        sim = new OrdersSimulator(2, orders);
        sim.frozenShelf.add(order1);
        sim.coldShelf.add(order2);
        sim.hotShelf.add(order3);
        sim.overflowShelf.add(order4);
    }

    @Test
    public void testGetOrdersIdsStr() {
        Assert.assertEquals("1(FROZEN) 2(COLD) 3(HOT) 4(FROZEN) 5(HOT) ", sim.getOrdersIdsStr(orders));
    }

    @Test
    public void testGetMovableOrder() {
        Order order = sim.getMovableOrder();
        Assert.assertEquals("4", order.getShortId());
        sim.frozenShelf.capacity = 1;
        order = sim.getMovableOrder();
        Assert.assertNull(order);
    }

    @Test
    public void testGetShelf() {
        Assert.assertEquals(sim.frozenShelf, sim.getShelf(order1));
        Assert.assertEquals(sim.coldShelf, sim.getShelf(order2));
        Assert.assertEquals(sim.hotShelf, sim.getShelf(order3));
    }

    @Test
    public void testGetNextOrderBatch() {
        List<Order> orders = sim.getNextOrderBatch(1);
        Assert.assertEquals(2, orders.size());
        Assert.assertEquals(order2, orders.get(0));
        Assert.assertEquals(order3, orders.get(1));
    }

    @Test
    public void testMakeRoomOnOverflowShelf() {
        // Make room by moving order to another shelf
        sim.makeRoomOnOverflowShelf();
        Assert.assertEquals(0, sim.overflowShelf.getCurrentOrders().size());

        // Make room by remove a random order
        sim.overflowShelf.add(order4);
        sim.overflowShelf.add(order5);
        sim.hotShelf.capacity = 1;
        sim.makeRoomOnOverflowShelf();
        Assert.assertEquals(1, sim.overflowShelf.getCurrentOrders().size());
    }

    @Test
    public void testIngest() {

    }
}
