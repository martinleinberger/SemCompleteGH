/**
     Shutting down a hierarchy will <em>safely</em> close and remove
     all appenders in all categories including the root logger.

     <p>Some appenders such as {@link org.apache.log4j.net.SocketAppender}
     and {@link AsyncAppender} need to be closed before the
     application exists. Otherwise, pending logging events might be
     lost.

     <p>The <code>shutdown</code> method is careful to close nested
     appenders before closing regular appenders. This is allows
     configurations where a regular appender is attached to a logger
     and again to a nested appender.


     @since 1.0 */
public void shutdown() {
    Logger root = getRootLogger();
    root.closeNestedAppenders();
    synchronized (ht) {
        Enumeration cats = this.getCurrentLoggers();
        while (cats.hasMoreElements()) {
            Logger c = (Logger) cats.nextElement();
            c.closeNestedAppenders();
        }
        root.removeAllAppenders();
        cats = this.getCurrentLoggers();
        while (cats.hasMoreElements()) {
            Logger c = (Logger) cats.nextElement();
            c.removeAllAppenders();
        }
    }
}
