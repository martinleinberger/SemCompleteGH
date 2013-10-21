protected void expandDescendants(CategoryNode node) {
    Enumeration descendants = node.depthFirstEnumeration();
    CategoryNode current;
    while (descendants.hasMoreElements()) {
        current = (CategoryNode) descendants.nextElement();
        expand(current);
    }
}
