/**
     Push new diagnostic context information for the current thread.

     <p>The contents of the <code>message</code> parameter is
     determined solely by the client.  
     
     @param message The new diagnostic context information.  */
public static void push(String message) {
    Stack stack = getCurrentStack();
    if (stack == null) {
        DiagnosticContext dc = new DiagnosticContext(message, null);
        stack = new Stack();
        Thread key = Thread.currentThread();
        ht.put(key, stack);
        stack.push(dc);
    } else if (stack.isEmpty()) {
        DiagnosticContext dc = new DiagnosticContext(message, null);
        stack.push(dc);
    } else {
        DiagnosticContext parent = (DiagnosticContext) stack.peek();
        stack.push(new DiagnosticContext(message, parent));
    }
}
