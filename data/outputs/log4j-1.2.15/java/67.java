/** sends a message to each of the clients in telnet-friendly output. */
public void send(String message) {
    Enumeration ce = connections.elements();
    for (Enumeration e = writers.elements(); e.hasMoreElements(); ) {
        Socket sock = (Socket) ce.nextElement();
        PrintWriter writer = (PrintWriter) e.nextElement();
        writer.print(message);
        if (writer.checkError()) {
            connections.remove(sock);
            writers.remove(writer);
        }
    }
}
