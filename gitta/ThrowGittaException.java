package gitta;

interface ThrowGittaException {
	
    /* MESSAGES AND ERROR REPORTING */

    /** Return a GitletException whose message is composed from MSG and ARGS as
     *  for the String.format method. */
    static GittaException error(String msg, Object... args) {
        return new GittaException(String.format(msg, args));
    }

    /** Print a message composed from MSG and ARGS as for the String.format
     *  method, followed by a newline. */
    static void message(String msg, Object... args) {
        System.out.printf(msg, args);
        System.out.println();
    }

}
