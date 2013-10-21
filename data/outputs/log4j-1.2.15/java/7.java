/**
   * Prints the configuration of the default log4j hierarchy as a Java
   * properties file on the specified Writer.
   * 
   * <p>N.B. print() can be invoked only once!
   */
public void print(PrintWriter out) {
    printOptions(out, Logger.getRootLogger());
    Enumeration cats = LogManager.getCurrentLoggers();
    while (cats.hasMoreElements()) {
        printOptions(out, (Logger) cats.nextElement());
    }
}
