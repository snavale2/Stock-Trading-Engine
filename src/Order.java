class Order {
    enum OrderType { BUY, SELL }

    OrderType orderType;
    String ticker;
    int quantity;
    double price;
    volatile Order next; // For lock-free linked list

    public Order(OrderType orderType, String ticker, int quantity, double price) {  //dont use this. use orderTypeIn, tickerIn, quantityIn, priceIn
        this.orderType = orderType;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.next = null;
    }
}
