protected List createFilteredRecordsList() {
    List result = new ArrayList();
    Iterator records = _allRecords.iterator();
    LogRecord current;
    while (records.hasNext()) {
        current = (LogRecord) records.next();
        if (_filter.passes(current)) {
            result.add(current);
        }
    }
    return result;
}
