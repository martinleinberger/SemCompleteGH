/**
   * Set the Throwable associated with this LogRecord.  When this method
   * is called, the stack trace in a String-based format is made
   * available via the getThrownStackTrace() method.
   *
   * @param thrown A Throwable to associate with this LogRecord.
   * @see #getThrown()
   * @see #getThrownStackTrace()
   */
public void setThrown(Throwable thrown) {
    if (thrown == null) {
        return;
    }
    _thrown = thrown;
    StringWriter sw = new StringWriter();
    PrintWriter out = new PrintWriter(sw);
    thrown.printStackTrace(out);
    out.flush();
    _thrownStackTrace = sw.toString();
    try {
        out.close();
        sw.close();
    } catch (IOException e) {
    }
    out = null;
    sw = null;
}
