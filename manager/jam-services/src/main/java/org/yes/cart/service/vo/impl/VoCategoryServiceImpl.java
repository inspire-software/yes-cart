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
        adaptCategories(categoryDTOs, voCategories);
        return voCategories;
    }

    /**
     * Adapt dto to vo recursively.
     * @param categoryDTOs list of dto
     * @param voCategories list of vo
     */
    private void adaptCategories(List<CategoryDTO> categoryDTOs, List<VoCategory> voCategories) {
        for(CategoryDTO dto : categoryDTOs) {
            VoCategory voCategory = new VoCategory();
            simpleVoCategoryAssembler.assembleDto(voCategory, dto, null, null);
            voCategories.add(voCategory);
            voCategory.setChildren(new ArrayList<VoCategory>(dto.getChildren().size()));
            adaptCategories(dto.getChildren(), voCategory.getChildren());
        }
    }


    /** {@inheritDoc} */
    public VoCategory getById(long id) throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getById(id);
        final VoCategory voCategory = new VoCategory();
        simpleVoCategoryAssembler.assembleDto(voCategory, categoryDTO, null ,null);
        return voCategory;
    }


    /** {@inheritDoc} */
    public VoCategory create(VoCategory voCategory)  throws Exception {
        final CategoryDTO categoryDTO = dtoCategoryService.getNew();
        simpleVoCategoryAssembler.assembleEntity(voCategory, categoryDTO, null, null);
        final CategoryDTO persistent = dtoCategoryService.create(categoryDTO);
        return getById(persistent.getId());
    }
}
