/**
     * Prompts the user for a file to load events from.
     * @param aIgnore an <code>ActionEvent</code> value
     */
public void actionPerformed(ActionEvent aIgnore) {
    LOG.info("load file called");
    if (mChooser.showOpenDialog(mParent) == JFileChooser.APPROVE_OPTION) {
        LOG.info("Need to load a file");
        final File chosen = mChooser.getSelectedFile();
        LOG.info("loading the contents of " + chosen.getAbsolutePath());
        try {
            final int num = loadFile(chosen.getAbsolutePath());
            JOptionPane.showMessageDialog(mParent, "Loaded " + num + " events.", "CHAINSAW", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            LOG.warn("caught an exception loading the file", e);
            JOptionPane.showMessageDialog(mParent, "Error parsing file - " + e.getMessage(), "CHAINSAW", JOptionPane.ERROR_MESSAGE);
        }
    }
}
