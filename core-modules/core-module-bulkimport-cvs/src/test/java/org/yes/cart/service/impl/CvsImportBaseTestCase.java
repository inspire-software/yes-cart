package org.yes.cart.service.impl;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 1:59 PM
 */
public class CvsImportBaseTestCase extends DBTestCase {

    /**
     * A Spring application context that we'll create from a
     * test application context and use to create
     * our DAO object (and data source, session factory, etc.)
     */
    protected static ApplicationContext ctx = null;

    protected SessionFactory sessionFactory;

    protected Session session;

    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(
                new File("../../persistence/src/test/resources/initialdata.xml"),
                false,
                true
        );
    }

    protected void dumpDataBase(final String prefix, final String[] tables) throws Exception {
        QueryDataSet queryDataSet = new QueryDataSet(getConnection());
        for (String tableName : tables) {
            queryDataSet.addTable(tableName);
        }
        FlatXmlDataSet.write(queryDataSet,
                new FileOutputStream("target/test-classes/" + this.getClass().getName() + "_" + prefix + "_dataset.xml"));
    }


    /**
     * {@inheritDoc}
     */
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    /**
     * {@inheritDoc}
     */
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.NONE;
    }

    public CvsImportBaseTestCase() {
        getConnection()
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:mem:testnpadb");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");

    }


    @Before
    public void setUp() throws Exception {
        // Load the applicationContext.xml file
        ctx = new ClassPathXmlApplicationContext(
                "testApplicationContext.xml",
                "core-module-bulkimport-csv.xml"
        );

        sessionFactory = (SessionFactory) ctx.getBean("sessionFactory");

        session = sessionFactory.openSession();

        super.setUp();

    }


    @Test
    public void testGetImportFile() {
        assertTrue(true);
    }

}
