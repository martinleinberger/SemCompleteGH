/**
     Returns an OutputStreamWriter when passed an OutputStream.  The
     encoding used will depend on the value of the
     <code>encoding</code> property.  If the encoding value is
     specified incorrectly the writer will be opened using the default
     system encoding (an error message will be printed to the loglog.  */
protected OutputStreamWriter createWriter(OutputStream os) {
    OutputStreamWriter retval = null;
    String enc = getEncoding();
    if (enc != null) {
        try {
            retval = new OutputStreamWriter(os, enc);
        } catch (IOException e) {
            LogLog.warn("Error initializing output writer.");
            LogLog.warn("Unsupported encoding?");
        }
    }
    if (retval == null) {
        retval = new OutputStreamWriter(os);
    }
    return retval;
}
