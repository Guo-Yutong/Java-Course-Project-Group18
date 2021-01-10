package gitta;

class GittaException extends RuntimeException {

    /** error msg. */
    private String _msg;

    /** A GitletException with no message. */
    GittaException() {
        super();
    }

    /**
     * A GitletException MSG as its message.
     * @param msg msg
     */
    GittaException(String msg) {
        super(msg);
        _msg = msg;
    }

    /**
     * Return msg.
     * @return msg
     */
    public String getMsg() {
        return _msg;
    }

}
