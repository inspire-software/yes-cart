package org.yes.cart.remote.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.impl.CategoryDTOImpl;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 18/11/2015
 * Time: 20:47
 */
public class RemoteCategoryServiceImplTest {

    private final Mockery context = new JUnit4Mockery();

    private Set<Long> scanTree(final List<CategoryDTO> tree) {
        final Set<Long> ids = new HashSet<Long>();
        if (tree != null) {
            for (final CategoryDTO cat : tree) {
                ids.add(cat.getCategoryId());
                ids.addAll(scanTree(cat.getChildren()));
            }
        }
        return ids;
    }

    /**
     * root 100
     *   |
     *   |-- 110
     *   |    |
     *   |    |-- 111
     *   |    |-- 112
     *   |    +-- 113
     *   |
     *   |-- 120
     *   |
     *   +-- 130
     *
     */
    private List<CategoryDTO> createRootStructure() {

        final CategoryDTO root = new CategoryDTOImpl();
        root.setCategoryId(100L);
        root.setParentId(100L);


        final CategoryDTO lvl1_1 = new CategoryDTOImpl();
        lvl1_1.setCategoryId(110L);
        lvl1_1.setParentId(100L);

        final CategoryDTO lvl1_2 = new CategoryDTOImpl();
        lvl1_2.setCategoryId(120L);
        lvl1_2.setParentId(100L);

        final CategoryDTO lvl1_3 = new CategoryDTOImpl();
        lvl1_3.setCategoryId(130L);
        lvl1_3.setParentId(100L);

        root.setChildren(Arrays.asList(lvl1_1, lvl1_2, lvl1_3));

        final CategoryDTO lvl2_1 = new CategoryDTOImpl();
        lvl2_1.setCategoryId(111L);
        lvl2_1.setParentId(110L);

        final CategoryDTO lvl2_2 = new CategoryDTOImpl();
        lvl2_2.setCategoryId(112L);
        lvl2_2.setParentId(110L);

        final CategoryDTO lvl2_3 = new CategoryDTOImpl();
        lvl2_3.setCategoryId(113L);
        lvl2_3.setParentId(110L);

        lvl1_1.setChildren(Arrays.asList(lvl2_1, lvl2_2, lvl2_3));



        return Collections.singletonList(root);
    }

