package test;
package src;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import com.sun.tools.javac.util.Context.Key;

import junit.framework.AssertionFailedError;

public class BlobTest {
    private Blob blob1;
    private Blob blob2;

    @Test
    public void testBlob() {


        FileWriter test1 = new FileWriter(file);
        try {
            test1.write("这是测试文件一");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter test2 = new FileWriter(file);
        try {
            test2.write("这是测试文件二");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Blob blob1 = new Blob(a);
        Blob blob2 = new Blob(b);

        assertTrue(blob1.getKey().length() == 40);
        assertNotEquals(blob2.key, blob2.key);
    }
}
