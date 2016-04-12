package org.yes.cart.domain.vo;

import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * Created by Igor_Azarny on 4/12/2016.
 */
public class VoShopLanguages {

    private long shopId;

    private List<String> supported;

    private List<MutablePair<String, String>>  all;


    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public List<String> getSupported() {
        return supported;
    }

    public void setSupported(List<String> supported) {
        this.supported = supported;
    }

    public List<MutablePair<String, String>> getAll() {
        return all;
    }

    public void setAll(List<MutablePair<String, String>> all) {
        this.all = all;
    }
}
