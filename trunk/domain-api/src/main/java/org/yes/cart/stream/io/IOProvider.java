/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.stream.io;

import java.io.IOException;
import java.util.Map;

/**
 * Proxy IO interface that allows to decouple file operations from the underlying implementation.
 * By using streams we prohibit use of File objects thus removing dependency on the file system.
 *
 * User: denispavlov
 * Date: 30/08/2014
 * Time: 15:23
 */
public interface IOProvider {

    /**
     * Simple check to determine if IOProvider supports given URI.
     *
     * @param uri uri to perform read/write operation on.
     *
     * @return true if this IO provider capable of handling given uri
     */
    boolean supports(String uri);

    /**
     * Check if resource with given uri already exists.
     *
     * @param uri uri of the read target
     * @param context any applicable context for given provider
     *
     * @return true if given resource exists
     */
    boolean exists(String uri, Map<String, Object> context);

    /**
     * Check if uriToCheck has newer timestamp than uriToCheckAgainst (i.e. uriToCheck.lastModified > uriToCheckAgainst.lastModified).
     *
     * @param uriToCheck uri to check timestamp
     * @param uriToCheckAgainst uri to check timestamp
     * @param context any applicable context for given provider
     *
     * @return true if given resource exists and timestamp is newer
     */
    boolean isNewerThan(String uriToCheck, String uriToCheckAgainst, Map<String, Object> context);

    /**
     * Read content as bytes.
     *
     * @param uri uri of the read target
     * @param context any applicable context for given provider
     *
     * @return bytes read from target
     *
     * @throws IOException in case if read fails
     */
    byte[] read(String uri, Map<String, Object> context) throws IOException;

    /**
     * Write content as bytes.
     *
     * @param uri uri of the target
     * @param content bytes to be written
     * @param context any applicable context for given provider
     *
     * @throws IOException in case if write fails
     */
    void write(String uri, byte[] content, Map<String, Object> context) throws IOException;

    /**
     * Remove content as bytes.
     *
     * @param uri uri of the target
     * @param context any applicable context for given provider
     *
     * @throws IOException in case if write fails
     */
    void delete(String uri, Map<String, Object> context) throws IOException;

}
