int computeCheckPeriod() {
    RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
    Date epoch = new Date(0);
    if (datePattern != null) {
        for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            simpleDateFormat.setTimeZone(gmtTimeZone);
            String r0 = simpleDateFormat.format(epoch);
            rollingCalendar.setType(i);
            Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
            String r1 = simpleDateFormat.format(next);
            if (r0 != null && r1 != null && !r0.equals(r1)) {
                return i;
            }
        }
    }
    return TOP_OF_TROUBLE;
}
