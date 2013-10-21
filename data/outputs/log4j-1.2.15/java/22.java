public void setDescendantSelection(CategoryNode node, boolean selected) {
    Enumeration descendants = node.depthFirstEnumeration();
    CategoryNode current;
    while (descendants.hasMoreElements()) {
        current = (CategoryNode) descendants.nextElement();
        if (current.isSelected() != selected) {
            current.setSelected(selected);
            nodeChanged(current);
        }
    }
    notifyActionListeners();
}
