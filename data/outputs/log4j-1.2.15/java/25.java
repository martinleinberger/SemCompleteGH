/**
   * @deprecated
   */
public void setAllDescendantsDeSelected() {
    Enumeration children = children();
    while (children.hasMoreElements()) {
        CategoryNode node = (CategoryNode) children.nextElement();
        node.setSelected(false);
        node.setAllDescendantsDeSelected();
    }
}
