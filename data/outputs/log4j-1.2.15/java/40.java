/**
   * Saves a list of MRU files out to a file.
   */
public void save() {
    File file = new File(getFilename());
    try {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(_mruFileList);
        oos.flush();
        oos.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
