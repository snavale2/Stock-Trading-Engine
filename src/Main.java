public class Main {
    public static void main(String[] args) {
        TradingSimulation simulation = new TradingSimulation();
        
        if (args.length > 0 && args[0].equals("--test")) {
            simulation.runTestMode();
        } else {
            simulation.startSimulation();
        }
    }
}
