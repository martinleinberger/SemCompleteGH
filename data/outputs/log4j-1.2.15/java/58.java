private Object get0(String key) {
    if (java1 || tlm == null) {
        return null;
    } else {
        Hashtable ht = (Hashtable) ((ThreadLocalMap) tlm).get();
        if (ht != null && key != null) {
            return ht.get(key);
        } else {
            return null;
        }
    }
}
