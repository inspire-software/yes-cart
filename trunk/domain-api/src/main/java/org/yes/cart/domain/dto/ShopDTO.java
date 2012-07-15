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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Shop DTO interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopDTO extends Identifiable {

    /**
     * Get pk value.
     *
     * @return pk value
     */
    long getShopId();

    /**
     * Set pk value.
     *
     * @param shopId pk value
     */
    void setShopId(long shopId);

    /**
     * Get shop code.
     *
     * @return shop code.
     */
    String getCode();

    /**
     * Set shop code.
     *
     * @param code shop code.
     */
    void setCode(String code);

    /**
     * Get name.
     *
     * @return shop name.
     */
    String getName();

    /**
     * Set name.
     *
     * @param name shop name.
     */
    void setName(String name);

    /**
     * Get shop descrition.
     *
     * @return description.
     */
    String getDescription();

    /**
     * Set descrotion.
     *
     * @param description shop description.
     */
    void setDescription(String description);

    /**
     * Get server side pointer to file system where shop templates are stored.
     *
     * @return file system pointer.
     */
    String getFspointer();

    /**
     * Set server side pointer to file system where shop templates are stored.
     *
     * @param fspointer file system pointer.
     */
    void setFspointer(String fspointer);

    /**
     * Get image vault folder.
     * @return image vault folder.
     */
    String getImageVaultFolder();

    /**
     * Set image vaule folder.
     * @param imageVaultFolder  image vault  folder.
     */
    void setImageVaultFolder(String imageVaultFolder);

}
