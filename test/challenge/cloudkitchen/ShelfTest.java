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
        order2 = new Order("0-0-0-0-2", "order2", "cold", 269, 0.19);
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
}
