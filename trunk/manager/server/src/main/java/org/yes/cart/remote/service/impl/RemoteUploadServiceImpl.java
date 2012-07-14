package org.yes.cart.remote.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.remote.service.RemoteUploadService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Upload file to server side.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 10/12/11
 * Time: 18:11
 */
public class RemoteUploadServiceImpl implements RemoteUploadService {

    /**
     * {@inheritDoc}
     */
    public String upload(final byte[] bytes, final String fileName) throws IOException {

        final String folderPath = System.getProperty("java.io.tmpdir")
                + File.separator
                + "yes-cart"
                + File.separator
                + Constants.IMPORT_FOLDER;

        final File folder = new File(folderPath);
        if (folder.mkdirs()) {

            FileOutputStream fos = null;
            try {
                File file = File.createTempFile(fileName, null, folder);
                fos = new FileOutputStream(file);
                fos.write(bytes);
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();  //Todo log
                if (fos != null) {
                    fos.close();
                }
                throw e;
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
        throw new IOException("Unable to create directory: " + folderPath);
    }

}
