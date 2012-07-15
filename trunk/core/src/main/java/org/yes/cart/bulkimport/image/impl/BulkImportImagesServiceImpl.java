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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.yes.cart.bulkimport.service.BulkImportImagesService;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
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

    private final GenericDAO<Object, Long> genericDAO;

    private final GenericDAO<Attribute, Long> attributeDao;

    private final ImageService imageService;

    private String pathToImportFolder;

    private final Pattern pattern;

    private final String regExp;

    private final String filePatterntRegExp;

    private String pathToRepository;

    /**
     * {@inheritDoc}
     */
    public void setPathToRepository(final String pathToRepository) {
        this.pathToRepository = pathToRepository;
    }

    /**
     * Construct bilk import service.
     *
     * @param genericDAO   generic dao
     * @param attributeDao attributes dao
     * @param imageService image service
     * @param regExp       image filter.
     */
    public BulkImportImagesServiceImpl(final GenericDAO<Object, Long> genericDAO,
                                       final GenericDAO<Attribute, Long> attributeDao,
                                       final ImageService imageService,
                                       final String regExp,
                                       final String filePatterntRegExp) {
        this.genericDAO = genericDAO;
        this.attributeDao = attributeDao;
        this.imageService = imageService;
        this.regExp = regExp;
        this.pattern = Pattern.compile(regExp);
        this.filePatterntRegExp = filePatterntRegExp;
    }


    /**
     * {@inheritDoc}
     */
    public void setPathToImportFolder(final String pathToImportFolder) {
        this.pathToImportFolder = pathToImportFolder;
    }

    /**
     * {@inheritDoc}
     */
    public BulkImportResult doImport(final StringBuilder errorReport, final Set<String> importedFiles,
                                     final String fileName, final String importFolder) {
        errorReport.append(MessageFormat.format(
                "\nINFO start images import with {0} path and {1} file mask",
                pathToImportFolder,
                regExp));
        File[] files = getFilesToImport(
                StringUtils.isNotBlank(importFolder) ? importFolder : pathToImportFolder,
                filePatterntRegExp,
                fileName);
        if (files != null) {
            errorReport.append(MessageFormat.format(
                    "\nINFO found {0} images to import",
                    files.length));
            for (File file : files) {
                doImport(file, errorReport, importedFiles);
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
     * @param errorReport   error report
     * @param importedFiles add file to this set if imported it successfuly imported.
     */
    void doImport(final File file, final StringBuilder errorReport, final Set<String> importedFiles) {
        final String fileName = file.getName();
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            final String code = matcher.group(1);
            final String suffix = getImageAttributeSuffixName(fileName);
            boolean importRezult = doImportProductImage(errorReport, fileName, code, suffix);
            importRezult |= doImportProductSkuImage(errorReport, fileName, code, suffix);
            if (importRezult) {
                try {
                    if (imageService.addImageToRepository(file.getAbsolutePath(), code, pathToRepository)) {
                        errorReport.append(MessageFormat.format(
                                "\nINFO image {0} added to image repository", file.getAbsolutePath()));
                        importedFiles.add(file.getAbsolutePath());
                    } else {
                        errorReport.append(MessageFormat.format(
                                "\nWARNING image {0} not added to image repository. Try to add it manually.", file.getAbsolutePath()));
                    }
                } catch (IOException e) {
                    String message = MessageFormat.format(
                            "\nERROR can not add {0} to image repository. Try to add it manually. Error is {1}", file.getAbsolutePath(), e.getMessage());
                    e.printStackTrace();
                    errorReport.append(message);
                }
            }
        } else {
            errorReport.append(MessageFormat.format(
                    "\nWARINIG sorry, product or sku code can not be found with {0} expression for file {1}, but files is mached by the same expression",
                    regExp,
                    fileName));
        }
    }

    /**
     * Prepare import for product image.
     *
     * @param errorReport error report
     * @param fileName    file name without the full path
     * @param code        product code
     * @param suffix      image suffix
     * @return true if given image file attached as attribute to giveb product
     */
    boolean doImportProductImage(
            final StringBuilder errorReport,
            final String fileName,
            final String code,
            final String suffix) {
        Product product = (Product) genericDAO.getScalarResultByNamedQuery("PRODUCT.BY.CODE", true, code);

        if (product == null) {
            errorReport.append(MessageFormat.format("\nWARINIG product with code {0} not found.", code));
            return false;
        } else {
            final String attributeCode = Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + suffix;
            final Attribute attribute = getAttribute(attributeCode);
            if (attribute == null) {
                errorReport.append(MessageFormat.format("\nERROR attribute with code {0} not found.", attributeCode));
                return false;
            } else {
                AttrValueProduct imageAttibute = (AttrValueProduct) product.getAttributeByCode(attributeCode);
                if (imageAttibute == null) {
                    imageAttibute = genericDAO.getEntityFactory().getByIface(AttrValueProduct.class);
                    imageAttibute.setProduct(product);
                    imageAttibute.setAttribute(attribute);
                    product.getAttribute().add(imageAttibute);
                }
                imageAttibute.setVal(fileName);
                errorReport.append(MessageFormat.format("\nINFO file {0} attached as {1} to product {2}", fileName, attributeCode, product.getCode()));
            }
        }
        try {
            genericDAO.saveOrUpdate(product);
            return true;

        } catch (DataIntegrityViolationException e) {
            errorReport.append(MessageFormat.format("\nWARINIG image {0} for product with code {1} not found.", fileName, product.getCode()));
            return false;

        }
    }

    /**
     * Prepare import for product sku image.
     *
     * @param errorReport error report
     * @param fileName    file name without the full path
     * @param code        product sku code
     * @param suffix      image suffix
     * @return true if given image file attached as attribute to giveb product
     */
    boolean doImportProductSkuImage(
            final StringBuilder errorReport,
            final String fileName,
            final String code,
            final String suffix) {
        ProductSku productSku = (ProductSku) genericDAO.getScalarResultByNamedQuery("PRODUCT.SKU.BY.CODE", code);
        if (productSku == null) {
            errorReport.append(MessageFormat.format("\nWARINIG product sku with code {0} not found.", code));
            return false;
        } else {
            final String attributeCode = Constants.PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX + suffix;
            final Attribute attribute = getAttribute(attributeCode);
            if (attribute == null) {
                errorReport.append(MessageFormat.format("\nERROR attribute with code {0} not found.", attributeCode));
                return false;
            } else {
                AttrValueProductSku imageAttibute = (AttrValueProductSku) productSku.getAttributeByCode(attributeCode);
                if (imageAttibute == null) {
                    imageAttibute = genericDAO.getEntityFactory().getByIface(AttrValueProductSku.class);
                    imageAttibute.setProductSku(productSku);
                    imageAttibute.setAttribute(attribute);
                    productSku.getAttribute().add(imageAttibute);
                }
                imageAttibute.setVal(fileName);
                errorReport.append(MessageFormat.format("\nINFO file {0} attached as {1} to product sku {2}",
                        fileName,
                        attributeCode,
                        productSku.getCode()));

            }
        }
        try {
            genericDAO.saveOrUpdate(productSku);
            return true;
        } catch (DataIntegrityViolationException e) {
            errorReport.append(MessageFormat.format("\nWARINIG image {0} for product sku with code {1} not found.", fileName, productSku.getCode()));
            return false;
        }
    }

    private Attribute getAttribute(final String code) {
        return attributeDao.findSingleByNamedQuery("ATTRIBUTE.BY.CODE", code); //TODO user service instead of dao
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
