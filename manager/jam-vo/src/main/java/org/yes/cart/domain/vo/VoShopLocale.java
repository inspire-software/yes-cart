package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import java.util.Map;

/**
 * Created by Igor_Azarny on 3/28/2016.
 */
public class VoShopLocale {

    @DtoField(value = "shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "uri")
    private String uri;

    @DtoField(value = "title")
    private String title;

    @DtoField(value = "metakeywords")
    private String metakeywords;

    @DtoField(value = "metadescription")
    private String metadescription;

    @DtoField(value = "displayTitle")
    private Map<String, String> displayTitles;

    @DtoField(value = "displayMetakeywords")
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "displayMetadescription")
    private Map<String, String> displayMetadescriptions;


    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetakeywords() {
        return metakeywords;
    }

    public void setMetakeywords(String metakeywords) {
        this.metakeywords = metakeywords;
    }

    public String getMetadescription() {
        return metadescription;
    }

    public void setMetadescription(String metadescription) {
        this.metadescription = metadescription;
    }

    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }
}
