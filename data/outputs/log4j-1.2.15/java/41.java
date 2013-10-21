/**
   * Creates the directory where the MRU file list will be written.
   * The "lf5" directory is created in the Documents and Settings
   * directory on Windows 2000 machines and where ever the user.home
   * variable points on all other platforms.
   */
public static void createConfigurationDirectory() {
    String home = System.getProperty("user.home");
    String sep = System.getProperty("file.separator");
    File f = new File(home + sep + "lf5");
    if (!f.exists()) {
        try {
            f.mkdir();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
