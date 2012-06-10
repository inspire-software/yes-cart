package org.yes.cart.bulkimport.model;

/**
 * Common descriptor for import file.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface ImportFile {

    /**
     * Get the file name mask. For example *.csv
     *
     * @return File name mask
     */
    String getFileNameMask();

    /**
     * Set the file name mask.
     *
     * @param fileNameMask file name mask
     */
    void setFileNameMask(String fileNameMask);

    /**
     * Fet the file encoding.
     *
     * @return file encoding
     */
    String getFileEncoding();

    /**
     * Set file encoding.
     *
     * @param fileEncoding file encoding.
     */
    void setFileEncoding(String fileEncoding);

    /**
     * Get the entity interface name. The real entity instance will be
     * created with {@link org.yes.cart.dao.EntityFactory}
     *
     * @return entity interface name
     */
    String getEntityName();

    /**
     * Set the entity interface name.
     *
     * @param entityName entity interface name
     */
    void setEntityName(String entityName);





}
