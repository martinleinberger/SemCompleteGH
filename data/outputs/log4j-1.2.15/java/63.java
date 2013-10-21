{
    this.socket = socket;
    this.hierarchy = hierarchy;
    try {
        ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
    } catch (Exception e) {
        logger.error("Could not open ObjectInputStream to " + socket, e);
    }
}