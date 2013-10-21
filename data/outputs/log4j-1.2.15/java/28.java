protected void collapseDescendants(CategoryNode node) {
    Enumeration descendants = node.depthFirstEnumeration();
    CategoryNode current;
    while (descendants.hasMoreElements()) {
        current = (CategoryNode) descendants.nextElement();
        collapse(current);
    }
}
