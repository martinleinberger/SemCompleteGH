public final Object childValue(Object parentValue) {
    Hashtable ht = (Hashtable) parentValue;
    if (ht != null) {
        return ht.clone();
    } else {
        return null;
    }
}
