LoggerRepository configureHierarchy(InetAddress inetAddress) {
    cat.info("Locating configuration file for " + inetAddress);
    String s = inetAddress.toString();
    int i = s.indexOf("/");
    if (i == -1) {
        cat.warn("Could not parse the inetAddress [" + inetAddress + "]. Using default hierarchy.");
        return genericHierarchy();
    } else {
        String key = s.substring(0, i);
        File configFile = new File(dir, key + CONFIG_FILE_EXT);
        if (configFile.exists()) {
            Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
            hierarchyMap.put(inetAddress, h);
            new PropertyConfigurator().doConfigure(configFile.getAbsolutePath(), h);
            return h;
        } else {
            cat.warn("Could not find config file [" + configFile + "].");
            return genericHierarchy();
        }
    }
}
