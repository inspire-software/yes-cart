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

        final String folder = System.getProperty("java.io.tmpdir")
                + File.separator
                + "yes-cart"
                + File.separator
                + Constants.IMPORT_FOLDER;

        final String fullFileName = FilenameUtils.normalize(
                folder
                        + File.separator
                        + fileName
        );

        new File(folder).mkdirs();

        File file = new File(fullFileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            return fullFileName;
        } catch (IOException e) {
            e.printStackTrace();  //Todo log
        } finally {
            fos.close();
        }


        return null;
    }

}
