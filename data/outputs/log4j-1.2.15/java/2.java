/**
     Returns the string resource coresponding to <code>key</code> in
     this category's inherited resource bundle. See also {@link
     #getResourceBundle}.

     <p>If the resource cannot be found, then an {@link #error error}
     message will be logged complaining about the missing resource.
  */
protected String getResourceBundleString(String key) {
    ResourceBundle rb = getResourceBundle();
    if (rb == null) {
        return null;
    } else {
        try {
            return rb.getString(key);
        } catch (MissingResourceException mre) {
            error("No resource is associated with key \"" + key + "\".");
            return null;
        }
    }
}
