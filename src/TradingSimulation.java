import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//why everything is static , check if better approach
//check for smaller input values
//check if it is really threadsafe using test case or examples dry run something
//Trade off maximum profit or maximum matching orders
//Check if better to priority queue for buy and sell orders rather than linked list and it is under constraints
class TradingSimulation {
    // private static final int TOTAL_TICKERS = 1024;
    // private static final String[] TICKERS = new String[TOTAL_TICKERS];
    private static final Random random = new Random();
    private static final StockExchange exchange = new StockExchange();

    // static {
    //     for (int i = 0; i < TOTAL_TICKERS; i++) {
    //         TICKERS[i] = "STOCK" + i; // Generating unique stock symbols
    //     }
    // }

    private static void initializeTickers() {
        for (int i = 0; i < StockExchange.TICKERS.length; i++) {
            StockExchange.TICKERS[i] = "STOCK" + i;  // Assigning stock symbols to the array
        }
    }

    private static Order generateRandomOrder() {
        Order.OrderType type = random.nextBoolean() ? Order.OrderType.BUY : Order.OrderType.SELL;
        String ticker = StockExchange.TICKERS[random.nextInt(StockExchange.TICKERS.length)];
        int quantity = (random.nextInt(10) + 1) * 10; // Random multiple of 10 shares  //hard-coded value
        double price = 100 + random.nextDouble() * 100; // Random price between 100-200 //hard-coded value

        return new Order(type, ticker, quantity, price);
    }

    public static void main(String[] args) {
        // Pre-populate the TICKERS array before starting the threads
        //Use singlton for intializing tickers use that in every other class where needed
        initializeTickers();
        //one thread is one stockbroker
        ExecutorService executor = Executors.newFixedThreadPool(5); //still hard coded value use properties file to read this value

        for (int i = 0; i < 5; i++) {  //still hard coded value use properties file to read this value
            executor.execute(() -> {
                while (true) {
                    Order order = generateRandomOrder();
                    exchange.addOrder(order.ticker, order);
                    try {
                        Thread.sleep(random.nextInt(500)); // Simulate real-time delay //hard-coded value
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        // doubt - should it stop or keep running or keep count of orders and then after limit stop the program
        executor.shutdown();
    }
}
