package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoCategory;

import java.util.List;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public interface VoCategoryService {

    /**
     * Get all categories in the system, filtered according to rights
     * @return
     * @throws Exception
     */
    List<VoCategory> getAll() throws Exception;

    /**
     * Get category by id.
     *
     * @param id
     * @return category vo
     * @throwsException
     */
    VoCategory getById(long id) throws Exception;

    /**
     * Create new category..
     * @param voCategory category
     * @return persistent version
     * @throws Exception
     */
    VoCategory create(VoCategory voCategory)  throws Exception;

}
