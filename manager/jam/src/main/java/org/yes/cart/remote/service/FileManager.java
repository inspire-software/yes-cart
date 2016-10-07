package org.yes.cart.remote.service;

import org.yes.cart.domain.misc.MutablePair;

import java.io.IOException;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/09/2016
 * Time: 09:01
 */
public interface FileManager {

    /**
     * List available files
     *
     * @return pair of absolute and human friendly paths
     * @throws IOException
     */
    List<MutablePair<String, String>> list(String mode) throws IOException;

    /**
     * Download given file as bytes.
     * @param fileName file name
     * @return file contents.
     */
    byte[] download(String fileName) throws IOException;

    /**
     * Store given bytes as file.
     * @param bytes file body.
     * @param fileName file name
     * @return file name, including path, on server side.
     */
    String upload(byte[] bytes, String fileName) throws IOException;

    /**
     * Delete given file.
     * @param fileName file name
     */
    void delete(String fileName) throws IOException;

    /**
     * Get user home directory.
     *
     * @return home path
     *
     * @throws IOException
     */
    String home() throws IOException;

}
