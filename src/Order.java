/**
 * Represents a single trading order in the system.
 * Contains order details and supports lock-free linked list implementation.
 */
class Order {
    /**
     * Defines the type of order (BUY or SELL)
     */
    enum OrderType { BUY, SELL }

    OrderType orderType;    // Type of order (BUY/SELL)
    String ticker;         // Stock ticker symbol
    int quantity;         // Number of shares
    double price;        // Price per share
    volatile Order next; // Next order in the linked list, volatile for thread safety

    /**
     * Creates a new order with the specified parameters
     * orderTypeIn: Type of order (BUY/SELL)
     * tickerIn: Stock ticker symbol
     * quantityIn: Number of shares
     * priceIn: Price per share
     */
    public Order(OrderType orderTypeIn, String tickerIn, int quantityIn, double priceIn) {
        orderType = orderTypeIn;
        ticker = tickerIn;
        quantity = quantityIn;
        price = priceIn;
        next = null;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderType=" + orderType +
                ", ticker='" + ticker + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
