/**
     Clone the diagnostic context for the current thread.

     <p>Internally a diagnostic context is represented as a stack.  A
     given thread can supply the stack (i.e. diagnostic context) to a
     child thread so that the child can inherit the parent thread's
     diagnostic context.

     <p>The child thread uses the {@link #inherit inherit} method to
     inherit the parent's diagnostic context.
     
     @return Stack A clone of the current thread's  diagnostic context.

  */
public static Stack cloneStack() {
    Stack stack = getCurrentStack();
    if (stack == null) return null; else {
        return (Stack) stack.clone();
    }
}
