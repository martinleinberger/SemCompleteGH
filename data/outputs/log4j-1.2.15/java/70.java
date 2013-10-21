public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
    super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
    if (append) {
        File f = new File(fileName);
        ((CountingQuietWriter) qw).setCount(f.length());
    }
}
