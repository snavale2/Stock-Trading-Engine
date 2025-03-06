import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;
import java.io.*;

/**
 * Main class that simulates a stock trading environment.
 * Handles order generation, execution, and test scenarios.
 */
class TradingSimulation {
    private final Random random = new Random();
    private final StockExchange exchange = new StockExchange();
    private final Properties properties = new Properties();
    private static FileWriter outputWriter;
    private final AtomicInteger orderCount = new AtomicInteger(0);
    private final int totalOrders;

    /**
     * Initializes the simulation by setting up output, loading properties,
     * and initializing stock tickers.
     */
    public TradingSimulation() {
        setupOutput();
        loadProperties();
        initializeTickers();
        String totalOrdersStr = properties.getProperty("totalOrders");
        // If totalOrders is empty, null, or invalid, set to -1 for infinite orders
        totalOrders = (totalOrdersStr != null && !totalOrdersStr.trim().isEmpty()) ? 
            Integer.parseInt(totalOrdersStr) : -1;
    }

    private void setupOutput() {
        try {
            outputWriter = new FileWriter("trading_output.txt", false);
        } catch (IOException e) {
            Logger.logError("Failed to setup output file", e);
        }
    }

    public static void writeOutput(String message) {
        try {
            outputWriter.write(message + "\n");
            outputWriter.flush();
        } catch (IOException e) {
            Logger.logError("Failed to write output", e);
        }
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                Logger.logError("Unable to find config.properties", new FileNotFoundException("config.properties not found"));
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            Logger.logError("Error loading properties", ex);
        }
    }

    private void initializeTickers() {
        for (int i = 0; i < StockExchange.TICKERS.length; i++) {
            StockExchange.TICKERS[i] = "STOCK" + i;
        }
    }

    /**
     * Generates a random order with randomized type, ticker, quantity, and price.
     * @return A new random Order object
     */
    private Order generateRandomOrder() {
        Order.OrderType type = random.nextBoolean() ? Order.OrderType.BUY : Order.OrderType.SELL;
        String ticker = StockExchange.TICKERS[random.nextInt(StockExchange.TICKERS.length)];
        int quantity = (random.nextInt(10) + 1) * 10; // Random multiple of 10 shares  //hard-coded value
        double price = Math.round((100 + random.nextDouble() * 100) * 100.0) / 100.0;
        return new Order(type, ticker, quantity, price);
    }

    /**
     * Starts the trading simulation with multiple threads generating and processing orders.
     * Runs until totalOrders is reached or indefinitely if totalOrders is -1.
     */
    public void startSimulation() {
        int threadPoolSize = Integer.parseInt(properties.getProperty("threadPoolSize", "5"));
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        for (int i = 0; i < threadPoolSize; i++) {
            executor.execute(() -> {
                // Will run infinitely if totalOrders is -1
                while (totalOrders == -1 || orderCount.get() < totalOrders) {
                    Order order = generateRandomOrder();
                    exchange.addOrder(order.ticker, order);
                    orderCount.incrementAndGet();
                    try {
                        Thread.sleep(random.nextInt(500)); // Simulate real-time delay //hard-coded value
                    } catch (InterruptedException e) {
                        Logger.logError("Thread interrupted", e);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
            outputWriter.close();
        } catch (InterruptedException | IOException e) {
            Logger.logError("Error during shutdown", e);
        }
    }

    /**
     * Creates a set of predefined test orders to validate the trading engine.
     * @return Array of test orders covering various trading scenarios
     */
    private Order[] getTestOrders() {
        return new Order[] {
            // Test Case 1: Simple match
            new Order(Order.OrderType.SELL, "STOCK1", 100, 10.0),  // Sell 100 shares at $10
            new Order(Order.OrderType.BUY, "STOCK1", 100, 10.0),   // Buy 100 shares at $10
            
            // Test Case 2: Partial match
            new Order(Order.OrderType.SELL, "STOCK2", 100, 20.0),  // Sell 100 shares at $20
            new Order(Order.OrderType.BUY, "STOCK2", 50, 25.0),    // Buy 50 shares at $25
            
            // Test Case 3: No match (price mismatch)
            new Order(Order.OrderType.SELL, "STOCK3", 100, 30.0),  // Sell 100 shares at $30
            new Order(Order.OrderType.BUY, "STOCK3", 100, 25.0),   // Buy 100 shares at $25
            
            // Test Case 4: Multiple matches
            new Order(Order.OrderType.SELL, "STOCK4", 100, 40.0),  // Sell 100 shares at $40
            new Order(Order.OrderType.BUY, "STOCK4", 40, 45.0),    // Buy 40 shares at $45
            new Order(Order.OrderType.BUY, "STOCK4", 60, 45.0),    // Buy 60 shares at $45
            
            // Test Case 5: Different tickers
            new Order(Order.OrderType.SELL, "STOCK5", 100, 50.0),  // Sell 100 shares of STOCK5
            new Order(Order.OrderType.BUY, "STOCK6", 100, 50.0)    // Buy 100 shares of STOCK6
        };
    }

    public void runTestMode() {
        Order[] testOrders = getTestOrders();
        for (Order order : testOrders) {
            exchange.addOrder(order.ticker, order);
            try {
                Thread.sleep(100); // Small delay between orders
            } catch (InterruptedException e) {
                Logger.logError("Thread interrupted during test", e);
            }
        }
        try {
            outputWriter.close();
        } catch (IOException e) {
            Logger.logError("Error closing output writer", e);
        }
    }

}
