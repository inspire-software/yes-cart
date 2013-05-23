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

package org.yes.cart.bulkimport.image.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.yes.cart.bulkimport.service.BulkImportImagesService;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ImageService;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 10:35 AM
 */
public class BulkImportImagesServiceImpl extends AbstractImportService implements BulkImportImagesService {

    private static final Logger LOG = LoggerFactory.getLogger(BulkImportImagesServiceImpl.class);

    private final GenericDAO<Object, Long> genericDAO;

    private final AttributeService attributeService;

    private final ImageService imageService;

    private final Pattern pattern;

    private final String regExp;

    private final String filePatterntRegExp;

    /**
     * Construct bilk import service.
     *
     * @param genericDAO   generic dao
     * @param attributeService attributes dao
     * @param imageService image service
     * @param regExp       image filter.
     */
    public BulkImportImagesServiceImpl(final GenericDAO<Object, Long> genericDAO,
                                       final AttributeService attributeService,
                                       final ImageService imageService,
                                       final String regExp,
                                       final String filePatterntRegExp) {
        this.genericDAO = genericDAO;
        this.attributeService = attributeService;
        this.imageService = imageService;
        this.regExp = regExp;
        this.pattern = Pattern.compile(regExp);
        this.filePatterntRegExp = filePatterntRegExp;
    }

    /**
     * {@inheritDoc}
     */
    public BulkImportResult doImport(final JobContext context) {

        final JobStatusListener statusListener = context.getListener();
        final Set<String> importedFiles = context.getAttribute(JobContextKeys.IMPORT_FILE_SET);
        final String fileName = context.getAttribute(JobContextKeys.IMPORT_FILE);
        final String importFolder = context.getAttribute(JobContextKeys.IMPORT_DIRECTORY_ROOT);
        final String imageVault = context.getAttribute(JobContextKeys.IMAGE_VAULT_PATH);

        String info = MessageFormat.format(
                "start images import with {0} path and {1} file mask",
                importFolder,
                regExp);
        statusListener.notifyMessage(info);
        LOG.info(info);
        File[] files = getFilesToImport(
                importFolder,
                filePatterntRegExp,
                fileName);
        if (files != null) {
            info = MessageFormat.format(
                    "\nINFO found {0} images to import",
                    files.length);
            statusListener.notifyMessage(info);
            LOG.info(info);
            for (File file : files) {
                doImport(file, statusListener, importedFiles, imageVault);
            }

        }
        return BulkImportResult.OK;

    }

