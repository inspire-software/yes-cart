/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

/**
 * Date: 13/06/2020
 * Time: 14:54
 */
public class VoShopSummaryEmailTemplate {

    private String name;
    private boolean disabled;
    private String cmsNameHTML;
    private String cmsNameTXT;
    private String cmsNameImage;
    private String cmsNameProp;
    private boolean yce;
    private boolean part;
    private boolean image;
    private boolean cmsHTML;
    private boolean cmsTXT;
    private boolean cmsImage;
    private boolean cmsProp;
    private String from;
    private String to;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public String getCmsNameHTML() {
        return cmsNameHTML;
    }

    public void setCmsNameHTML(final String cmsNameHTML) {
        this.cmsNameHTML = cmsNameHTML;
    }

    public String getCmsNameTXT() {
        return cmsNameTXT;
    }

    public void setCmsNameTXT(final String cmsNameTXT) {
        this.cmsNameTXT = cmsNameTXT;
    }

    public String getCmsNameImage() {
        return cmsNameImage;
    }

    public void setCmsNameImage(final String cmsNameImage) {
        this.cmsNameImage = cmsNameImage;
    }

    public String getCmsNameProp() {
        return cmsNameProp;
    }

    public void setCmsNameProp(final String cmsNameProp) {
        this.cmsNameProp = cmsNameProp;
    }

    public boolean isYce() {
        return yce;
    }

    public void setYce(final boolean yce) {
        this.yce = yce;
    }

    public boolean isPart() {
        return part;
    }

    public void setPart(final boolean part) {
        this.part = part;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(final boolean image) {
        this.image = image;
    }

    public boolean isCmsHTML() {
        return cmsHTML;
    }

    public void setCmsHTML(final boolean cmsHTML) {
        this.cmsHTML = cmsHTML;
    }

    public boolean isCmsTXT() {
        return cmsTXT;
    }

    public void setCmsTXT(final boolean cmsTXT) {
        this.cmsTXT = cmsTXT;
    }

    public boolean isCmsImage() {
        return cmsImage;
    }

    public void setCmsImage(final boolean cmsImage) {
        this.cmsImage = cmsImage;
    }

    public boolean isCmsProp() {
        return cmsProp;
    }

    public void setCmsProp(final boolean cmsProp) {
        this.cmsProp = cmsProp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(final String to) {
        this.to = to;
    }
}
