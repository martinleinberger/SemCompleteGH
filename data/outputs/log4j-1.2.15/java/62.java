/**
     Release the underlying ServerMonitor thread, and drop the connections
     to all connected remote servers. */
public void cleanUp() {
    LogLog.debug("stopping ServerSocket");
    serverMonitor.stopMonitor();
    serverMonitor = null;
    LogLog.debug("closing client connections");
    while (oosList.size() != 0) {
        ObjectOutputStream oos = (ObjectOutputStream) oosList.elementAt(0);
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                LogLog.error("could not close oos.", e);
            }
            oosList.removeElementAt(0);
        }
    }
}
