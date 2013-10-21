protected void load() {
    File file = new File(getFilename());
    if (file.exists()) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            processRecordFilter(doc);
            processCategories(doc);
            processLogLevels(doc);
            processLogLevelColors(doc);
            processLogTableColumns(doc);
        } catch (Exception e) {
            System.err.println("Unable process configuration file at " + getFilename() + ". Error Message=" + e.getMessage());
        }
    }
}
