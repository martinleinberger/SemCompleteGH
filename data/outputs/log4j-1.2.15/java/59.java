private void remove0(String key) {
    if (!java1 && tlm != null) {
        Hashtable ht = (Hashtable) ((ThreadLocalMap) tlm).get();
        if (ht != null) {
            ht.remove(key);
        }
    }
}
