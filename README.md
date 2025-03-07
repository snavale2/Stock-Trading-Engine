Name: Shubham Navale 
Email: snavale@binghamton.edu 

# Stock Trading Engine

A multi-threaded stock trading simulation engine that processes buy and sell orders for multiple stock tickers in real-time.

## Directory Structure

```
Stock-Trading-Engine/
├── src/
│   ├── Main.java              # Application entry point
│   ├── TradingSimulation.java # Core simulation logic
│   ├── StockExchange.java     # Order matching engine
│   ├── OrderBook.java         # Manages orders per ticker
│   ├── Order.java             # Order entity class
│   ├── Logger.java            # Logging utility
│   └── config.properties      # Configuration settings
├── bin/                       # Compiled class files
└── .vscode/                   # VSCode configuration
```

## Configuration Setup

Before running the project, configure the simulation parameters in `src/config.properties`:

```properties
# Number of stock symbols to simulate
totalTickers=1024

# Number of concurrent order-processing threads
threadPoolSize=5

# Total orders to generate (leave blank for infinite)
totalOrders=
```

## Building and Running

Navigate to the project root directory (Stock-Trading-Engine) in your terminal and execute the following commands:

1. **Create the bin directory** (if it doesn't exist):
```bash
mkdir bin
```

2. **Compile all Java files**:
```bash
javac -d bin src/*.java
```

3. **Run the application**:
- For normal mode:
```bash
java -cp bin Main
```
- For test mode:
```bash
java -cp bin Main --test
```

## Output Format

### trading_output.txt
Records all orders and trades in the following format:

**Orders:**
```
TYPE    | TICKER    | SHARES | PRICE
BUY     | STOCK1     | 100    | 150.50
SELL    | STOCK1     | 50     | 151.00
```

**Trades:**
```
TRADE   | TICKER    | SHARES | PRICE   | Buy: X | Sell: Y
```
Example:
```
TRADE   | STOCK1     | 50     | 150.75  | Buy: 50 | Sell: 0
```
In this example:
- 50 shares were traded
- Buy order has 50 shares remaining
- Sell order has 0 shares remaining

### trading_error.log
Contains error messages and stack traces for debugging purposes.