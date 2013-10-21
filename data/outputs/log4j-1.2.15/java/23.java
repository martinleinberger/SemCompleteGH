public void resetAllNodeCounts() {
    Enumeration nodes = getRootCategoryNode().depthFirstEnumeration();
    CategoryNode current;
    while (nodes.hasMoreElements()) {
        current = (CategoryNode) nodes.nextElement();
        current.resetNumberOfContainedRecords();
        nodeChanged(current);
    }
}
