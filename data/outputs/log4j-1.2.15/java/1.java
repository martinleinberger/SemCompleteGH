/**
     Close all attached appenders implementing the AppenderAttachable
     interface.
     @since 1.0
  */
synchronized void closeNestedAppenders() {
    Enumeration enumeration = this.getAllAppenders();
    if (enumeration != null) {
        while (enumeration.hasMoreElements()) {
            Appender a = (Appender) enumeration.nextElement();
            if (a instanceof AppenderAttachable) {
                a.close();
            }
        }
    }
}
