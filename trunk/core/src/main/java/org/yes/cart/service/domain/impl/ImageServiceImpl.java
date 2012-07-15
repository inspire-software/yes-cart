
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

package org.yes.cart.service.domain.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.cache.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.service.image.ImageNameStrategyResolver;

import javax.media.jai.*;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Image service to resize and store resized image.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImageServiceImpl
        extends BaseGenericServiceImpl<SeoImage>
        implements ImageService {

    private final String allowedSizes;

    private final double colorR;
    private final double colorG;
    private final double colorB;

    private final ImageNameStrategyResolver imageNameStrategyResolver;

    private final GenericDAO<SeoImage, Long> seoImageDao;
    private final RenderingHints renderingHints;
    private final Color rectColor;


    /**
     * Construct image service.
     *
     * @param allowedSizes  sizes allowed for resizing
     * @param borderColorR  red border density
     * @param borderColorG  green border color density
     * @param borderColorB  blue border color density
     * @param seoImageDao   image seo dao
     * @param imageNameStrategyResolver the image name strategy resolver
     */
    public ImageServiceImpl(
            final GenericDAO<SeoImage, Long> seoImageDao,
            final ImageNameStrategyResolver imageNameStrategyResolver,
            final String allowedSizes,
            final double borderColorR,
            final double borderColorG,
            final double borderColorB) {

        super(seoImageDao);

        this.seoImageDao = seoImageDao;
        this.imageNameStrategyResolver = imageNameStrategyResolver;
        this.allowedSizes = allowedSizes;
        this.colorR = borderColorR;
        this.colorG = borderColorG;
        this.colorB = borderColorB;
        this.renderingHints = new RenderingHints(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        this.rectColor = new Color((int) colorR, (int) colorG, (int) colorB);

    }


    /**
     * Resize given image file to requested width and height.
     * @param original path to original image
     * @param resized path to resized image
     * @param width new requested width
     * @param height new requested height
     */
    public void resizeImage(
            final String original,
            final String resized,
            final String width,
            final String height) {

        PlanarImage image = JAI.create("fileload", original);

        int x = NumberUtils.toInt(width);
        int y = NumberUtils.toInt(height);
        int originalX = image.getWidth();
        int originalY = image.getHeight();

        // calculate minimal scale factor
        BigDecimal xScale = new BigDecimal((double) x).divide(new BigDecimal((double) originalX), 10, BigDecimal.ROUND_UP);
        BigDecimal yScale = new BigDecimal((double) y).divide(new BigDecimal((double) originalY), 10, BigDecimal.ROUND_UP);
        BigDecimal scale = xScale.min(yScale);
        // getByKey pads from left and top
        int xPad = (x - (new BigDecimal((double) originalX).multiply(scale)).intValue()) / 2;
        int yPad = (y - (new BigDecimal((double) originalY).multiply(scale)).intValue()) / 2;


        createFolder(resized);
        RenderedOp tmpImage = resizeImage(image, scale);
        BufferedImage bufferedImage = drawBorder(tmpImage);
        RenderedOp borderedImage = addPadding(bufferedImage, xPad, yPad);
        JAI.create("filestore", borderedImage.getAsBufferedImage(), resized, getCodecFromFilename(original));
        borderedImage.dispose();
        tmpImage.dispose();

    }

    /**
     * To prevent black border after resize, just draw a square with specified color.
     * see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6331420 for more details
     *
     * @param tmpImage resized image
     * @return buffered image with rectangle on border
     */
    private BufferedImage drawBorder(final RenderedOp tmpImage) {
        final BufferedImage bufferedImage = tmpImage.getAsBufferedImage();
        final Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(rectColor);
        graphics.drawRect(0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1);
        return bufferedImage;
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


    private RenderedOp addPadding(final BufferedImage bufferedImage, final int xPad, final int yPad) {
        ParameterBlock borderParams = new ParameterBlock();
        borderParams.addSource(bufferedImage);
        borderParams.add(xPad); //left pad
        borderParams.add(xPad); //right pad
        borderParams.add(yPad); //top pad
        borderParams.add(yPad); //bottom pad
        double fill[] = {colorR, colorG, colorB};
        borderParams.add(new BorderExtenderConstant(fill));//type

        return JAI.create("border", borderParams);
    }

    private RenderedOp resizeImage(final RenderedImage image, final BigDecimal scale) {

        final Float scalef = scale.floatValue();
        return ScaleDescriptor.create(
                image, scalef, scalef, 0f, 0f,
                Interpolation.getInstance(Interpolation.INTERP_BICUBIC),
                renderingHints);
    }


    /**
     * Create fodler(s) to store resized image.
     *
     * @param fullPath full file name of resized image
     * @return true if folder created
     */
    private boolean createFolder(final String fullPath) {
        int idx = fullPath.lastIndexOf(File.separator);
        if (idx == -1) {
            idx = fullPath.lastIndexOf('/');
            if (idx == -1) {
                idx = fullPath.lastIndexOf('\\');
            }
        }

        final String dirs = fullPath.substring(0, idx);

        File dir = new File(dirs);
        return dir.mkdirs();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSizeAllowed(final String size) {
        return allowedSizes.contains(size);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSizeAllowed(final String width, final String height) {
        return isSizeAllowed(width + "x" + height);
    }

    /**
     * Get the image name strategy.
     *
     * @return image name strategy
     */
    public ImageNameStrategy getImageNameStrategy(final String url) {
        return imageNameStrategyResolver.getImageNameStrategy(url);
    }

    /**
     * Add the given file to image repository during bulk import.
     * At this momen only product images can be imported.
     *
     * @param fullFileName full path to image file.
     * @param code         product or sku code.
     * @return true if file was added successfully
     */
    public boolean addImageToRepository(final String fullFileName,
                                        final String code,
                                        final String pathToRepository) throws IOException {
        File file = new File(fullFileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(StringUtils.EMPTY).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(pathInRepository);
        FileUtils.copyFile(file, destinationFile);
        return destinationFile.exists();
    }

    /** {@inheritDoc} */
    public boolean addImageToRepository(final String fullFileName, final String code,
                                        final byte[] imgBody, final String storagePrefix) throws IOException {
        return addImageToRepository(fullFileName, code, imgBody, storagePrefix, StringUtils.EMPTY);
    }

    /** {@inheritDoc} */
    public boolean addImageToRepository(final String fullFileName, final String code,
                                        final byte[] imgBody, final String storagePrefix,
                                        final String pathToRepository) throws IOException {
        File file = new File(fullFileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(storagePrefix).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(pathInRepository);
        FileUtils.writeByteArrayToFile(destinationFile, imgBody);
        return destinationFile.exists();
    }

    /** {@inheritDoc} */
    public byte[] getImageAsByteArray(final String fileName,
                                      final String code,
                                      final String storagePrefix,
                                      final String pathToRepository) throws IOException {
        final File file = new File(fileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(storagePrefix).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(pathInRepository);
        return FileUtils.readFileToByteArray(destinationFile);
    }

    /** {@inheritDoc}*/
    public boolean deleteImage(final String imageFileName) {
        final SeoImage seoImage = getSeoImage(imageFileName);
        if (seoImage != null) {
            getGenericDao().delete(seoImage);
        }
        final File file = new File(imageFileName);
        return file.delete();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageServiceImplMethodCache")
    public SeoImage getSeoImage(final String imageName) {
        java.util.List<SeoImage> seoImages = seoImageDao.findByCriteria(Restrictions.eq("imageName", imageName));
        if (seoImages == null || seoImages.isEmpty()) {
            return null;
        }
        return seoImages.get(0);
    }

}
