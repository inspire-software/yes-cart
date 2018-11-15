
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for product-categoriesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="product-categoriesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="product-category" type="{}product-categoryType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="import-mode" type="{}collectionImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "product-categoriesType", propOrder = {
    "productCategory"
})
public class ProductCategoriesType {

    @XmlElement(name = "product-category")
    protected List<ProductCategoryType> productCategory;
    @XmlAttribute(name = "import-mode")
    protected CollectionImportModeType importMode;

    /**
     * Gets the value of the productCategory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productCategory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductCategoryType }
     * 
     * 
     */
    public List<ProductCategoryType> getProductCategory() {
        if (productCategory == null) {
            productCategory = new ArrayList<ProductCategoryType>();
        }
        return this.productCategory;
    }

    /**
     * Gets the value of the importMode property.
     * 
     * @return
     *     possible object is
     *     {@link CollectionImportModeType }
     *     
     */
    public CollectionImportModeType getImportMode() {
        return importMode;
    }

    /**
     * Sets the value of the importMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CollectionImportModeType }
     *     
     */
    public void setImportMode(CollectionImportModeType value) {
        this.importMode = value;
    }

}
