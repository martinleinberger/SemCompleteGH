protected void trimOldestRecords() {
    synchronized (_allRecords) {
        int trim = numberOfRecordsToTrim();
        if (trim > 1) {
            List oldRecords = _allRecords.subList(0, trim);
            oldRecords.clear();
            refresh();
        } else {
            _allRecords.remove(0);
            fastRefresh();
        }
    }
}
