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

package org.yes.cart.bulkimport.service;

import org.yes.cart.service.async.JobStatusListener;

import java.io.File;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface BulkImportImagesService extends ImportService {

    /**
     * Prepare import for product image.
     *
     * @param statusListener error report
     * @param fileName    file name without the full path
     * @param code        product code
     * @param suffix      image suffix
     * @return true if given image file attached as attribute to given product
     */
    boolean doImportProductImage(
             JobStatusListener statusListener,
             String fileName,
             String code,
             String suffix);


    /**
     * Prepare import for product sku image.
     *
     * @param statusListener error report
     * @param fileName    file name without the full path
     * @param code        product sku code
     * @param suffix      image suffix
     * @return true if given image file attached as attribute to giveb product
     */
    boolean doImportProductSkuImage(
             JobStatusListener statusListener,
             String fileName,
             String code,
             String suffix);


    /**
     * Performs import of single image file. With following workflow:
     * first locate the product by code, if product found then insert / update image attribute.
     * The try to locate sku by code, if sku found, then insert / update image attribute.
     * If product or sku image attribute was inserted or update, that copy file to particular folder.
     *
     * @param file          file to import
     * @param statusListener error report
     * @param importedFiles add file to this set if imported it successfully imported.
     * @param pathToRepository path to image vault
     */
    void doImport(
             File file,
             JobStatusListener statusListener,
             Set<String> importedFiles,
             String pathToRepository);


    /**
     * Get the suffix of IMAGE or SKUIMAGE attribute name by given file name.
     * a  - 0, b - 1, etc
     *
     * @param fileName file name.
     * @return image attribute suffix name
     */
     String getImageAttributeSuffixName( String fileName) ;

}
