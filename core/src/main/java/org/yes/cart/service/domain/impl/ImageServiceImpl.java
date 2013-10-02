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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.service.image.ImageNameStrategyResolver;

import javax.media.jai.*;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.*;
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

    private final boolean cropToFit;

    private final double colorR;
    private final double colorG;
    private final double colorB;

    private final ImageNameStrategyResolver imageNameStrategyResolver;

    private final GenericDAO<SeoImage, Long> seoImageDao;
    private final RenderingHints renderingHints;


    /**
     * Construct image service.
     *
     * @param seoImageDao   image seo dao
     * @param imageNameStrategyResolver the image name strategy resolver
     * @param allowedSizes  sizes allowed for resizing
     * @param borderColorR  red border density
     * @param borderColorG  green border color density
     * @param borderColorB  blue border color density
     * @param cropToFit     setting this to true will crop the image to proper ratio
     *                      prior to scaling so that scaled image fills all the space.
     *                      This is useful for those who wish to have images that fill
     *                      all space dedicated for image without having border around
     *                      the image. For those who wish images of products in the middle
     *                      e.g. as it is in YC demo better to set this to false.
     */
    public ImageServiceImpl(
            final GenericDAO<SeoImage, Long> seoImageDao,
            final ImageNameStrategyResolver imageNameStrategyResolver,
            final String allowedSizes,
            final double borderColorR,
            final double borderColorG,
            final double borderColorB,
            final boolean cropToFit) {

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
        // When scaling has rounding errors which leaves blank edges this setting will force JAI to
        // copy adjacent pixels instead of zero fill (which is default that leaves a black border)
        this.renderingHints.put(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));
        this.cropToFit = cropToFit;
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

        createFolder(resized);

        RenderedImage imageRef = image;

        int x = NumberUtils.toInt(width);
        int y = NumberUtils.toInt(height);
        int originalX = image.getWidth();
        int originalY = image.getHeight();

        RenderedOp cropOp = null;
        if (cropToFit) {
            // crop the original to best fit of target size
            cropOp = cropImageToCenter(imageRef, x, y, originalX, originalY);
            imageRef = cropOp.getAsBufferedImage();
        }

        originalX = imageRef.getWidth();
        originalY = imageRef.getHeight();

        // resize to target dimensions
        RenderedOp resizeOp = resizeImage(imageRef, x, y, originalX, originalY);
        imageRef = resizeOp.getAsBufferedImage();

        int xNew = imageRef.getWidth();
        int yNew = imageRef.getHeight();

        int lPad = (x - xNew) / 2;
        int rPad = x - xNew - lPad;
        int tPad = (y - yNew) / 2;
        int bPad = y - yNew - tPad;

        // add padding if it needs
        RenderedOp paddedOp = addPadding(imageRef, tPad, rPad, bPad, lPad);

        JAI.create("filestore", paddedOp.getAsBufferedImage(), resized, getCodecFromFilename(original));

        paddedOp.dispose();
        resizeOp.dispose();
        if (cropToFit) {
            cropOp.dispose();
        }

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


    private RenderedOp addPadding(final RenderedImage image, final int tPad, final int rPad, final int bPad, final int lPad) {
        ParameterBlock borderParams = new ParameterBlock();
        borderParams.addSource(image);
        borderParams.add(lPad); //left pad
        borderParams.add(rPad); //right pad
        borderParams.add(tPad); //top pad
        borderParams.add(bPad); //bottom pad
        double fill[] = {colorR, colorG, colorB};
        borderParams.add(new BorderExtenderConstant(fill));//type

        return JAI.create("border", borderParams);
    }

    private RenderedOp cropImageToCenter(final RenderedImage image, final int targetX, final int targetY, final int originalX, final int originalY) {

        // calculate crop so that we can scale to fit
        BigDecimal sourceRatio = new BigDecimal(originalX).divide(new BigDecimal(originalY), 10, BigDecimal.ROUND_UP);
        BigDecimal targetRatio = new BigDecimal(targetX).divide(new BigDecimal(targetY), 10, BigDecimal.ROUND_UP);
        final int cropWidth, cropHeight, cropX, cropY;
        if (sourceRatio.compareTo(targetRatio) < 0) { // need to crop by height
            cropWidth = originalX;
            cropHeight = new BigDecimal(originalY).divide((targetRatio.divide(sourceRatio, 10, BigDecimal.ROUND_UP)), 0, BigDecimal.ROUND_UP).intValue();
            cropX = 0;
            cropY = (originalY - cropHeight) / 2;
        } else { // need to crop by width
            cropHeight = originalY;
            cropWidth = new BigDecimal(originalX).divide((sourceRatio.divide(targetRatio, 10, BigDecimal.ROUND_UP)), 0, BigDecimal.ROUND_UP).intValue();
            cropX = (originalX - cropWidth) / 2;
            cropY = 0;
        }


        ParameterBlock cropParams = new ParameterBlock();
        cropParams.addSource(image);
        cropParams.add(Float.valueOf(cropX)); // X
        cropParams.add(Float.valueOf(cropY)); // Y
        cropParams.add(Float.valueOf(cropWidth)); // width
        cropParams.add(Float.valueOf(cropHeight)); // height

        return JAI.create("crop", cropParams);

    }

    private RenderedOp resizeImage(final RenderedImage image, final int targetX, final int targetY, final int originalX, final int originalY) {

        final BigDecimal xScale = new BigDecimal(targetX).divide(new BigDecimal(originalX), 10, BigDecimal.ROUND_UP);
        final BigDecimal yScale = new BigDecimal(targetY).divide(new BigDecimal(originalY), 10, BigDecimal.ROUND_UP);
        final float scale = xScale.min(yScale).floatValue();

        return ScaleDescriptor.create(
                image, scale, scale, 0f, 0f,
                Interpolation.getInstance(Interpolation.INTERP_BICUBIC),
                renderingHints);
    }


    /**
     * Create folder(s) to store scaled image.
     *
     * @param fullPath full file name of scaled image
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
    public String addImageToRepository(final String fullFileName,
                                        final String code,
                                        final String pathToRepository) throws IOException {
        File file = new File(fullFileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(StringUtils.EMPTY).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(createRepositoryUniqueName(pathInRepository));
        FileUtils.copyFile(file, destinationFile);
        return destinationFile.getName();
    }

    /** {@inheritDoc} */
    public String addImageToRepository(final String fullFileName, final String code,
                                        final byte[] imgBody, final String storagePrefix) throws IOException {
        return addImageToRepository(fullFileName, code, imgBody, storagePrefix, StringUtils.EMPTY);
    }

    /** {@inheritDoc} */
    public String addImageToRepository(final String fullFileName, final String code,
                                        final byte[] imgBody, final String storagePrefix,
                                        final String pathToRepository) throws IOException {
        File file = new File(fullFileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(storagePrefix).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(createRepositoryUniqueName(pathInRepository));
        FileUtils.writeByteArrayToFile(destinationFile, imgBody);
        return destinationFile.getName();
    }


    /**
     * Check is given file name present on disk and create new if necessary.
     * @param fileName given file name
     * @return sequental file name.
     */
    String createRepositoryUniqueName(final String fileName) {
        File destinationFile = new File(fileName);
        while(destinationFile.exists()) {
            final String newFileName = createRollingFileName(fileName);
            return createRepositoryUniqueName(newFileName);
        }
        return fileName;
    }

    /**
     * Create new sequental file name, which will have _seqnumber before file extension.
     * Example /tmp/file.txt -> /tmp/file_1.txt
     *  /tmp/file_1.txt -> /tmp/file_2.txt
     * @param fileName given file name
     * @return sequental file name.
     */
    String createRollingFileName(final String fileName) {
        Assert.notNull(fileName, "file name must be not null");
        int idx = fileName.lastIndexOf(".");
        final String [] fileParts;
        if (idx == -1) {
            fileParts =  new String [] {fileName};
        } else {
            fileParts =  new String [2];
            fileParts[0] = fileName.substring(0, idx);
            fileParts[1] = fileName.substring(idx + 1);
        }
        final String [] numberSequence = fileParts[0].split("_");
        int seq = 1;
        if (NumberUtils.isDigits(numberSequence[numberSequence.length-1])) {
            seq = NumberUtils.toInt(numberSequence[numberSequence.length-1]) + 1 ;
        }
        return numberSequence[0] + "_" + seq + ( fileParts.length>1? "." +  fileParts[fileParts.length-1] : "");
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
    @Cacheable(value = "imageService-seoImage")
    public SeoImage getSeoImage(final String imageName) {
        java.util.List<SeoImage> seoImages = seoImageDao.findByCriteria(Restrictions.eq("imageName", imageName));
        if (seoImages == null || seoImages.isEmpty()) {
            return null;
        }
        return seoImages.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage"
    }, allEntries = true)
    public SeoImage update(SeoImage instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage"
    }, allEntries = true)
    public void delete(SeoImage instance) {
        super.delete(instance);
    }
}
