package org.yes.cart.report.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/1/12
 * Time: 11:59 AM
 */
public class ReportServiceImplTest  {

    final List<ReportDescriptor> allReportToTestCreation = new ArrayList<ReportDescriptor>();

    @Before
    public void setUp() {


        ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setHsqlQuery("select s from ShopEntity s");
        reportDescriptor.getLangXslfo().add(new ReportPair("en", "src/test/resources/xslfo/shop.xslfo"));
        reportDescriptor.setReportId("shopTestReport");

        allReportToTestCreation.add(reportDescriptor);

        reportDescriptor = new ReportDescriptor();
        reportDescriptor.setHsqlQuery("select  o.sku.code,  o.sku.name, o.sku.barCode, o.reserved, o.quantity  from SkuWarehouseEntity o\n" +
                "                                          where o.warehouse.code = ?1\n" +
                "                                          order by o.sku.code");
        reportDescriptor.getLangXslfo().add(new ReportPair("en", "src/test/resources/xslfo/wh-remains-goods.xslfo"));
        reportDescriptor.setReportId("warehouseRemainsGoods");

        allReportToTestCreation.add(reportDescriptor);
    }



    @Test
    public void testGetReportShop() throws Exception {



        ReportServiceImpl reportService = new ReportServiceImpl(null, allReportToTestCreation, null) {

            /**
             * {@inheritDoc}
             * @param query  hsql query
             * @param params parameters.
             * @return
             */
            List<Object> getQueryResult(final String query, final Object... params) {
                return Collections.EMPTY_LIST;

            }

            /**
             * Write into given file name xml rezult.
             *
             * @param rez list of objects.
             * @return tmp xml file name
             */
            String getXml(final List<Object> rez) {

                try {
                    String fileName = File.createTempFile("testyes", "cart").getAbsolutePath();
                    BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
                    out.write(getXmlDataString() );
                    out.close();
                    return fileName;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return null;



            }

        };

        reportId =  "shopTestReport";

        assertTrue(reportService.createReport("en", reportId, "shopTestReport.pdf"));

        reportId =  "warehouseRemainsGoods";

        assertTrue(reportService.createReport("en", reportId, "warehouseRemainsGoods.pdf"));

    }

    String reportId;

    String getXmlDataString() {

        if ("shopTestReport".equals(reportId))  {
            return shopData;
        } else if ("warehouseRemainsGoods".equals(reportId))  {
            return whRemainsGoodsData;
        }

        return null;

    }

    String whRemainsGoodsData ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<yes-report>\n" +
            "  <object-array>\n" +
            "    <string>LG656ET#UUG</string>\n" +
            "    <string>HP ProBook 6560b</string>\n" +
            "    <string>0112312334</string>\n" +
            "    <big-decimal>0.00</big-decimal>\n" +
            "    <big-decimal>4.00</big-decimal>\n" +
            "  </object-array>\n" +
            "  <object-array>\n" +
            "    <string>LH339EA#UUG</string>\n" +
            "    <string>HP ProBook 4730d</string>\n" +
            "    <string></string>\n" +
            "    <big-decimal>0.00</big-decimal>\n" +
            "    <big-decimal>5.00</big-decimal>\n" +
            "  </object-array>\n" +
            "  <object-array>\n" +
            "    <string>LG656ET</string>\n" +
            "    <string>HP ProBook 6560b</string>    \n" +
            "    <string>0123456789012</string>\n" +
            "    <big-decimal>5.00</big-decimal>\n" +
            "    <big-decimal>100.00</big-decimal>\n" +
            "  </object-array>\n" +
            "</yes-report>";


    String shopData =  "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><yes-report>\n" +
            "  <shop>\n" +
            "    <code>SHOIP1</code>\n" +
            "    <name>Gadget universe кирилица</name>\n" +
            "    <description>Gadget universe shop</description>\n" +
            "    <fspointer>gadget</fspointer>\n" +
            "    <imageVaultFolder>gadget/imagevault</imageVaultFolder>\n" +
            "    <shopUrl class=\"org.hibernate.collection.internal.PersistentSet\">\n" +
            "      <set>\n" +
            "        <shopurl>\n" +
            "          <url>gadget.npa.com</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>10</storeUrlId>\n" +
            "        </shopurl>\n" +
            "        <shopurl>\n" +
            "          <url>www.gadget.npa.com</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>11</storeUrlId>\n" +
            "        </shopurl>\n" +
            "      </set>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopUrl</role>\n" +
            "      <key class=\"long\">10</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"map\">\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "        </entry>\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl[2]\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl[2]\"/>\n" +
            "        </entry>\n" +
            "      </storedSnapshot>\n" +
            "    </shopUrl>\n" +
            "    <exchangerates class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag>\n" +
            "        <exchangerates>\n" +
            "          <fromCurrency>EUR</fromCurrency>\n" +
            "          <toCurrency>UAH</toCurrency>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <rate>11.38</rate>\n" +
            "          <shopexchangerateId>1</shopexchangerateId>\n" +
            "        </exchangerates>\n" +
            "        <exchangerates>\n" +
            "          <fromCurrency>EUR</fromCurrency>\n" +
            "          <toCurrency>RUB</toCurrency>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <rate>42.56</rate>\n" +
            "          <shopexchangerateId>2</shopexchangerateId>\n" +
            "        </exchangerates>\n" +
            "      </bag>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.exchangerates</role>\n" +
            "      <key class=\"long\">10</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\">\n" +
            "        <exchangerates reference=\"../../bag/exchangerates\"/>\n" +
            "        <exchangerates reference=\"../../bag/exchangerates[2]\"/>\n" +
            "      </storedSnapshot>\n" +
            "    </exchangerates>\n" +
            "    <advertisingPlaces class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.advertisingPlaces</role>\n" +
            "      <key class=\"long\">10</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </advertisingPlaces>\n" +
            "    <shopDiscountRules class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopDiscountRules</role>\n" +
            "      <key class=\"long\">10</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopDiscountRules>\n" +
            "    <attribute class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag>\n" +
            "        <org.yes.cart.domain.entity.impl.AttrValueEntityShop>\n" +
            "          <val>EUR,UAH,RUB</val>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <attribute class=\"org.yes.cart.domain.entity.impl.AttributeEntity\">\n" +
            "            <mandatory>false</mandatory>\n" +
            "            <allowduplicate>false</allowduplicate>\n" +
            "            <allowfailover>false</allowfailover>\n" +
            "            <rank>500</rank>\n" +
            "            <name>Currency</name>\n" +
            "            <etype class=\"org.hibernate.proxy.HibernateProxy_$$_javassist_67\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "              <persistentClass>org.yes.cart.domain.entity.Etype</persistentClass>\n" +
            "              <interfaces>\n" +
            "                <java-class>org.hibernate.proxy.HibernateProxy</java-class>\n" +
            "                <java-class>org.yes.cart.domain.entity.Etype</java-class>\n" +
            "              </interfaces>\n" +
            "              <getIdentifierMethodClass>org.yes.cart.domain.entity.Etype</getIdentifierMethodClass>\n" +
            "              <setIdentifierMethodClass>org.yes.cart.domain.entity.Etype</setIdentifierMethodClass>\n" +
            "              <getIdentifierMethodName>getEtypeId</getIdentifierMethodName>\n" +
            "              <setIdentifierMethodName>setEtypeId</setIdentifierMethodName>\n" +
            "              <setIdentifierMethodParams>\n" +
            "                <java-class>long</java-class>\n" +
            "              </setIdentifierMethodParams>\n" +
            "              <entityName>org.yes.cart.domain.entity.Etype</entityName>\n" +
            "              <id class=\"long\">1004</id>\n" +
            "            </etype>\n" +
            "            <attributeGroup class=\"org.yes.cart.domain.entity.AttributeGroup_$$_javassist_22\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "              <persistentClass>org.yes.cart.domain.entity.AttributeGroup</persistentClass>\n" +
            "              <interfaces>\n" +
            "                <java-class>org.yes.cart.domain.entity.AttributeGroup</java-class>\n" +
            "                <java-class>org.hibernate.proxy.HibernateProxy</java-class>\n" +
            "              </interfaces>\n" +
            "              <getIdentifierMethodClass>org.yes.cart.domain.entity.AttributeGroup</getIdentifierMethodClass>\n" +
            "              <setIdentifierMethodClass>org.yes.cart.domain.entity.AttributeGroup</setIdentifierMethodClass>\n" +
            "              <getIdentifierMethodName>getAttributegroupId</getIdentifierMethodName>\n" +
            "              <setIdentifierMethodName>setAttributegroupId</setIdentifierMethodName>\n" +
            "              <setIdentifierMethodParams>\n" +
            "                <java-class>long</java-class>\n" +
            "              </setIdentifierMethodParams>\n" +
            "              <entityName>org.yes.cart.domain.entity.AttributeGroup</entityName>\n" +
            "              <id class=\"long\">1001</id>\n" +
            "            </attributeGroup>\n" +
            "            <attributeId>1006</attributeId>\n" +
            "            <code>CURRENCY</code>\n" +
            "          </attribute>\n" +
            "          <attrvalueId>10</attrvalueId>\n" +
            "        </org.yes.cart.domain.entity.impl.AttrValueEntityShop>\n" +
            "        <org.yes.cart.domain.entity.impl.AttrValueEntityShop>\n" +
            "          <val>noreply@gadget.com</val>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <attribute class=\"org.yes.cart.domain.entity.impl.AttributeEntity\">\n" +
            "            <mandatory>true</mandatory>\n" +
            "            <allowduplicate>false</allowduplicate>\n" +
            "            <allowfailover>false</allowfailover>\n" +
            "            <rank>500</rank>\n" +
            "            <name>Shop mail from</name>\n" +
            "            <etype class=\"org.hibernate.proxy.HibernateProxy_$$_javassist_67\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "              <persistentClass>org.yes.cart.domain.entity.Etype</persistentClass>\n" +
            "              <interfaces reference=\"../../../../org.yes.cart.domain.entity.impl.AttrValueEntityShop/attribute/etype/interfaces\"/>\n" +
            "              <getIdentifierMethodClass>org.yes.cart.domain.entity.Etype</getIdentifierMethodClass>\n" +
            "              <setIdentifierMethodClass>org.yes.cart.domain.entity.Etype</setIdentifierMethodClass>\n" +
            "              <getIdentifierMethodName>getEtypeId</getIdentifierMethodName>\n" +
            "              <setIdentifierMethodName>setEtypeId</setIdentifierMethodName>\n" +
            "              <setIdentifierMethodParams>\n" +
            "                <java-class>long</java-class>\n" +
            "              </setIdentifierMethodParams>\n" +
            "              <entityName>org.yes.cart.domain.entity.Etype</entityName>\n" +
            "              <id class=\"long\">1000</id>\n" +
            "            </etype>\n" +
            "            <attributeGroup class=\"org.yes.cart.domain.entity.AttributeGroup_$$_javassist_22\" reference=\"../../../org.yes.cart.domain.entity.impl.AttrValueEntityShop/attribute/attributeGroup\"/>\n" +
            "            <attributeId>1027</attributeId>\n" +
            "            <code>SHOP_MAIL_FROM</code>\n" +
            "          </attribute>\n" +
            "          <attrvalueId>11</attrvalueId>\n" +
            "        </org.yes.cart.domain.entity.impl.AttrValueEntityShop>\n" +
            "      </bag>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.attribute</role>\n" +
            "      <key class=\"long\">10</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\">\n" +
            "        <org.yes.cart.domain.entity.impl.AttrValueEntityShop reference=\"../../bag/org.yes.cart.domain.entity.impl.AttrValueEntityShop\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.AttrValueEntityShop reference=\"../../bag/org.yes.cart.domain.entity.impl.AttrValueEntityShop[2]\"/>\n" +
            "      </storedSnapshot>\n" +
            "    </attribute>\n" +
            "    <shopCategory class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>5</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces>\n" +
            "              <java-class>org.yes.cart.domain.entity.Category</java-class>\n" +
            "              <java-class>org.hibernate.proxy.HibernateProxy</java-class>\n" +
            "            </interfaces>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">140</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>107</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>5</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">141</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>108</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>5</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">142</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>109</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>5</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">211</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>211</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>10</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">101</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>100</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>20</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">106</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>101</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>30</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">113</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>102</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>40</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">120</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>103</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>50</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">129</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>104</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>60</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">133</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>105</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>70</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces reference=\"../../../org.yes.cart.domain.entity.impl.ShopCategoryEntity/category/interfaces\"/>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">134</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>106</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "      </bag>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopCategory</role>\n" +
            "      <key class=\"long\">10</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\">\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[2]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[3]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[4]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[5]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[6]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[7]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[8]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[9]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[10]\"/>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity[11]\"/>\n" +
            "      </storedSnapshot>\n" +
            "    </shopCategory>\n" +
            "    <shopId>10</shopId>\n" +
            "  </shop>\n" +
            "  <shop>\n" +
            "    <code>SHOIP2</code>\n" +
            "    <name>Mobile universe</name>\n" +
            "    <description>Mobile universe shop</description>\n" +
            "    <fspointer>mobile</fspointer>\n" +
            "    <imageVaultFolder>mobile/imagevault</imageVaultFolder>\n" +
            "    <shopUrl class=\"org.hibernate.collection.internal.PersistentSet\">\n" +
            "      <set>\n" +
            "        <shopurl>\n" +
            "          <url>www.mobile.npa.com</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>21</storeUrlId>\n" +
            "        </shopurl>\n" +
            "        <shopurl>\n" +
            "          <url>mobile.npa.com</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>20</storeUrlId>\n" +
            "        </shopurl>\n" +
            "      </set>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopUrl</role>\n" +
            "      <key class=\"long\">20</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"map\">\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "        </entry>\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl[2]\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl[2]\"/>\n" +
            "        </entry>\n" +
            "      </storedSnapshot>\n" +
            "    </shopUrl>\n" +
            "    <exchangerates class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.exchangerates</role>\n" +
            "      <key class=\"long\">20</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </exchangerates>\n" +
            "    <advertisingPlaces class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.advertisingPlaces</role>\n" +
            "      <key class=\"long\">20</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </advertisingPlaces>\n" +
            "    <shopDiscountRules class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopDiscountRules</role>\n" +
            "      <key class=\"long\">20</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopDiscountRules>\n" +
            "    <attribute class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.attribute</role>\n" +
            "      <key class=\"long\">20</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </attribute>\n" +
            "    <shopCategory class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopCategory</role>\n" +
            "      <key class=\"long\">20</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopCategory>\n" +
            "    <shopId>20</shopId>\n" +
            "  </shop>\n" +
            "  <shop>\n" +
            "    <code>SHOIP3</code>\n" +
            "    <name>Game universe</name>\n" +
            "    <description>Game universe shop</description>\n" +
            "    <fspointer>game</fspointer>\n" +
            "    <imageVaultFolder>game/imagevault</imageVaultFolder>\n" +
            "    <shopUrl class=\"org.hibernate.collection.internal.PersistentSet\">\n" +
            "      <set>\n" +
            "        <shopurl>\n" +
            "          <url>www.game.npa.com</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>31</storeUrlId>\n" +
            "        </shopurl>\n" +
            "        <shopurl>\n" +
            "          <url>game.npa.com</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>30</storeUrlId>\n" +
            "        </shopurl>\n" +
            "      </set>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopUrl</role>\n" +
            "      <key class=\"long\">30</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"map\">\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "        </entry>\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl[2]\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl[2]\"/>\n" +
            "        </entry>\n" +
            "      </storedSnapshot>\n" +
            "    </shopUrl>\n" +
            "    <exchangerates class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.exchangerates</role>\n" +
            "      <key class=\"long\">30</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </exchangerates>\n" +
            "    <advertisingPlaces class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.advertisingPlaces</role>\n" +
            "      <key class=\"long\">30</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </advertisingPlaces>\n" +
            "    <shopDiscountRules class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopDiscountRules</role>\n" +
            "      <key class=\"long\">30</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopDiscountRules>\n" +
            "    <attribute class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.attribute</role>\n" +
            "      <key class=\"long\">30</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </attribute>\n" +
            "    <shopCategory class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopCategory</role>\n" +
            "      <key class=\"long\">30</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopCategory>\n" +
            "    <shopId>30</shopId>\n" +
            "  </shop>\n" +
            "  <shop>\n" +
            "    <code>SHOIP4</code>\n" +
            "    <name>Shop without assigned categories</name>\n" +
            "    <description>Shop without assigned categories used in test</description>\n" +
            "    <fspointer>/dev/none</fspointer>\n" +
            "    <imageVaultFolder>none/imagevault</imageVaultFolder>\n" +
            "    <shopUrl class=\"org.hibernate.collection.internal.PersistentSet\">\n" +
            "      <set>\n" +
            "        <shopurl>\n" +
            "          <url>eddie.lives.somewhere.in.time</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>32</storeUrlId>\n" +
            "        </shopurl>\n" +
            "      </set>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopUrl</role>\n" +
            "      <key class=\"long\">40</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"map\">\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "        </entry>\n" +
            "      </storedSnapshot>\n" +
            "    </shopUrl>\n" +
            "    <exchangerates class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.exchangerates</role>\n" +
            "      <key class=\"long\">40</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </exchangerates>\n" +
            "    <advertisingPlaces class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.advertisingPlaces</role>\n" +
            "      <key class=\"long\">40</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </advertisingPlaces>\n" +
            "    <shopDiscountRules class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopDiscountRules</role>\n" +
            "      <key class=\"long\">40</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopDiscountRules>\n" +
            "    <attribute class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.attribute</role>\n" +
            "      <key class=\"long\">40</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </attribute>\n" +
            "    <shopCategory class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopCategory</role>\n" +
            "      <key class=\"long\">40</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopCategory>\n" +
            "    <shopId>40</shopId>\n" +
            "  </shop>\n" +
            "  <shop>\n" +
            "    <code>SHOIP5</code>\n" +
            "    <name>Shop with limited assigned categories</name>\n" +
            "    <description>Shop with limited assigned categories used in test</description>\n" +
            "    <fspointer>/dev/none</fspointer>\n" +
            "    <imageVaultFolder>none/imagevault</imageVaultFolder>\n" +
            "    <shopUrl class=\"org.hibernate.collection.internal.PersistentSet\">\n" +
            "      <set>\n" +
            "        <shopurl>\n" +
            "          <url>long.live.robots</url>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <storeUrlId>33</storeUrlId>\n" +
            "        </shopurl>\n" +
            "      </set>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopUrl</role>\n" +
            "      <key class=\"long\">50</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"map\">\n" +
            "        <entry>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "          <shopurl reference=\"../../../set/shopurl\"/>\n" +
            "        </entry>\n" +
            "      </storedSnapshot>\n" +
            "    </shopUrl>\n" +
            "    <exchangerates class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.exchangerates</role>\n" +
            "      <key class=\"long\">50</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </exchangerates>\n" +
            "    <advertisingPlaces class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.advertisingPlaces</role>\n" +
            "      <key class=\"long\">50</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </advertisingPlaces>\n" +
            "    <shopDiscountRules class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopDiscountRules</role>\n" +
            "      <key class=\"long\">50</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopDiscountRules>\n" +
            "    <attribute class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.attribute</role>\n" +
            "      <key class=\"long\">50</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </attribute>\n" +
            "    <shopCategory class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag>\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "          <rank>5</rank>\n" +
            "          <shop class=\"shop\" reference=\"../../../..\"/>\n" +
            "          <category class=\"org.yes.cart.domain.entity.Category_$$_javassist_46\" resolves-to=\"org.hibernate.proxy.pojo.javassist.SerializableProxy\">\n" +
            "            <persistentClass>org.yes.cart.domain.entity.Category</persistentClass>\n" +
            "            <interfaces>\n" +
            "              <java-class>org.yes.cart.domain.entity.Category</java-class>\n" +
            "              <java-class>org.hibernate.proxy.HibernateProxy</java-class>\n" +
            "            </interfaces>\n" +
            "            <getIdentifierMethodClass>org.yes.cart.domain.entity.Category</getIdentifierMethodClass>\n" +
            "            <setIdentifierMethodClass>org.yes.cart.domain.entity.Category</setIdentifierMethodClass>\n" +
            "            <getIdentifierMethodName>getCategoryId</getIdentifierMethodName>\n" +
            "            <setIdentifierMethodName>setCategoryId</setIdentifierMethodName>\n" +
            "            <setIdentifierMethodParams>\n" +
            "              <java-class>long</java-class>\n" +
            "            </setIdentifierMethodParams>\n" +
            "            <entityName>org.yes.cart.domain.entity.Category</entityName>\n" +
            "            <id class=\"long\">200</id>\n" +
            "          </category>\n" +
            "          <shopCategoryId>150</shopCategoryId>\n" +
            "        </org.yes.cart.domain.entity.impl.ShopCategoryEntity>\n" +
            "      </bag>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopCategory</role>\n" +
            "      <key class=\"long\">50</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\">\n" +
            "        <org.yes.cart.domain.entity.impl.ShopCategoryEntity reference=\"../../bag/org.yes.cart.domain.entity.impl.ShopCategoryEntity\"/>\n" +
            "      </storedSnapshot>\n" +
            "    </shopCategory>\n" +
            "    <shopId>50</shopId>\n" +
            "  </shop>\n" +
            "  <shop>\n" +
            "    <code>JEWEL_SHOP</code>\n" +
            "    <name>To pass bulk import test</name>\n" +
            "    <description>Shop with limited assigned categories used in test</description>\n" +
            "    <fspointer>/dev/none</fspointer>\n" +
            "    <imageVaultFolder>none/imagevault</imageVaultFolder>\n" +
            "    <shopUrl class=\"org.hibernate.collection.internal.PersistentSet\">\n" +
            "      <set/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopUrl</role>\n" +
            "      <key class=\"long\">60</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"map\"/>\n" +
            "    </shopUrl>\n" +
            "    <exchangerates class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.exchangerates</role>\n" +
            "      <key class=\"long\">60</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </exchangerates>\n" +
            "    <advertisingPlaces class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.advertisingPlaces</role>\n" +
            "      <key class=\"long\">60</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </advertisingPlaces>\n" +
            "    <shopDiscountRules class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopDiscountRules</role>\n" +
            "      <key class=\"long\">60</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopDiscountRules>\n" +
            "    <attribute class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.attribute</role>\n" +
            "      <key class=\"long\">60</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </attribute>\n" +
            "    <shopCategory class=\"org.hibernate.collection.internal.PersistentBag\">\n" +
            "      <bag/>\n" +
            "      <initialized>true</initialized>\n" +
            "      <owner class=\"shop\" reference=\"../..\"/>\n" +
            "      <cachedSize>-1</cachedSize>\n" +
            "      <role>org.yes.cart.domain.entity.impl.ShopEntity.shopCategory</role>\n" +
            "      <key class=\"long\">60</key>\n" +
            "      <dirty>false</dirty>\n" +
            "      <storedSnapshot class=\"list\"/>\n" +
            "    </shopCategory>\n" +
            "    <shopId>60</shopId>\n" +
            "  </shop>\n" +
            "</yes-report>";


}
