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

package org.yes.cart.service.async.model;

/**
 * User: denispavlov
 * Date: 12-11-09
 * Time: 10:31 AM
 */
public interface JobContextKeys {

    //-- INDEXING JOBS --------------------------------//

    /** Holds Map<String, Boolean> to keep track which nodes have finished full index */
    String NODE_FULL_PRODUCT_INDEX_STATE = "nodeIndexState";

    //-- IMPORT JOBS ----------------------------------//

    /** file name of the currently imported file */
    String IMPORT_FILE = "fileName";
    /** Set of imported file, so that we can archive them. */
    String IMPORT_FILE_SET = "importedFiles";
    /** chosen descriptors group */
    String IMPORT_DESCRIPTOR_GROUP = "descriptorGroup";
    /** import descriptor object */
    String IMPORT_DESCRIPTOR = "importDescriptor";
    /** import descriptor name */
    String IMPORT_DESCRIPTOR_NAME = "importDescriptorName";
    /** path to import directory. */
    String IMPORT_DIRECTORY_ROOT = "pathToImportDirectoryRoot";
    /** path to image vault. */
    String IMAGE_VAULT_PATH = "pathToImageVault";


}
