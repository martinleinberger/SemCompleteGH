protected JMenuItem createAllLogTableColumnsMenuItem() {
    JMenuItem result = new JMenuItem("Show all Columns");
    result.setMnemonic('s');
    result.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            selectAllLogTableColumns(true);
            List selectedColumns = updateView();
            _table.setView(selectedColumns);
        }
    });
    return result;
}
