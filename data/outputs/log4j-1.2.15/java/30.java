{
    String processedCategory = category;
    if (processedCategory == null) {
        processedCategory = "Debug";
    }
    processedCategory.replace('/', '.');
    processedCategory = processedCategory.replace('\\', '.');
    StringTokenizer st = new StringTokenizer(processedCategory, ".");
    while (st.hasMoreTokens()) {
        String element = st.nextToken();
        addCategoryElement(new CategoryElement(element));
    }
}