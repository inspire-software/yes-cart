package org.yes.cart.service.vo.impl;

import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.vo.VoCategoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public class VoCategoryServiceImpl implements VoCategoryService {

    private final DtoCategoryService dtoCategoryService;
    private final Assembler simpleVoCategoryAssembler;

    /**
     * Construct service.
     * @param dtoCategoryService dto service to use.
     */
    public VoCategoryServiceImpl(final DtoCategoryService dtoCategoryService) {
        this.dtoCategoryService = dtoCategoryService;
        this.simpleVoCategoryAssembler = DTOAssembler.newAssembler(VoCategory.class, CategoryDTO.class);
    }

    /** {@inheritDoc} */
    public List<VoCategory> getAll() throws Exception {
        final List<CategoryDTO> categoryDTOs = dtoCategoryService.getAll();
        final List<VoCategory> voCategories = new ArrayList<>(categoryDTOs.size());
        simpleVoCategoryAssembler.assembleDtos(voCategories, categoryDTOs, null ,null);
        return voCategories;
    }
}
