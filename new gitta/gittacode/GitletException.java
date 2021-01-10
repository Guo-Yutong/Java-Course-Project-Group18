package gitlet;

/** General exception indicating a Gitlet error.  For fatal errors, the
 *  result of .getMessage() is the error message to be printed.
 *  @author P. N. Hilfinger
 */
class GitletException extends RuntimeException {

    /** error msg. */
    private String _msg;

    /** A GitletException with no message. */
    GitletException() {
        super();
    }

    /**
     * A GitletException MSG as its message.
     * @param msg msg
     */
    GitletException(String msg) {
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
