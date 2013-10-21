/** @see DefaultHandler **/
public void endElement(String aNamespaceURI, String aLocalName, String aQName) {
    if (TAG_EVENT.equals(aQName)) {
        addEvent();
        resetData();
    } else if (TAG_NDC.equals(aQName)) {
        mNDC = mBuf.toString();
    } else if (TAG_MESSAGE.equals(aQName)) {
        mMessage = mBuf.toString();
    } else if (TAG_THROWABLE.equals(aQName)) {
        final StringTokenizer st = new StringTokenizer(mBuf.toString(), "\n\t");
        mThrowableStrRep = new String[st.countTokens()];
        if (mThrowableStrRep.length > 0) {
            mThrowableStrRep[0] = st.nextToken();
            for (int i = 1; i < mThrowableStrRep.length; i++) {
                mThrowableStrRep[i] = "\t" + st.nextToken();
            }
        }
    }
}
