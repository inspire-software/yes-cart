package org.yes.cart.remote.service;

import sun.java2d.pipe.BufferedTextPipe;

import java.io.IOException;

/**
 *
 * Service responsible to upload file.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 10/12/11
 * Time: 18:08
 */
public interface RemoteUploadService {

    /**
     * Store given bytes as file.
     * @param bytes file body.
     * @param fileName file name
     * @return file name, including path, on server side.
     */
    String upload(byte [] bytes, String fileName) throws IOException;

}
