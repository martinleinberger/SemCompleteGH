private void processLogTableColumns(List logTableColumnMenuItems, StringBuffer xml) {
    xml.append("\t<logtablecolumns>\r\n");
    Iterator it = logTableColumnMenuItems.iterator();
    while (it.hasNext()) {
        LogTableColumn column = (LogTableColumn) it.next();
        JCheckBoxMenuItem item = _monitor.getTableColumnMenuItem(column);
        exportLogTableColumnXMLElement(column.getLabel(), item.isSelected(), xml);
    }
    xml.append("\t</logtablecolumns>\r\n");
}
