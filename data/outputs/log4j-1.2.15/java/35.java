protected void deleteConfigurationFile() {
    try {
        File f = new File(getFilename());
        if (f.exists()) {
            f.delete();
        }
    } catch (SecurityException e) {
        System.err.println("Cannot delete " + getFilename() + " because a security violation occured.");
    }
}
