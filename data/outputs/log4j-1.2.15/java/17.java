private long parseDate(String record) {
    try {
        String s = parseAttribute(DATE_DELIMITER, record);
        if (s == null) {
            return 0;
        }
        Date d = _sdf.parse(s);
        return d.getTime();
    } catch (ParseException e) {
        return 0;
    }
}
