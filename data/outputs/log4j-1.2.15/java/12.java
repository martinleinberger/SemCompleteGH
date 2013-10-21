/**
     Reset all values contained in this hierarchy instance to their
     default.  This removes all appenders from all categories, sets
     the level of all non-root categories to <code>null</code>,
     sets their additivity flag to <code>true</code> and sets the level
     of the root logger to {@link Level#DEBUG DEBUG}.  Moreover,
     message disabling is set its default "off" value.

     <p>Existing categories are not removed. They are just reset.

     <p>This method should be used sparingly and with care as it will
     block all logging until it is completed.</p>

     @since 0.8.5 */
public void resetConfiguration() {
    getRootLogger().setLevel((Level) Level.DEBUG);
    root.setResourceBundle(null);
    setThreshold(Level.ALL);
    synchronized (ht) {
        shutdown();
        Enumeration cats = getCurrentLoggers();
        while (cats.hasMoreElements()) {
            Logger c = (Logger) cats.nextElement();
            c.setLevel(null);
            c.setAdditivity(true);
            c.setResourceBundle(null);
        }
    }
    rendererMap.clear();
}
