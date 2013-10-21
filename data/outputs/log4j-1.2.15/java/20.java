/**
   * @return a byte[] containing the information contained in the
   * specified InputStream.
   * @throws java.io.IOException
   */
public static byte[] getBytes(InputStream input) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    copy(input, result);
    result.close();
    return result.toByteArray();
}
