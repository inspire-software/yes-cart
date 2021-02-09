/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
     * @throws IOException errors
     */
    List<MutablePair<String, String>> list(String mode) throws IOException;

    /**
     * Download given file as bytes.
     *
     * @param fileName file name
     *
     * @return file contents.
     */
    byte[] download(String fileName) throws IOException;

    /**
     * Download given file as bytes.
     *
     * @param fileName file name
     * @param rawFile raw file
     *
     * @return file contents.
     */
    byte[] download(String fileName, boolean rawFile) throws IOException;

    /**
     * Store given bytes as file.
     *
     * @param bytes file body.
     * @param fileName file name
     *
     * @return file name, including path, on server side.
     */
    String upload(byte[] bytes, String fileName) throws IOException;

    /**
     * Delete given file.
     *
     * @param fileName file name
     */
    void delete(String fileName) throws IOException;

    /**
     * Get user home directory.
     *
     * @return home path
     *
     * @throws IOException errors
     */
    String home() throws IOException;

}
