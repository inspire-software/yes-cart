package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;

import java.util.List;

/**
 * User: denispavlov
 * Date: 12/09/2016
 * Time: 08:22
 */
@Dto
public class VoContentWithBody extends VoContent {

    private List<VoContentBody> contentBodies;

    public List<VoContentBody> getContentBodies() {
        return contentBodies;
    }

    public void setContentBodies(final List<VoContentBody> contentBodies) {
        this.contentBodies = contentBodies;
    }
}
