package org.yes.cart.bulkimport.model;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface ImportDescriptor {

    /**
     * Get full qualiffied entity interface. For example - org.yes.cart.domain.entity.Brand
     *
     * @return full qualiffied entity interface
     */
    String getEntityIntface();

    /**
     * Set full qualiffied entity interface.
     *
     * @param entityIntface entity interface
     */
    void setEntityIntface(String entityIntface);

    /**
     * Get the import file decription.
     *
     * @return {@link org.yes.cart.bulkimport.model.ImportFile}
     */
    ImportFile getImportFile();

    /**
     * Get the collection of import columns.
     *
     * @return collection of import columns
     */
    Collection<ImportColumn> getImportColumns();


    /**
     * Get the folder with stored file to import.
     *
     * @return configured import folder.
     */
    String getImportFolder();

    /**
     * Set the import folder.
     *
     * @param importFolder import folder to use.
     */
    void setImportFolder(String importFolder);

    /**
     * Get insert sql, which used instead of hibernate object save for
     * speed up bulk import.
     * @return        insert sql
     */
    String getInsertSql();

    /**
     * Set inser sql
     * @param insertSql insert sql
     */
    void setInsertSql(String insertSql);


}