    /**
     * Performs import of single image file. With following workflow:
     * first locate the product by code, if product found then insert / update image attribute.
     * The try to locate sku by code, if sku found, then insert / update image attribute.
     * If product or sku image attribute was inserted or update, that copy file to particular folder.
     *
     * @param file          file to import
     * @param statusListener error report
     * @param importedFiles add file to this set if imported it successfuly imported.
     * @param pathToRepository path to image vault
     */
    void doImport(final File file, final JobStatusListener statusListener, final Set<String> importedFiles, final String pathToRepository) {
        final String fileName = file.getName();
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            final String code = matcher.group(1);
            final String suffix = getImageAttributeSuffixName(fileName);
            boolean importRezult = doImportProductImage(statusListener, fileName, code, suffix);
            importRezult |= doImportProductSkuImage(statusListener, fileName, code, suffix);
            if (importRezult) {
                try {

                    String newFileName = imageService.addImageToRepository(file.getAbsolutePath(), code, pathToRepository);
                    final String info = MessageFormat.format(
                            "image {0} {1} added to image repository", file.getAbsolutePath(), newFileName);
                    statusListener.notifyMessage(info);
                    LOG.info(info);
                    importedFiles.add(file.getAbsolutePath());

                } catch (IOException e) {
                    final String err = MessageFormat.format(
                            "can not add {0} to image repository. Try to add it manually. Error is {1}", file.getAbsolutePath(), e.getMessage());
                    LOG.error(err, e);
                    statusListener.notifyError(err);
                }
            }
        } else {
            final String warn = MessageFormat.format(
                    "sorry, product or sku code can not be found with {0} expression for file {1}, but files is matched by the same expression",
                    regExp,
                    fileName);
            statusListener.notifyWarning(warn);
            LOG.warn(warn);
        }
    }

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
            final JobStatusListener statusListener,
            final String fileName,
            final String code,
            final String suffix) {
        Product product = (Product) genericDAO.getScalarResultByNamedQuery("PRODUCT.BY.CODE", true, code);

        if (product == null) {
            final String warn = MessageFormat.format("product with code {0} not found.", code);
            statusListener.notifyWarning(warn);
            LOG.warn(warn);
            return false;
        } else {
            final String attributeCode = Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + suffix;
            final Attribute attribute = attributeService.findByAttributeCode(attributeCode);
            if (attribute == null) {
                final String warn = MessageFormat.format("attribute with code {0} not found.", attributeCode);
                statusListener.notifyWarning(warn);
                LOG.warn(warn);
                return false;
            } else {
                AttrValueProduct imageAttribute = (AttrValueProduct) product.getAttributeByCode(attributeCode);
                if (imageAttribute == null) {
                    imageAttribute = genericDAO.getEntityFactory().getByIface(AttrValueProduct.class);
                    imageAttribute.setProduct(product);
                    imageAttribute.setAttribute(attribute);
                    product.getAttributes().add(imageAttribute);
                }
                imageAttribute.setVal(fileName);
                final String info = MessageFormat.format("file {0} attached as {1} to product {2}", fileName, attributeCode, product.getCode());
                statusListener.notifyMessage(info);
                LOG.info(info);
            }
        }
        try {
            genericDAO.saveOrUpdate(product);
            return true;

        } catch (DataIntegrityViolationException e) {
            final String err = MessageFormat.format("image {0} for product with code {1} not found.", fileName, product.getCode());
            LOG.error(err, e);
            statusListener.notifyError(err);
            return false;

        }
    }

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
            final JobStatusListener statusListener,
            final String fileName,
            final String code,
            final String suffix) {
        ProductSku productSku = (ProductSku) genericDAO.getScalarResultByNamedQuery("PRODUCT.SKU.BY.CODE", code);
        if (productSku == null) {
            final String warn = MessageFormat.format("product sku with code {0} not found.", code);
            statusListener.notifyWarning(warn);
            LOG.warn(warn);
            return false;
        } else {
            final String attributeCode = Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + suffix;
            final Attribute attribute = attributeService.findByAttributeCode(attributeCode);
            if (attribute == null) {
                final String warn = MessageFormat.format("attribute with code {0} not found.", attributeCode);
                statusListener.notifyWarning(warn);
                LOG.warn(warn);
                return false;
            } else {
                AttrValueProductSku imageAttibute = (AttrValueProductSku) productSku.getAttributeByCode(attributeCode);
                if (imageAttibute == null) {
                    imageAttibute = genericDAO.getEntityFactory().getByIface(AttrValueProductSku.class);
                    imageAttibute.setProductSku(productSku);
                    imageAttibute.setAttribute(attribute);
                    productSku.getAttributes().add(imageAttibute);
                }
                imageAttibute.setVal(fileName);
                final String info = MessageFormat.format("file {0} attached as {1} to product sku {2}",
                        fileName,
                        attributeCode,
                        productSku.getCode());
                statusListener.notifyMessage(info);
                LOG.info(info);
            }
        }
        try {
            genericDAO.saveOrUpdate(productSku);
            return true;
        } catch (DataIntegrityViolationException e) {
            final String err = MessageFormat.format("image {0} for product sku with code {1} not found.", fileName, productSku.getCode());
            LOG.error(err, e);
            statusListener.notifyError(err);
            return false;
        }
    }


    /**
     * Get the suffix of IMAGE or SKUIMAGE attribute name by given file name.
     * a  - 0, b - 1, etc
     *
     * @param fileName file name.
     * @return image attribute suffix name
     */
    String getImageAttributeSuffixName(final String fileName) {
        int startIndex = fileName.lastIndexOf('_') + 1;
        int endIndex = fileName.lastIndexOf('.');
        String suffixChar = fileName.substring(startIndex, endIndex);
        return String.valueOf(0 + suffixChar.charAt(0) - 'a');
    }


}
