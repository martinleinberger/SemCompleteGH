protected Object getDisplayedProperties(CategoryNode node) {
    ArrayList result = new ArrayList();
    result.add("Category: " + node.getTitle());
    if (node.hasFatalRecords()) {
        result.add("Contains at least one fatal LogRecord.");
    }
    if (node.hasFatalChildren()) {
        result.add("Contains descendants with a fatal LogRecord.");
    }
    result.add("LogRecords in this category alone: " + node.getNumberOfContainedRecords());
    result.add("LogRecords in descendant categories: " + node.getNumberOfRecordsFromChildren());
    result.add("LogRecords in this category including descendants: " + node.getTotalNumberOfRecords());
    return result.toArray();
}
