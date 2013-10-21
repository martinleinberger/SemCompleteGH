private void processLogLevelColors(Map logLevelMenuItems, Map logLevelColors, StringBuffer xml) {
    xml.append("\t<loglevelcolors>\r\n");
    Iterator it = logLevelMenuItems.keySet().iterator();
    while (it.hasNext()) {
        LogLevel level = (LogLevel) it.next();
        Color color = (Color) logLevelColors.get(level);
        exportLogLevelColorXMLElement(level.getLabel(), color, xml);
    }
    xml.append("\t</loglevelcolors>\r\n");
}
