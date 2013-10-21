private void processLogLevels(Map logLevelMenuItems, StringBuffer xml) {
    xml.append("\t<loglevels>\r\n");
    Iterator it = logLevelMenuItems.keySet().iterator();
    while (it.hasNext()) {
        LogLevel level = (LogLevel) it.next();
        JCheckBoxMenuItem item = (JCheckBoxMenuItem) logLevelMenuItems.get(level);
        exportLogLevelXMLElement(level.getLabel(), item.isSelected(), xml);
    }
    xml.append("\t</loglevels>\r\n");
}
