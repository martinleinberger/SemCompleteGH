/**
     Returns all the currently defined categories in this hierarchy as
     an {@link java.util.Enumeration Enumeration}.

     <p>The root logger is <em>not</em> included in the returned
     {@link Enumeration}.  */
public Enumeration getCurrentLoggers() {
    Vector v = new Vector(ht.size());
    Enumeration elems = ht.elements();
    while (elems.hasMoreElements()) {
        Object o = elems.nextElement();
        if (o instanceof Logger) {
            v.addElement(o);
        }
    }
    return v.elements();
}
