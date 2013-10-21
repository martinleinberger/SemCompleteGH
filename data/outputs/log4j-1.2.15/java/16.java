/**
   * Loads a log file from a web server into the LogFactor5 GUI.
   */
private String loadLogFile(InputStream stream) throws IOException {
    BufferedInputStream br = new BufferedInputStream(stream);
    int count = 0;
    int size = br.available();
    StringBuffer sb = null;
    if (size > 0) {
        sb = new StringBuffer(size);
    } else {
        sb = new StringBuffer(1024);
    }
    while ((count = br.read()) != -1) {
        sb.append((char) count);
    }
    br.close();
    br = null;
    return sb.toString();
}
