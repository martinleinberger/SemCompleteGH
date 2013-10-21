/**
   * Removes any inactive nodes from the Category tree.
   */
protected int removeUnusedNodes() {
    int count = 0;
    CategoryNode root = _categoryModel.getRootCategoryNode();
    Enumeration enumeration = root.depthFirstEnumeration();
    while (enumeration.hasMoreElements()) {
        CategoryNode node = (CategoryNode) enumeration.nextElement();
        if (node.isLeaf() && node.getNumberOfContainedRecords() == 0 && node.getParent() != null) {
            _categoryModel.removeNodeFromParent(node);
            count++;
        }
    }
    return count;
}
