protected void selectAllNodes() {
    CategoryExplorerModel model = _monitor.getCategoryExplorerTree().getExplorerModel();
    CategoryNode root = model.getRootCategoryNode();
    Enumeration all = root.breadthFirstEnumeration();
    CategoryNode n = null;
    while (all.hasMoreElements()) {
        n = (CategoryNode) all.nextElement();
        n.setSelected(true);
    }
}
