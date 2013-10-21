LoggerRepository genericHierarchy() {
    if (genericHierarchy == null) {
        File f = new File(dir, GENERIC + CONFIG_FILE_EXT);
        if (f.exists()) {
            genericHierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
            new PropertyConfigurator().doConfigure(f.getAbsolutePath(), genericHierarchy);
        } else {
            cat.warn("Could not find config file [" + f + "]. Will use the default hierarchy.");
            genericHierarchy = LogManager.getLoggerRepository();
        }
    }
    return genericHierarchy;
}
