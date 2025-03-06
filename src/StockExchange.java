class StockExchange {
    private static final int TOTAL_TICKERS = 1024; //use properties file to read this value
    public static String[] TICKERS = new String[TOTAL_TICKERS]; // Predefined tickers //rather than using public static try getter or setter
    private final OrderBook[] orderBooks = new OrderBook[TOTAL_TICKERS];

    public StockExchange() {
        for (int i = 0; i < TOTAL_TICKERS; i++) {
            orderBooks[i] = new OrderBook(); // Initialize order books
        }
    }

    private int getStockIndex(String ticker) {
        for (int i = 0; i < TOTAL_TICKERS; i++) {
            if (TICKERS[i].equals(ticker)) {
                return i; // O(1) lookup, since tickers are pre-defined
            }
        }
        return -1; // Invalid ticker (not expected)
    }

    public void addOrder(String ticker, Order order) {
        int index = getStockIndex(ticker);
        if (index == -1) return;   //dont directly return log that this ticker doesnt exist
        orderBooks[index].addOrder(order);
    }
}
