private void put0(String key, Object o) {
    if (java1 || tlm == null) {
        return;
    } else {
        Hashtable ht = (Hashtable) ((ThreadLocalMap) tlm).get();
        if (ht == null) {
            ht = new Hashtable(HT_SIZE);
            ((ThreadLocalMap) tlm).set(ht);
        }
        ht.put(key, o);
    }
}
