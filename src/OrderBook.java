import java.util.concurrent.atomic.AtomicReference;

class OrderBook {
    private volatile Order buyHead;   //never used this variable
    private volatile Order sellHead;
    private final AtomicReference<Order> buyRef;
    private final AtomicReference<Order> sellRef;

    public OrderBook() {
        buyRef = new AtomicReference<>(null);
        sellRef = new AtomicReference<>(null);
    }

    public void addOrder(Order order) {
        if (order.orderType == Order.OrderType.BUY) {
            insertSortedBuy(order);
        } else {
            insertSortedSell(order);
        }
        matchOrders();
    }

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

    private void matchOrders() {
        while (buyRef.get() != null && sellRef.get() != null &&
                buyRef.get().price >= sellRef.get().price) {

            Order buyOrder = buyRef.get();
            Order sellOrder = sellRef.get();

            int matchedQuantity = Math.min(buyOrder.quantity, sellOrder.quantity);
            buyOrder.quantity -= matchedQuantity;
            sellOrder.quantity -= matchedQuantity;

            System.out.println("Trade Executed: " + matchedQuantity + " shares of " +
                    buyOrder.ticker + " at $" + sellOrder.price);    //dont print use log file for this

            if (buyOrder.quantity == 0) {
                buyRef.set(buyOrder.next);
            }
            if (sellOrder.quantity == 0) {
                sellRef.set(sellOrder.next);
            }
        }
    }
}
