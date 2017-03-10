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

package org.yes.cart.bulkimport.model;

import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ValueAdapter;

import java.util.List;

/**
 * Single import column description.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface ImportColumn extends ImpExColumn {

    /**
     * Get the field value. Regular expression will be used for obtain value if reg exp is set.
     *
     * @param rawValue the whole value from cell
     * @param adapter value adapter
     * @param tuple tuple
     *
     * @return value
     */
    Object getValue(String rawValue, ValueAdapter adapter, ImportTuple tuple);


    /**
     * Get the field value as string array via reg exp.
     *
     * @param rawValue the whole value from cell
     * @param adapter value adapter
     * @param tuple tuple
     *
     * @return field value as string array
     */
    List getValues(String rawValue, ValueAdapter adapter, ImportTuple tuple);

    /**
     * Column index from csv file
     *
     * @return column index
     */
    int getColumnIndex();


    /**
     * Boolean flag to only mutate data for inserts (updates ignore this data).
     *
     * @return true if inserts only.
     */
    boolean isInsertOnly();

    /**
     * Set insert only flag.
     *
     * @param insertOnly inserts only flag.
     */
    void setInsertOnly(boolean insertOnly);


    /**
     * Boolean flag to only mutate data for update (inserts ignore this data).
     *
     * @return true if update only.
     */
    boolean isUpdateOnly();

    /**
     * Set update only flag.
     *
     * @param updateOnly update only flag.
     */
    void setUpdateOnly(boolean updateOnly);


    /**
     * Boolean flag to indicate if update should be skipped if FK entity is not resolved.
     *
     * @return true if skip unresolved FK's.
     */
    boolean isSkipUpdateForUnresolved();

    /**
     * Set skip unresolved FK flag.
     *
     * @param skipUpdateForUnresolved flag.
     */
    void setSkipUpdateForUnresolved(boolean skipUpdateForUnresolved);



    /**
     * Get included import descriptor for complex fields.
     *
     * @return {@link ImportDescriptor}
     */
    ImportDescriptor getDescriptor();

}
