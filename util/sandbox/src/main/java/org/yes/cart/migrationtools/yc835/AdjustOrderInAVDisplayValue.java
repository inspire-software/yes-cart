package org.yes.cart.migrationtools.yc835;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * User: denispavlov
 * Date: 04/11/2017
 * Time: 11:31
 */
public class AdjustOrderInAVDisplayValue {


    private void adjust(final String uri, final String user, final String pass) throws Exception {


        Connection conn = DriverManager.getConnection(uri, user, pass);
        conn.setAutoCommit(false);

        adjustTable(conn, "TPRODUCTATTRVALUE");
        adjustTable(conn, "TPRODUCTSKUATTRVALUE");

        conn.close();


    }

    private void adjustTable(final Connection conn, final String table) throws Exception {

        System.out.println("Adjusting table " + table);

        Statement sta = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = sta.executeQuery("SELECT * FROM " + table + " WHERE DISPLAYVAL is not null");

        while (rs.next()) {

            final Object pk = rs.getObject("ATTRVALUE_ID");
            final String i18n = rs.getString("DISPLAYVAL");
            final String model = adjustValue(i18n);
            if (model != null && !model.equals(i18n)) {
                rs.updateObject("DISPLAYVAL", model);
                rs.updateRow();
                System.out.println("Adjusting object(" + pk + ") val: " + model);
            }
        }

        sta.close();
        conn.commit();

    }

    private String adjustValue(String raw) {

        if (raw != null && raw.length() > 0) {

            final String SEPARATOR = "#~#";
            final Map<String, String> values = new TreeMap<String, String>();

            final String[] valuePairs = raw.split(SEPARATOR);
            for (int i = 0; i < valuePairs.length - 1; i += 2) {
                final String key = valuePairs[i];
                final String value = valuePairs[i + 1];
                if (value != null && value.length() > 0) {
                    values.put(key, value);
                }
            }
            final StringBuilder out = new StringBuilder();
            for (final Map.Entry<String, String> entry : values.entrySet()) {
                if (entry.getValue() != null && entry.getValue().length() > 0) {
                    out.append(entry.getKey()).append(SEPARATOR).append(entry.getValue()).append(SEPARATOR);
                }
            }
            return out.toString();
        }
        return null;
    }



    public static void main(String ... args) throws Exception {

        Scanner scan = new Scanner(System.in);

        System.out.println("Specify DB connection properties \n\n" +
                "Derby e.g.: jdbc:derby://localhost:1527/yes;\napp/app\n\n" +
                "MySQL e.g.: jdbc:mysql://yesmysqlhost/yes?AutoReconnect=true&relaxAutoCommit=true&useUnicode=true&characterEncoding=utf-8\nuser/pass\n\n" +
                "NOTE: add JDBC driver to -classpath\n\n" +
                "NOTE: for MySQL ensure that relaxAutoCommit=true if you get error\n\n" +
                "e.g.: java -Dfile.encoding=UTF-8 -classpath .:/path/to/mysql-connector-java-5.1.43.jar org.yes.cart.migrationtools.yc835.AdjustOrderInAVDisplayValue\n\n");

        System.out.print("Specify DB connection string:");
        final String dbConn = scan.nextLine();
        System.out.print("Specify DB connection user  :");
        final String dbUser = scan.nextLine();
        System.out.print("Specify DB connection pass  :");
        final String dbPass = scan.nextLine();

        new AdjustOrderInAVDisplayValue().adjust(dbConn, dbUser, dbPass);
    }

}
