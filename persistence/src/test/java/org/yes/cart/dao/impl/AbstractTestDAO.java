package org.yes.cart.dao.impl;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public abstract class AbstractTestDAO extends DBTestCase {

     /**
      * Please, set working directory for test(s) to npa\trunk\db if you are run under IDE.  
      * {@inheritDoc}
      * */
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(
                new File("src/test/resources/initialdata.xml"),
                false,
                true);
    }

    

    /** {@inheritDoc} */
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    /** {@inheritDoc} */
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.NONE;
    }

    public AbstractTestDAO() {

        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:mem:testnpadb");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");


    }


    /**
     * A Spring application context that we'll create from a
     * test application context and use to create
     * our DAO object (and data source, session factory, etc.)
     */
    protected static ApplicationContext ctx = null;

    protected SessionFactory sessionFactory;
    
    protected Session session;

    @Before
    public void setUp() throws Exception {
        
        ctx = new ClassPathXmlApplicationContext( "testApplicationContext.xml" );

        sessionFactory = (SessionFactory) ctx.getBean("sessionFactory");

        session = sessionFactory.openSession();

        super.setUp();

    }

    @After
    public void tearDown() {
        try {
            super.tearDown();
            sessionFactory.close();
            session.close();
            ctx = null;
            session = null;
            sessionFactory = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Clean up all created entities
     */
    public abstract void cleanUp();

    protected synchronized int update(String expression) throws SQLException {
        Statement st = null;
        try {
            st = getConnection().getConnection().createStatement();
            return st.executeUpdate(expression);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (st!=null) {
                st.close();
            }
        }
        return 0;
    }

    protected synchronized String query(String expression) throws SQLException {
        Statement st = null;
        try {
            st = getConnection().getConnection().createStatement();
            ResultSet rs = st.executeQuery(expression);
            return dump(rs);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (st!=null) {
                st.close();
            }
        }
        return "";
    }

    protected void dumpDataBase(final String prefix, final String [] tables) throws Exception {
        QueryDataSet queryDataSet = new QueryDataSet(getConnection());
        for (String tableName : tables) {
            queryDataSet.addTable(tableName);
        }
        FlatXmlDataSet.write(queryDataSet,
                new FileOutputStream("target/test-classes/"+this.getClass().getName()+"_" + prefix +"_dataset.xml"));
    }

    protected static String dump(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();

        int colmax = meta.getColumnCount();
        Object o = null;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < colmax; i++) {
            sb.append(meta.getColumnName(i+1));
            sb.append(" ");            
        }
        sb.append("\n");


        for (; rs.next(); ) {
            for (int i = 0; i < colmax; i++) {
                o = rs.getObject(i + 1);
                if (o != null)
                    sb.append(o.toString());
                else
                    sb.append("null");

                if(i < colmax-1)
                    sb.append(" ");
            }

            if(!rs.isLast())
                sb.append("\n");
        }
        return sb.toString();
    }



}
