/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.migrationtools.yc859;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

/**
 * User: denispavlov
 * Date: 24/05/2019
 * Time: 07:18
 */
public class RemoveCategoryTreeById {


    private void remove(String uri, String user, String pass, String[] catId, boolean category) throws Exception {
        Connection conn = DriverManager.getConnection(uri, user, pass);
        conn.setAutoCommit(false);

        String table1 = category ? "TCATEGORY" : "TCONTENT";
        String table2 = category ? "TCATEGORYATTRVALUE" : "TCONTENTATTRVALUE";
        String colId = category ? "CATEGORY_ID" : "CONTENT_ID";

        if (catId != null && catId.length > 0) {
            if ("-1".equals(catId[0])) {
                System.out.println("Removing orphans entries");
                this.removeOrphanData(conn, table1, table2, colId);
            } else {
                for (final String id : catId) {
                    System.out.println("Removing tree, starting with " + id);
                    this.removeData(conn, table1, table2, colId, id);
                }
            }
        }
        conn.close();
    }

    private void removeOrphanData(Connection conn, String table1, String table2, String colId) throws Exception {

        Statement sta = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = sta.executeQuery("SELECT * FROM " + table1);

        while(rs.next()) {
            Object pk = rs.getObject(colId);
            Object parentPk = rs.getObject("PARENT_ID");
            if (!"0".equals(parentPk.toString())) {
                Statement sta2 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs2 = sta2.executeQuery("SELECT * FROM " + table1 + " WHERE " + colId + " = " + parentPk);
                if (!rs2.next()) {
                    this.removeData(conn, table1, table2, colId, pk.toString());
                }
                sta2.close();
            }
        }

        sta.close();
        conn.commit();

    }

    private void removeData(Connection conn, String table1, String table2, String colId, String catId) throws Exception {
        Statement sta = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = sta.executeQuery("SELECT * FROM " + table1 + " WHERE PARENT_ID = " + catId);

        while(rs.next()) {
            Object pk = rs.getObject(colId);
            this.removeData(conn, table1, table2, colId, pk.toString());
        }

        System.out.println("Removing catId " + catId + " from " + table1);
        System.out.println("DELETE FROM " + table2 + " WHERE " + colId + " = " + catId);
        Statement sta2 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        sta2.executeUpdate("DELETE FROM " + table2 + " WHERE " + colId + " = " + catId);
        sta2.close();
        System.out.println("DELETE FROM " + table1 + " WHERE " + colId + " = " + catId);
        Statement sta3 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        sta3.executeUpdate("DELETE FROM " + table1 + " WHERE " + colId + " = " + catId);
        sta3.close();


        sta.close();
        conn.commit();
    }

    public static void main(String... args) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("Specify DB connection properties \n\nDerby e.g.: jdbc:derby://localhost:1527/yes;\napp/app\n\nMySQL e.g.: jdbc:mysql://yesmysqlhost/yes?AutoReconnect=true&relaxAutoCommit=true&useUnicode=true&characterEncoding=utf-8\nuser/pass\n\nNOTE: add JDBC driver to -classpath\n\nNOTE: for MySQL ensure that relaxAutoCommit=true if you get error\n\ne.g.: java -Dfile.encoding=UTF-8 -classpath .:/path/to/mysql-connector-java-5.1.43.jar org.yes.cart.migrationtools.yc835.AdjustOrderInAVDisplayValue\n\n");
        System.out.print("Specify DB connection string:");
        String dbConn = scan.nextLine();
        System.out.print("Specify DB connection user  :");
        String dbUser = scan.nextLine();
        System.out.print("Specify DB connection pass  :");
        String dbPass = scan.nextLine();
        System.out.print("Is category (Y), content (N) :");
        String category = scan.nextLine();
        System.out.print("Specify CSV category IDs (or '-1' for orphan clean up) :");
        String catId = scan.nextLine();
        (new RemoveCategoryTreeById()).remove(dbConn, dbUser, dbPass, catId.split(","), "Y".equalsIgnoreCase(category));
    }

}
