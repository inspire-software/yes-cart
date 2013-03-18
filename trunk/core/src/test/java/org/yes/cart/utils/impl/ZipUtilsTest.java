package org.yes.cart.utils.impl;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * User: igora Igor Azarny
 * Date: 1/30/13
 * Time: 10:14 AM
 */
public class ZipUtilsTest {
    /**
     * TEst non ascii arc file entry
     * @throws Exception
     */
    @Test
    public void testUnzipArchive() throws Exception {


        (new ZipUtils("Cp866")).unzipArchive(
                "src/test/resources/import/archive/testfile.zip",
                "target/test-classes/import/archive"
        );

        File [] files = (new File("target/test-classes/import/archive")).listFiles();

        boolean found = false;
        
        for (File file : files) {
            if (file.getAbsolutePath().contains("Привет, я файло.txt")) {
                found = true;
                break;
            }
        }
        assertTrue(found);


    }

}
