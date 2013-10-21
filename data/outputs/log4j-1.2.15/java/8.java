public void activateOptions() {
    super.activateOptions();
    if (datePattern != null && fileName != null) {
        now.setTime(System.currentTimeMillis());
        sdf = new SimpleDateFormat(datePattern);
        int type = computeCheckPeriod();
        printPeriodicity(type);
        rc.setType(type);
        File file = new File(fileName);
        scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));
    } else {
        LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
    }
}
