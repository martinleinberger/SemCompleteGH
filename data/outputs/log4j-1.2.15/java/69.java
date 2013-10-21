/**
     Read configuration options from url <code>configURL</code>.
   */
public void doConfigure(java.net.URL configURL, LoggerRepository hierarchy) {
    Properties props = new Properties();
    LogLog.debug("Reading configuration from URL " + configURL);
    InputStream istream = null;
    try {
        istream = configURL.openStream();
        props.load(istream);
    } catch (Exception e) {
        LogLog.error("Could not read configuration file from URL [" + configURL + "].", e);
        LogLog.error("Ignoring configuration file [" + configURL + "].");
        return;
    } finally {
        if (istream != null) {
            try {
                istream.close();
            } catch (Exception ignore) {
            }
        }
    }
    doConfigure(props, hierarchy);
}
