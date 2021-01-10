package gitta;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GittaUtils {
	/* FILE DELETION */

    /** Deletes FILE if it exists and is not a directory.  Returns true
     *  if FILE was deleted, and false otherwise.  Refuses to delete FILE
     *  and throws IllegalArgumentException unless the directory designated by
     *  FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitta")).isDirectory()) {
            throw new IllegalArgumentException("not .gitta working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /** Deletes the file named FILE if it exists and is not a directory.
     *  Returns true if FILE was deleted, and false otherwise.  Refuses
     *  to delete FILE and throws IllegalArgumentException unless the
     *  directory designated by FILE also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* DIRECTORIES */

    /** Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list();
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /** Returns a list of the names of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }
    
    /** Returns a list of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<File> plainFilesIn(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }
    
    /** Returns a list of all plain files in the directory DIR, in
     *  lexicographic order as Java Strings.  Returns null if DIR does
     *  not denote a directory. */
    static List<File> plainFilesIn(String dir) {
        return plainFilesIn(new File(dir));
    }

    /* OTHER FILE UTILITIES */

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator,
     *  analogous to the {@link java.nio.file.Paths.#get(String, String[])}
     *  method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }
    
    public static void main(String[] args) {
    	GittaUtils utl = new GittaUtils();
    	System.out.println(utl.plainFilenamesIn("/home/yutong_guo/Java/gitta/bin/newfolder/newfolder2"));
    	for(String str : utl.plainFilenamesIn("/home/yutong_guo/Java/gitta/bin/newfolder/newfolder2")) {
    		System.out.println(join("/home/yutong_guo/Java/gitta/bin/newfolder/newfolder2",str).getAbsolutePath());
    	}
    }

}
