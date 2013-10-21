public static void register(List logLevels) {
    if (logLevels != null) {
        Iterator it = logLevels.iterator();
        while (it.hasNext()) {
            register((LogLevel) it.next());
        }
    }
}
