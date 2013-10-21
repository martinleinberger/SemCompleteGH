protected void store(String s) {
    try {
        PrintWriter writer = new PrintWriter(new FileWriter(getFilename()));
        writer.print(s);
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
