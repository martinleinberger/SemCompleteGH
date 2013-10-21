/**
   * @deprecated
   */
public void setAllDescendantsSelected() {
    Enumeration children = children();
    while (children.hasMoreElements()) {
        CategoryNode node = (CategoryNode) children.nextElement();
        node.setSelected(true);
        node.setAllDescendantsSelected();
    }
}
