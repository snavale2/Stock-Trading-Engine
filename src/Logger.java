import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "trading_error.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static PrintWriter logWriter;

    static {
        try {
            logWriter = new PrintWriter(new FileWriter(LOG_FILE, false));
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static void logError(String message, Exception e) {
        if (logWriter != null) {
            String timestamp = LocalDateTime.now().format(formatter);
            logWriter.println(timestamp + " - ERROR: " + message);
            e.printStackTrace(logWriter);
            logWriter.flush();
        }
    }

    public static void close() {
        if (logWriter != null) {
            logWriter.close();
        }
    }
}
