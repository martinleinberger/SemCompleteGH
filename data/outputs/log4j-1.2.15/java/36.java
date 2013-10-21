private void processConfigurationNode(CategoryNode node, StringBuffer xml) {
    CategoryExplorerModel model = _monitor.getCategoryExplorerTree().getExplorerModel();
    Enumeration all = node.breadthFirstEnumeration();
    CategoryNode n = null;
    while (all.hasMoreElements()) {
        n = (CategoryNode) all.nextElement();
        exportXMLElement(n, model.getTreePathToRoot(n), xml);
    }
}
