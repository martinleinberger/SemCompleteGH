/**
   * Get the InputStreamReader for this Resource. Uses the classloader from
   * this Resource.
   *
   * @see #getInputStream
   * @see ResourceUtils
   */
public InputStreamReader getInputStreamReader() {
    InputStream in = ResourceUtils.getResourceAsStream(this, this);
    if (in == null) {
        return null;
    }
    InputStreamReader reader = new InputStreamReader(in);
    return reader;
}
