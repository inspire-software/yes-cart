package org.yes.cart.stream.io;

import java.io.File;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/11/2015
 * Time: 14:46
 */
public interface FileSystemIOProvider extends IOProvider {

    /**
     * Resolve file system path by given uri.
     *
     * @param uri uri of the read target
     * @param context any applicable context for given provider
     *
     * @return file object
     */
    File resolveFileFromUri(final String uri, final Map<String, Object> context);

}
