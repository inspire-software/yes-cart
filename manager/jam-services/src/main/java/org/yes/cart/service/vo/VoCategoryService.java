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

}
