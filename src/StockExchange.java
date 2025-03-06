import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

class StockExchange {
    private static final Properties properties = new Properties();
    private static final int TOTAL_TICKERS;
    public static String[] TICKERS;

    static {
        try (InputStream input = StockExchange.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                Logger.logError("Unable to find config.properties", new FileNotFoundException("config.properties not found"));
                TOTAL_TICKERS = 1024;
            } else {
                properties.load(input);
                TOTAL_TICKERS = Integer.parseInt(properties.getProperty("totalTickers", "1024"));
            }
        } catch (IOException ex) {
            Logger.logError("Error loading stock exchange properties", ex);
            throw new ExceptionInInitializerError(ex);
        }
        TICKERS = new String[TOTAL_TICKERS];
    }

    private final OrderBook[] orderBooks = new OrderBook[TOTAL_TICKERS];

    public StockExchange() {
        for (int i = 0; i < TOTAL_TICKERS; i++) {
            orderBooks[i] = new OrderBook();
        }
    }

    private int getStockIndex(String ticker) {
        for (int i = 0; i < TOTAL_TICKERS; i++) {
            if (TICKERS[i].equals(ticker)) {
                return i;
            }
        }
        return -1;
    }

    public void addOrder(String ticker, Order order) {
        int index = getStockIndex(ticker);
        if (index == -1) {
            TradingSimulation.writeOutput("Warning: Ticker " + ticker + " does not exist.");
            return;
        }
        orderBooks[index].addOrder(order);
    }
}
