package org.yes.cart.remote.service.impl;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 10/12/11
 * Time: 18:33
 */
public class RemoteUploadServiceImplTest2 {

    @Test
    public void testUpload() throws Exception {

        RemoteUploadServiceImpl remoteUploadService = new RemoteUploadServiceImpl();
        String fullFileName = remoteUploadService.upload("some bytes".getBytes(), "filename.txt");
        File file = new File(fullFileName);
        assertTrue(file.exists());

    }

}
