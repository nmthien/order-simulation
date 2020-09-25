package challenge.cloudkitchen;

import org.apache.commons.cli.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static challenge.cloudkitchen.Constants.MAX_TIME_PICK_UP;
import static challenge.cloudkitchen.Constants.MIN_TIME_PICK_UP;

public class OrdersSimulator {

    private final static Logger LOGGER = Logger.getLogger(OrdersSimulator.class.getName());

    List<Order> orders;
    Shelf hotShelf = new Shelf(Constants.ShelfType.HOT);
    Shelf coldShelf = new Shelf(Constants.ShelfType.COLD);
    Shelf frozenShelf = new Shelf(Constants.ShelfType.FROZEN);
    Shelf overflowShelf = new Shelf(Constants.ShelfType.OVERFLOW);
    int numOrder;
    int ingestionRate;

    public OrdersSimulator(int ingestionRate) {
        this(ingestionRate, new ArrayList<>());
    }

    public OrdersSimulator(int ingestionRate, List<Order> orders) {
        this.ingestionRate = ingestionRate;
        this.orders = orders;
        this.numOrder = orders.size();
    }

    public void run() throws InterruptedException {
        int count = 0;
        int timer = 0;
        while (count < numOrder) {
            LOGGER.info("Timestamp = " + timer + " ------------------------------");

            checkWastedOrder(timer);
            checkDeliveredOrder(timer);
            List<Order> orderBatch = getNextOrderBatch(count);
            ingest(orderBatch, timer);
            count += ingestionRate;
            printShelfContent();
            Thread.sleep(1000);
            timer++;
        }
    }

    /**
     * Print contents of all shelves to console.
     */
    void printShelfContent() {
        System.out.println(hotShelf.getShelfContent());
        System.out.println(coldShelf.getShelfContent());
        System.out.println(frozenShelf.getShelfContent());
        System.out.println(overflowShelf.getShelfContent());
    }

    /**
     * Check and clean up orders whose inherent values reach 0.
     *
     * @param time current simulated time.
     */
    void checkWastedOrder(int time) {
        hotShelf.cleanUpWastedOrders(time);
        coldShelf.cleanUpWastedOrders(time);
        frozenShelf.cleanUpWastedOrders(time);
        overflowShelf.cleanUpWastedOrders(time);
    }

    /**
     * Check and clean up delivered orders.
     * @param time current simulated time.
     */
    void checkDeliveredOrder(int time) {
        hotShelf.cleanUpDeliveredOrders(time);
        coldShelf.cleanUpDeliveredOrders(time);
        frozenShelf.cleanUpDeliveredOrders(time);
        overflowShelf.cleanUpDeliveredOrders(time);
    }

    /**
     * Get batch of orders to be ingested from list of orders, starting from given index.
     *
     * @param startingIndex starting index of orders to be added to next batch.
     * @return list of orders to be ingested.
     */
    List<Order> getNextOrderBatch(int startingIndex) {
        List<Order> batch =  new ArrayList<>();
        for (int i = 0; i < ingestionRate; i++) {
            if (startingIndex + i < numOrder) {
                batch.add(orders.get(startingIndex + i));
            }
        }
        return batch;
    }

    /**
     * Ingest orders to correct shelves.
     *
     * @param orders list of orders to be ingested.
     * @param time current simulated time.
     */
    void ingest(List<Order> orders, int time) {
        System.out.println("New orders: " + getOrdersIdsStr(orders));
        for (Order order : orders) {
            order.arrive(time);
            order.setTimePickedUp(
                    time + new Random().nextInt(MAX_TIME_PICK_UP - MIN_TIME_PICK_UP + 1) + MIN_TIME_PICK_UP);
            Shelf shelf = getShelf(order);
            if (shelf.add(order)) {
                continue;
            }
            if (overflowShelf.add(order)) {
                continue;
            }
            makeRoomOnOverflowShelf();
            overflowShelf.add(order);
        }
    }

    /**
     * Move an order to a single temperature shelf or remove a random order from overflow shelf to make room.
     */
    void makeRoomOnOverflowShelf() {
        Order movableOrder = getMovableOrder();
        if (movableOrder != null) {
            overflowShelf.remove(movableOrder);
            Shelf nextShelf = getShelf(movableOrder);
            nextShelf.add(movableOrder);
        } else {
            overflowShelf.removeRandomOrder();
        }
    }

    /**
     * Get a string representation of all orders in a list.
     *
     * @param orders list of orders.
     * @return string representation of all orders, in the format order_id1(order_temp1) order_id2(order_temp2)
     */
    String getOrdersIdsStr(List<Order> orders) {
        String str = "";
        for (Order order : orders) {
            str += order.getShortIdWithTemp() + " ";
        }
        return str;
    }

    /**
     * Get an order from overflow shelf that could be moved to a single temperature shelf.
     *
     * @return an order to be moved if possible. null if not possible.
     */
    Order getMovableOrder() {
        for (Order order : overflowShelf.getCurrentOrders()) {
            Shelf shelf = getShelf(order);
            if (shelf.isAvailable()) {
                return order;
            }
        }
        return null;
    }

    /**
     * Get shelf to put an order in based on order's temperature.
     *
     * @param order
     * @return shelf to put the order in.
     */
    Shelf getShelf(Order order) {
        if (order.getTemp().equals(Constants.ShelfType.HOT)) {
            return hotShelf;
        }
        if (order.getTemp().equals(Constants.ShelfType.COLD)) {
            return coldShelf;
        }
        if (order.getTemp().equals(Constants.ShelfType.FROZEN)) {
            return frozenShelf;
        }
        return null;
    }

    private static CommandLine getCommandLine(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "Input file path");
        input.setRequired(true);
        options.addOption(input);

        Option ingestionRate = new Option("r", "ingestionRate", true, "Ingestion rate");
        options.addOption(ingestionRate);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        return cmd;
    }

    public static void main(String[] args) {
        CommandLine cmd = getCommandLine(args);
        String inputFile = cmd.getOptionValue('i');
        int ingestionRate = Integer.parseInt(cmd.getOptionValue('r', "2"));
        List<Order> orders = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(inputFile)) {
            JSONArray ordersList = (JSONArray)jsonParser.parse(reader);
            for (int i = 0; i < ordersList.size(); i++) {
                orders.add(Order.fromJsonObject((JSONObject)ordersList.get(i)));
            }
            OrdersSimulator sim = new OrdersSimulator(ingestionRate, orders);
            sim.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}