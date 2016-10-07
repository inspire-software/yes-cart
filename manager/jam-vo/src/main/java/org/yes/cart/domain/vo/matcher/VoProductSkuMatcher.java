package org.yes.cart.domain.vo.matcher;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.vo.VoProductSku;

/**
 * User: denispavlov
 * Date: 27/09/2016
 * Time: 16:10
 */
public class VoProductSkuMatcher implements DtoToEntityMatcher<VoProductSku, ProductSkuDTO> {

    @Override
    public boolean match(final VoProductSku voProductSku, final ProductSkuDTO productSkuDTO) {
        return voProductSku != null && productSkuDTO != null && voProductSku.getSkuId() == productSkuDTO.getSkuId();
    }
}