    @Test
    public void testGetAllSysAdmin() throws Exception {

        final DtoCategoryService cs = context.mock(DtoCategoryService.class, "cs");
        final FederationFacade ff = context.mock(FederationFacade.class, "ff");

        context.checking(new Expectations() {{
            allowing(cs).getAll(); will(returnValue(createRootStructure()));
            allowing(ff).isManageable(100L, CategoryDTO.class); will(returnValue(true));
        }});

        final RemoteCategoryServiceImpl rcs = new RemoteCategoryServiceImpl(cs, ff);

        final List<CategoryDTO> root = rcs.getAll();

        final Set<Long> ids = scanTree(root);

        assertEquals(7, ids.size());
        assertTrue(ids.toString(), ids.containsAll(Arrays.asList(100L, 110L, 111L, 112L, 113L, 120L, 130L)));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAllShopAdminLvl1() throws Exception {

        final DtoCategoryService cs = context.mock(DtoCategoryService.class, "cs");
        final FederationFacade ff = context.mock(FederationFacade.class, "ff");

        context.checking(new Expectations() {{
            allowing(cs).getAll(); will(returnValue(createRootStructure()));
            allowing(ff).isManageable(100L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(110L, CategoryDTO.class); will(returnValue(true));
            allowing(ff).isManageable(120L, CategoryDTO.class); will(returnValue(true));
            allowing(ff).isManageable(130L, CategoryDTO.class); will(returnValue(true));
        }});

        final RemoteCategoryServiceImpl rcs = new RemoteCategoryServiceImpl(cs, ff);

        final List<CategoryDTO> root = rcs.getAll();

        final Set<Long> ids = scanTree(root);

        assertEquals(7, ids.size());
        assertTrue(ids.toString(), ids.containsAll(Arrays.asList(100L, 110L, 111L, 112L, 113L, 120L, 130L)));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAllShopAdminLvl1Partial() throws Exception {

        final DtoCategoryService cs = context.mock(DtoCategoryService.class, "cs");
        final FederationFacade ff = context.mock(FederationFacade.class, "ff");

        context.checking(new Expectations() {{
            allowing(cs).getAll(); will(returnValue(createRootStructure()));
            allowing(ff).isManageable(100L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(110L, CategoryDTO.class); will(returnValue(true));
            allowing(ff).isManageable(120L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(130L, CategoryDTO.class); will(returnValue(true));
        }});

        final RemoteCategoryServiceImpl rcs = new RemoteCategoryServiceImpl(cs, ff);

        final List<CategoryDTO> root = rcs.getAll();

        final Set<Long> ids = scanTree(root);

        assertEquals(6, ids.size());
        assertTrue(ids.toString(), ids.containsAll(Arrays.asList(100L, 110L, 111L, 112L, 113L, 130L)));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAllShopAdminLvl2() throws Exception {

        final DtoCategoryService cs = context.mock(DtoCategoryService.class, "cs");
        final FederationFacade ff = context.mock(FederationFacade.class, "ff");

        context.checking(new Expectations() {{
            allowing(cs).getAll(); will(returnValue(createRootStructure()));
            allowing(ff).isManageable(100L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(110L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(120L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(130L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(111L, CategoryDTO.class); will(returnValue(true));
            allowing(ff).isManageable(112L, CategoryDTO.class); will(returnValue(true));
            allowing(ff).isManageable(113L, CategoryDTO.class); will(returnValue(true));
        }});

        final RemoteCategoryServiceImpl rcs = new RemoteCategoryServiceImpl(cs, ff);

        final List<CategoryDTO> root = rcs.getAll();

        final Set<Long> ids = scanTree(root);

        assertEquals(5, ids.size());
        assertTrue(ids.toString(), ids.containsAll(Arrays.asList(100L, 110L, 111L, 112L, 113L)));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAllShopAdminLvl2Partial() throws Exception {

        final DtoCategoryService cs = context.mock(DtoCategoryService.class, "cs");
        final FederationFacade ff = context.mock(FederationFacade.class, "ff");

        context.checking(new Expectations() {{
            allowing(cs).getAll(); will(returnValue(createRootStructure()));
            allowing(ff).isManageable(100L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(110L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(120L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(130L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(111L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(112L, CategoryDTO.class); will(returnValue(true));
            allowing(ff).isManageable(113L, CategoryDTO.class); will(returnValue(false));
        }});

        final RemoteCategoryServiceImpl rcs = new RemoteCategoryServiceImpl(cs, ff);

        final List<CategoryDTO> root = rcs.getAll();

        final Set<Long> ids = scanTree(root);

        assertEquals(3, ids.size());
        assertTrue(ids.toString(), ids.containsAll(Arrays.asList(100L, 110L, 112L)));

        context.assertIsSatisfied();

    }

    @Test
    public void testGetAllShopAdminNoAccess() throws Exception {

        final DtoCategoryService cs = context.mock(DtoCategoryService.class, "cs");
        final FederationFacade ff = context.mock(FederationFacade.class, "ff");

        context.checking(new Expectations() {{
            allowing(cs).getAll(); will(returnValue(createRootStructure()));
            allowing(ff).isManageable(100L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(110L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(120L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(130L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(111L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(112L, CategoryDTO.class); will(returnValue(false));
            allowing(ff).isManageable(113L, CategoryDTO.class); will(returnValue(false));
        }});

        final RemoteCategoryServiceImpl rcs = new RemoteCategoryServiceImpl(cs, ff);

        final List<CategoryDTO> root = rcs.getAll();

        final Set<Long> ids = scanTree(root);

        assertEquals(1, ids.size());
        assertTrue(ids.toString(), ids.containsAll(Arrays.asList(100L)));

        context.assertIsSatisfied();

    }

}