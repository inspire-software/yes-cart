package org.yes.cart.util;


/**
 *
 * Hold current shop code context.
 *
 */
public class ShopCodeContext {



    private static ThreadLocal<String> shopCode = new ThreadLocal<String>();

    /**
     * Get curent shop code.
     * @return current shop code.
     */
    public static String getShopCode() {
        if (shopCode.get() == null) {
            shopCode.set("DEFAULT");
        }
        return shopCode.get();
    }

    /**
     * Set shop code.
     * @param currentShopCode shop code to set.
     */
    public static void setShopCode(final String currentShopCode) {
        shopCode.set(currentShopCode);
    }

}
