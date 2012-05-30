package org.yes.cart.icecat.transform

/**
 * Context Groovy class.
 */
class Context  {
    
    String dataDirectory = "/dev/yes-cart/icecatdata";

    String url = "http://data.icecat.biz/";

    String login;

    String pwd;

    int langId = 8;

    String productDir = "RU";

    String categories = "151,1296,942,803,788,195,194,197,943,196,191,192";

    //long mindata =  20120101000000L;

    long mindata =  20110101000000L;

    int limit = 1000;



    @Override
    public String toString() {
        return "Context{" +
                "dataDirectory='" + dataDirectory + '\'' +
                ", url='" + url + '\'' +
                ", login='" + login + '\'' +
                ", pwd='" + pwd + '\'' +
                ", langId=" + langId +
                ", categories='" + categories + '\'' +
                ", mindata=" + mindata +
                ", limit=" + limit +
                '}';
    }


}
