import java.util.concurrent.atomic.AtomicReference;

/**
 * Maintains buy and sell orders for a single stock ticker.
 * Implements thread-safe order matching using atomic references.
 */
class OrderBook {
    private final AtomicReference<Order> buyRef;
    private final AtomicReference<Order> sellRef;

    public OrderBook() {
        buyRef = new AtomicReference<>(null);
        sellRef = new AtomicReference<>(null);
    }

    /**
     * Adds a new order to the order book and attempts to match it.
     * @param order The order to be added and processed
     */
    public void addOrder(Order order) {
        TradingSimulation.writeOutput("Adding Order: " + order);
        if (order.orderType == Order.OrderType.BUY) {
            insertSortedBuy(order);
        } else {
            insertSortedSell(order);
        }
        matchOrders();
    }

    /**
     * Inserts a buy order into the sorted buy orders list.
     * Orders are sorted by price in descending order.
     * @param order The buy order to insert
     */
    private void insertSortedBuy(Order order) {    //check if the better approach is to use a single method for both buy and sell and for sorting
        Order prev = null, curr = buyRef.get();
        while (curr != null && curr.price >= order.price) {
            prev = curr;
            curr = curr.next;
        }
        order.next = curr;
        if (prev == null) {
            buyRef.set(order);
        } else {
            prev.next = order;
        }
    }

    /**
     * Inserts a sell order into the sorted sell orders list.
     * Orders are sorted by price in ascending order.
     * @param order The sell order to insert
     */
    private void insertSortedSell(Order order) {
        Order prev = null, curr = sellRef.get();
        while (curr != null && curr.price <= order.price) {
            prev = curr;
            curr = curr.next;
        }
        order.next = curr;
        if (prev == null) {
            sellRef.set(order);
        } else {
            prev.next = order;
        }
    }

    /**
     * Matches compatible buy and sell orders.
     * Uses atomic operations to ensure thread safety.
     * Executes trades when buy price >= sell price.
     */
    private void matchOrders() {
        while (true) {
            Order buyOrder = buyRef.get();
            Order sellOrder = sellRef.get();
            
            if (buyOrder == null || sellOrder == null) {
                return;
            }

            if (buyOrder.ticker.equals(sellOrder.ticker) && buyOrder.price >= sellOrder.price) {
                int tradedQuantity = Math.min(buyOrder.quantity, sellOrder.quantity);
                
                // Update quantities atomically
                buyOrder.quantity -= tradedQuantity;
                sellOrder.quantity -= tradedQuantity;
                
                // Remove orders with zero quantity
                if (buyOrder.quantity == 0) {
                    if (!buyRef.compareAndSet(buyOrder, buyOrder.next)) {
                        continue; // Retry if CAS fails
                    }
                }
                
                if (sellOrder.quantity == 0) {
                    if (!sellRef.compareAndSet(sellOrder, sellOrder.next)) {
                        continue; // Retry if CAS fails
                    }
                }
                TradingSimulation.writeOutput("Trade Executed: " + tradedQuantity + " shares of " +
                     buyOrder.ticker +" BuyOrder "+buyOrder.quantity+ " SellOrder "+ sellOrder.quantity + " at $" + sellOrder.price);

            } else {
                return; // No match possible
            }
        }
    }    
}
