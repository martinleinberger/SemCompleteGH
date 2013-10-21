public InputSource resolveEntity(String publicId, String systemId) {
    if (systemId.endsWith("log4j.dtd")) {
        Class clazz = getClass();
        InputStream in = clazz.getResourceAsStream("/org/apache/log4j/xml/log4j.dtd");
        if (in == null) {
            LogLog.warn("Could not find [log4j.dtd] using [" + clazz.getClassLoader() + "] class loader, parsed without DTD.");
            in = new ByteArrayInputStream(new byte[0]);
        }
        return new InputSource(in);
    } else {
        return null;
    }
}
