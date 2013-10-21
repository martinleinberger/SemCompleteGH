protected void processLogLevels(Document doc) {
    NodeList nodeList = doc.getElementsByTagName(LEVEL);
    Map menuItems = _monitor.getLogLevelMenuItems();
    for (int i = 0; i < nodeList.getLength(); i++) {
        Node n = nodeList.item(i);
        NamedNodeMap map = n.getAttributes();
        String name = getValue(map, NAME);
        try {
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) menuItems.get(LogLevel.valueOf(name));
            item.setSelected(getValue(map, SELECTED).equalsIgnoreCase("true"));
        } catch (LogLevelFormatException e) {
        }
    }
}
