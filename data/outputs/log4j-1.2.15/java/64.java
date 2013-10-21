static void init(String portStr, String configFile, String dirStr) {
    try {
        port = Integer.parseInt(portStr);
    } catch (java.lang.NumberFormatException e) {
        e.printStackTrace();
        usage("Could not interpret port number [" + portStr + "].");
    }
    PropertyConfigurator.configure(configFile);
    File dir = new File(dirStr);
    if (!dir.isDirectory()) {
        usage("[" + dirStr + "] is not a directory.");
    }
    server = new SocketServer(dir);
}
