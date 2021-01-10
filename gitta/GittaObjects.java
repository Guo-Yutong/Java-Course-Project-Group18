package gitta;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;

public abstract class GittaObjects implements GittaIO,ThrowGittaException{
	
	protected String _type;
    static final File OBJECTS_FOLDER = GittaUtils.join(Repository.REPO_FOLDER, "objects");
	 /** The length of a complete SHA-1 UID as a hexadecimal numeral. */
    /** Blob object content. */
    protected byte[] _content;
    
    static final int UID_LENGTH = 40;
	
	protected String getSHA1(Object... vals) {
		try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
	}
	protected String getSHA1(List<Object> vals) {
		return getSHA1(vals.toArray(new Object[vals.size()]));
	}
	
//	static byte[] serialize(Serializable obj) {
//        try {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
//            objectStream.writeObject(obj);
//            objectStream.close();
//            return stream.toByteArray();
//        } catch (IOException excp) {
//            throw ThrowGittaException.error("Internal error serializing commit.");
//        }
//    }
    /**
     * Return content of object.
     * @return object's content
     */
    public byte[] getContent() {
        return _content;
    }
}
