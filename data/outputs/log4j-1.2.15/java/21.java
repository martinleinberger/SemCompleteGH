protected void resetAllNodes() {
    Enumeration nodes = _model.getRootCategoryNode().depthFirstEnumeration();
    CategoryNode current;
    while (nodes.hasMoreElements()) {
        current = (CategoryNode) nodes.nextElement();
        current.resetNumberOfContainedRecords();
        _model.nodeChanged(current);
    }
}
