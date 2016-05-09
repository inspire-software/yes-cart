package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoCategory;

import java.util.List;

/**
 * Created by Igor_Azarny on 3- may -2016.
 */
public interface VoShopCategoryService {

    /**
     * Get all assigned to shop categories.
     *
     * @param shopId shop id
     * @return list of assigned categories
     */
    List<VoCategory> getAllByShopId(final long shopId) throws Exception;

    /**
     * Update categories assigned to shop.
     *
     * @param shopId shop id
     * @return list of assigned categories
     */
    List<VoCategory> update(final long shopId, List<VoCategory> voCategories) throws Exception;


}
