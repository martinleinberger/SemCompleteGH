protected LogRecord getFilteredRecord(int row) {
    List records = getFilteredRecords();
    int size = records.size();
    if (row < size) {
        return (LogRecord) records.get(row);
    }
    return (LogRecord) records.get(size - 1);
}
