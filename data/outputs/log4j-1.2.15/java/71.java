static void roll() {
    try {
        Socket socket = new Socket(host, port);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        dos.writeUTF(ExternallyRolledFileAppender.ROLL_OVER);
        String rc = dis.readUTF();
        if (ExternallyRolledFileAppender.OK.equals(rc)) {
            cat.info("Roll over signal acknowledged by remote appender.");
        } else {
            cat.warn("Unexpected return code " + rc + " from remote entity.");
            System.exit(2);
        }
    } catch (IOException e) {
        cat.error("Could not send roll signal on host " + host + " port " + port + " .", e);
        System.exit(2);
    }
    System.exit(0);
}
