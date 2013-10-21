protected List updateView() {
    ArrayList updatedList = new ArrayList();
    Iterator columnIterator = _columns.iterator();
    while (columnIterator.hasNext()) {
        LogTableColumn column = (LogTableColumn) columnIterator.next();
        JCheckBoxMenuItem result = getLogTableColumnMenuItem(column);
        if (result.isSelected()) {
            updatedList.add(column);
        }
    }
    return updatedList;
}
