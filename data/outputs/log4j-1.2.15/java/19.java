/**
   * Get the InputStream for this resource.  Note: to convert an InputStream
   * into an InputReader, use: new InputStreamReader(InputStream).
   *
   * @param object   The object to grab the Classloader from.
   *                 This parameter is quite important from a
   *                 visibility of resources standpoint as the
   *                 hierarchy of Classloaders plays a role.
   *
   * @param resource The resource to load.
   *
   * @return If the Resource was found, the InputStream, otherwise null.
   *
   * @see Resource
   * @see #getResourceAsURL(Object,Resource)
   * @see InputStream
   */
public static InputStream getResourceAsStream(Object object, Resource resource) {
    ClassLoader loader = object.getClass().getClassLoader();
    InputStream in = null;
    if (loader != null) {
        in = loader.getResourceAsStream(resource.getName());
    } else {
        in = ClassLoader.getSystemResourceAsStream(resource.getName());
    }
    return in;
}
