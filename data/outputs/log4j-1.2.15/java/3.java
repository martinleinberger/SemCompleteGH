/**
     Remove all previously added appenders from this Category
     instance.

     <p>This is useful when re-reading configuration information.
  */
public synchronized void removeAllAppenders() {
    if (aai != null) {
        Vector appenders = new Vector();
        for (Enumeration iter = aai.getAllAppenders(); iter.hasMoreElements(); ) {
            appenders.add(iter.nextElement());
        }
        aai.removeAllAppenders();
        for (Enumeration iter = appenders.elements(); iter.hasMoreElements(); ) {
            fireRemoveAppenderEvent((Appender) iter.nextElement());
        }
        aai = null;
    }
}
