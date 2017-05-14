/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.media.MediaFileNameStrategy;
import org.yes.cart.service.media.MediaFileNameStrategyResolver;
import org.yes.cart.stream.io.IOProvider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Image service to resize and store resized image.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImageServiceImpl extends BaseGenericServiceImpl<SeoImage> implements ImageService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageServiceImpl.class);

    private String allowedSizes;

    private boolean cropToFit;
    private int forceCropToFitOnSize;

    private boolean replaceFilesModeOn;

    private Color defaultBorder;
    private final Color alphaBorder = new Color(0, 0, 0, 0);

    private final MediaFileNameStrategyResolver mediaFileNameStrategyResolver;

    private final GenericDAO<SeoImage, Long> seoImageDao;

    private final IOProvider ioProvider;


    /**
     * Construct image service.
     *
     * @param seoImageDao   image seo dao
     * @param mediaFileNameStrategyResolver the image name strategy resolver
     * @param allowedSizes  sizes allowed for resizing
     * @param borderColorR  red border density
     * @param borderColorG  green border color density
     * @param borderColorB  blue border color density
     * @param cropToFit     setting this to true will crop the image to proper ratio
     *                      prior to scaling so that scaled image fills all the space.
     *                      This is useful for those who wish to have images that fill
     *                      all space dedicated for image without having border around
     *                      the image. For those who wish images of products in the middle
     * @param forceCropToFitOnSize forcefully use cropping if scale is below given size
     *                             This option is very useful for small thumbs (<100px)
     *                             as you really cannot see much with added padding for
     * @param replaceFilesModeOn if set to true will overwrite existing file if name is
     *                           the same. Recommended setting is 'true'.
     * @param ioProvider IO provider
     */
    public ImageServiceImpl(final GenericDAO<SeoImage, Long> seoImageDao,
                            final MediaFileNameStrategyResolver mediaFileNameStrategyResolver,
                            final String allowedSizes,
                            final int borderColorR,
                            final int borderColorG,
                            final int borderColorB,
                            final boolean cropToFit,
                            final int forceCropToFitOnSize,
                            final boolean replaceFilesModeOn,
                            final IOProvider ioProvider) {

        super(seoImageDao);

        this.seoImageDao = seoImageDao;
        this.mediaFileNameStrategyResolver = mediaFileNameStrategyResolver;
        this.allowedSizes = allowedSizes;
        this.ioProvider = ioProvider;
        this.defaultBorder = new Color(borderColorR, borderColorG, borderColorB);

        this.cropToFit = cropToFit;
        this.forceCropToFitOnSize = forceCropToFitOnSize;

        this.replaceFilesModeOn = replaceFilesModeOn;
    }

    public void setConfig(final Resource config) throws IOException {

        final Properties properties = new Properties();
        properties.load(config.getInputStream());

        this.allowedSizes = properties.getProperty("imagevault.resize.allowed.sizes", this.allowedSizes);
        this.cropToFit = Boolean.valueOf(properties.getProperty("imagevault.resize.crop.to.fit", String.valueOf(this.cropToFit)));
        this.forceCropToFitOnSize = NumberUtils.toInt(properties.getProperty("imagevault.resize.force.crop.to.fit.on.size"), this.forceCropToFitOnSize);

        this.defaultBorder = new Color(
                NumberUtils.toInt(properties.getProperty("imagevault.resize.border.color.R"), this.defaultBorder.getRed()),
                NumberUtils.toInt(properties.getProperty("imagevault.resize.border.color.G"), this.defaultBorder.getGreen()),
                NumberUtils.toInt(properties.getProperty("imagevault.resize.border.color.B"), this.defaultBorder.getBlue())
        );

    }

    /**
     * {@inheritDoc}
     */
    public byte[] resizeImage(final String filename,
                              final byte[] content,
                              final String width,
                              final String height) {
        return resizeImage(filename, content, width, height, cropToFit);
    }

    /**
     * {@inheritDoc}
     */
    public byte[] resizeImage(final String filename,
                              final byte[] content,
                              final String width,
                              final String height,
                              final boolean cropToFit) {

        try {

            final InputStream bis = new ByteArrayInputStream(content);

            final BufferedImage originalImg = ImageIO.read(bis);
            final String codec = getCodecFromFilename(filename);
            final boolean supportsAlpha = hasAlphaSupport(codec);

            int x = NumberUtils.toInt(width);
            int y = NumberUtils.toInt(height);
            int originalX = originalImg.getWidth();
            int originalY = originalImg.getHeight();

            boolean doCropToFit = cropToFit || x < forceCropToFitOnSize || y < forceCropToFitOnSize;

            final int imageType = originalImg.getType();

            final Image resizedImg;
//            final BufferedImage resizedImg;
            final int padX, padY;
            if (doCropToFit) {
                // crop the original to best fit of target size
                int[] cropDims = cropImageToCenter(x, y, originalX, originalY);
                padX = 0;
                padY = 0;

                final BufferedImage croppedImg = originalImg.getSubimage(cropDims[0], cropDims[1], cropDims[2], cropDims[3]);
                resizedImg = croppedImg.getScaledInstance(x, y, Image.SCALE_SMOOTH);

//                final BufferedImage croppedImg = originalImg.getSubimage(cropDims[0], cropDims[1], cropDims[2], cropDims[3]);
//                resizedImg = new BufferedImage(y, x, imageType);
//                Graphics2D graphics = resizedImg.createGraphics();
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                graphics.drawImage(croppedImg, 0, 0, x, y, null);
            } else {
                int[] scaleDims = scaleImageToCenter(x, y, originalX, originalY);
                padX = scaleDims[0];
                padY = scaleDims[1];

                resizedImg = originalImg.getScaledInstance(scaleDims[2], scaleDims[3], Image.SCALE_SMOOTH);

//                resizedImg = new BufferedImage(scaleDims[3], scaleDims[2], imageType);
//                Graphics2D graphics = resizedImg.createGraphics();
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                graphics.drawImage(originalImg, 0, 0, scaleDims[2], scaleDims[3], null);
            }

            // base canvas
            final BufferedImage resizedImgFinal = new BufferedImage(x, y, imageType);

            // fill base color
            final Graphics2D graphics = resizedImgFinal.createGraphics();
            graphics.setPaint(supportsAlpha ? alphaBorder : defaultBorder);
            graphics.fillRect(0, 0, x, y);

            // insert scaled image
            graphics.drawImage(resizedImg, padX, padY, null);

            final ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ImageIO.write(resizedImgFinal, codec, bos);

            return bos.toByteArray();

        } catch (Exception exp) {
            LOG.error("Unable to resize image " + filename, exp);
        }

        return new byte[0];

    }

    /**
     * Get the image codec from filename's given extension
     * <p/>
     * FIXME: This is a very primitive check - need something more robust, MIME Types maybe?
     *
     * @param filename file name
     * @return codec name
     */
    private String getCodecFromFilename(final String filename) {
        final String fileExt = filename.substring(filename.lastIndexOf('.') + 1);
        final String ext = fileExt.toLowerCase();
        if ("jpg".equals(ext)) {
            return "jpeg";
        } else if ("tif".equals(ext)) {
            return "tiff";
        }
        return ext;
    }

    /**
     * Check if image codec supports alpha channel.
     *
     * FIXME: simple check based on codec name
     *
     * @param codec code
     * @return true if alpha is supported
     */
    private boolean hasAlphaSupport(final String codec) {
        return "png".equals(codec);
    }

    int[] cropImageToCenter(final int targetX, final int targetY, final int originalX, final int originalY) {

        // calculate crop so that we can scale to fit
        BigDecimal sourceRatio = new BigDecimal(originalX).divide(new BigDecimal(originalY), 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal targetRatio = new BigDecimal(targetX).divide(new BigDecimal(targetY), 10, BigDecimal.ROUND_HALF_UP);
        final int cropWidth, cropHeight, cropX, cropY;
        if (sourceRatio.compareTo(targetRatio) < 0) { // need to crop by height
            cropWidth = originalX;
            cropHeight = new BigDecimal(originalY).divide((targetRatio.divide(sourceRatio, 10, BigDecimal.ROUND_HALF_UP)), 0, BigDecimal.ROUND_HALF_UP).intValue();
            cropX = 0;
            cropY = (originalY - cropHeight) / 2;
        } else { // need to crop by width
            cropHeight = originalY;
            cropWidth = new BigDecimal(originalX).divide((sourceRatio.divide(targetRatio, 10, BigDecimal.ROUND_HALF_UP)), 0, BigDecimal.ROUND_HALF_UP).intValue();
            cropX = (originalX - cropWidth) / 2;
            cropY = 0;
        }

        return new int[] { cropX, cropY, cropWidth, cropHeight };

    }

    int[] scaleImageToCenter(final int targetX, final int targetY, final int originalX, final int originalY) {

        final BigDecimal xScale = new BigDecimal(targetX).divide(new BigDecimal(originalX), 10, BigDecimal.ROUND_HALF_UP);
        final BigDecimal yScale = new BigDecimal(targetY).divide(new BigDecimal(originalY), 10, BigDecimal.ROUND_HALF_UP);

        final int scaleWidth, scaleHeight, padX, padY;
        if (xScale.compareTo(yScale) < 0) { // need to scale by height
            scaleWidth = targetX;
            scaleHeight = new BigDecimal(originalY).multiply(xScale).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            padX = 0;
            padY = (targetY - scaleHeight) / 2;
        } else { // need to scale by width
            scaleHeight = targetY;
            scaleWidth = new BigDecimal(originalX).multiply(yScale).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            padX = (targetX - scaleWidth) / 2;
            padY = 0;
        }

        return new int[] { padX, padY, scaleWidth, scaleHeight };

    }

    /**
     * {@inheritDoc}
     */
    public boolean isSizeAllowed(final String size) {

        final SystemService systemService = getSystemService();
        final String allowedSizes = systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_ALLOWED_IMAGE_SIZES);
        if (StringUtils.isNotBlank(allowedSizes)) {
            return allowedSizes.contains(size);
        }
        return this.allowedSizes.contains(size);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSizeAllowed(final String width, final String height) {
        return isSizeAllowed(width + "x" + height);
    }

    /** {@inheritDoc} */
    public MediaFileNameStrategy getImageNameStrategy(final String url) {
        return mediaFileNameStrategyResolver.getMediaFileNameStrategy(url);
    }

    /** {@inheritDoc} */
    public byte[] resizeImage(final String original,
                              final String resized,
                              final String width,
                              final String height) {
        return resizeImage(original, resized, width, height, cropToFit);
    }

    /** {@inheritDoc} */
    public byte[] resizeImage(final String original,
                              final String resized,
                              final String width,
                              final String height,
                              final boolean cropToFit) {

        try {
            final Map<String, Object> ctx = Collections.EMPTY_MAP;

            if (resized != null) {

                final boolean resizedIsNewer = ioProvider.isNewerThan(resized, original, ctx);

                if (!resizedIsNewer) {

                    final byte[] originalContent = ioProvider.read(original, ctx);
                    final byte[] resizedContent = resizeImage(original, originalContent, width, height);

                    if (resizedContent.length > 0) {
                        ioProvider.write(resized, resizedContent, ctx);
                        return resizedContent;
                    }

                    /*
                         If we failed to resize this is probably due to invalid color metadata for the original image.
                         JDK image API will fail if the metadata is incorrect. In order to fail gracefully we
                         just pass back the original bytes so that the original image is used instead. There will be an
                         ERROR log produced by catch block from #resizeImage(), so sys admins should provide regular
                         feedback to business users to fix these images. Usually the fix is  simply erasing all meta
                         from the image.
                     */
                    return originalContent;
                }

                return ioProvider.read(resized, ctx);
            }
            return ioProvider.read(original, ctx);

        } catch (IOException ioe) {
            LOG.error("Unable to resize image " + original + " to " + resized + ", caused by: " + ioe.getMessage(), ioe);
            return new byte[0];
        }
    }

    /** {@inheritDoc} */
    public boolean isImageInRepository(final String fullFileName,
                                       final String code,
                                       final String storagePrefix,
                                       final String pathToRepository) {

        final MediaFileNameStrategy strategy = getImageNameStrategy(storagePrefix);
        final String filename = strategy.resolveFileName(fullFileName);

        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(filename, code, null);

        return ioProvider.exists(pathInRepository, Collections.EMPTY_MAP);

    }

    /** {@inheritDoc} */
    public String addImageToRepository(final String fullFileName,
                                       final String code,
                                       final byte[] imgBody,
                                       final String storagePrefix,
                                       final String pathToRepository) throws IOException {

        final MediaFileNameStrategy strategy = getImageNameStrategy(storagePrefix);
        final String filename = strategy.resolveFileName(fullFileName);
        final String suffix = strategy.resolveSuffix(fullFileName);
        final String locale = strategy.resolveLocale(fullFileName);

        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(filename, code, null);

        final String uniqueName = createRepositoryUniqueName(pathInRepository, ioProvider, strategy, code, suffix, locale);

        ioProvider.write(uniqueName, imgBody, Collections.EMPTY_MAP);

        return strategy.resolveFileName(uniqueName);
    }


    /*
     * Check is given file name present on disk and create new if necessary.
     * Return sequential file name.
     */
    String createRepositoryUniqueName(final String fileName,
                                      final IOProvider ioProvider,
                                      final MediaFileNameStrategy strategy,
                                      final String code,
                                      final String suffix,
                                      final String locale) {
        if (this.replaceFilesModeOn) {
            return fileName;
        }
        if (ioProvider.exists(fileName, Collections.EMPTY_MAP)) {
            final String newFileName = strategy.createRollingFileName(fileName, code, suffix, locale);
            return createRepositoryUniqueName(newFileName, ioProvider, strategy, code, suffix, locale);
        }
        return fileName;
    }

    /** {@inheritDoc} */
    public byte[] imageToByteArray(final String fileName,
                                   final String code,
                                   final String storagePrefix,
                                   final String pathToRepository) throws IOException {

        final MediaFileNameStrategy strategy = getImageNameStrategy(storagePrefix);
        final String file = strategy.resolveFileName(fileName);
        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(file, code, null);

        return ioProvider.read(pathInRepository, Collections.EMPTY_MAP);

    }

    /** {@inheritDoc}*/
    public boolean deleteImage(final String imageFileName,
                               final String storagePrefix,
                               final String pathToRepository) {

        final SeoImage seoImage = getSeoImage(storagePrefix + imageFileName);
        if (seoImage != null) {
            getGenericDao().delete(seoImage);
        }

        final MediaFileNameStrategy strategy = getImageNameStrategy(storagePrefix);
        final String file = strategy.resolveFileName(imageFileName);
        final String code = strategy.resolveObjectCode(imageFileName);
        String pathInRepository = pathToRepository + strategy.resolveRelativeInternalFileNamePath(file, code, null);

        try {
            ioProvider.delete(pathInRepository, Collections.EMPTY_MAP);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public SeoImage getSeoImage(final String imageName) {
        java.util.List<SeoImage> seoImages = seoImageDao.findByCriteria(Restrictions.eq("imageName", imageName));
        if (seoImages == null || seoImages.isEmpty()) {
            return null;
        }
        return seoImages.get(0);
    }

    /**
     * Spring IoC.
     *
     * @return system service
     */
    public SystemService getSystemService() {
        return null;
    }

}
