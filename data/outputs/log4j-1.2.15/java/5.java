/** initialise log4j **/
private static void initLog4J() {
    final Properties props = new Properties();
    props.setProperty("log4j.rootLogger", "DEBUG, A1");
    props.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
    props.setProperty("log4j.appender.A1.layout", "org.apache.log4j.TTCCLayout");
    PropertyConfigurator.configure(props);
}
