protected JCheckBoxMenuItem createLogTableColumnMenuItem(LogTableColumn column) {
    JCheckBoxMenuItem result = new JCheckBoxMenuItem(column.toString());
    result.setSelected(true);
    result.setMnemonic(column.toString().charAt(0));
    result.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            List selectedColumns = updateView();
            _table.setView(selectedColumns);
        }
    });
    return result;
}
