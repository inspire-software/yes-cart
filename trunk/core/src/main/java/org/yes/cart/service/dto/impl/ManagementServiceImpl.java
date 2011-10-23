package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ManagerDTOImpl;
import org.yes.cart.domain.dto.impl.RoleDTOImpl;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.domain.entity.ManagerRole;
import org.yes.cart.domain.entity.Role;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.ManagerService;
import org.yes.cart.service.dto.ManagementService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * User management service implemenation
 * <p/>
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ManagementServiceImpl implements ManagementService {

    private static final String EMAIL = "email";
    private static final String CODE = "code";


    private final ManagerService managerService;

    private final GenericDAO<ManagerRole, Long> managerRoleDao;

    private final GenericDAO<Role, Long> roleDao;

    private final DtoFactory dtoFactory;

    private final DTOAssembler managerAssembler;

    private final DTOAssembler roleAssembler;


    /**
     * Construct user managment service.
     *
     * @param managerService          manager service to use
     * @param managerRoleDao      manager roles dao
     * @param roleDao             role dao
     * @param dtoFactory          {@link DtoFactory}
     */
    public ManagementServiceImpl(final ManagerService managerService,
                                    final GenericDAO<ManagerRole, Long> managerRoleDao,
                                    final GenericDAO<Role, Long> roleDao,
                                    final DtoFactory dtoFactory) {
        this.managerService = managerService;
        this.managerRoleDao = managerRoleDao;
        this.roleDao = roleDao;
        this.dtoFactory = dtoFactory;

        managerAssembler = DTOAssembler.newAssembler(ManagerDTOImpl.class, Manager.class);

        roleAssembler = DTOAssembler.newAssembler(RoleDTOImpl.class, Role.class);
    }

    /**
     * Get the list of managers by given filtring citeria.
     *
     * @param emailFilter     optional email filter
     * @param firstNameFilter optional first name filter
     * @param lastNameFilter  optional last name filter
     * @return list of managers dto that match given criteria
     * @throws UnmappedInterfaceException
     * @throws UnableToCreateInstanceException
     *
     */
    public List<ManagerDTO> getManagers(final String emailFilter,
                                        final String firstNameFilter,
                                        final String lastNameFilter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Criterion> criteriaList = new ArrayList<Criterion>();
        if (StringUtils.isNotBlank(emailFilter)) {
            criteriaList.add(Restrictions.like(EMAIL, emailFilter, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(firstNameFilter)) {
            criteriaList.add(Restrictions.like("firstname", firstNameFilter, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotBlank(lastNameFilter)) {
            criteriaList.add(Restrictions.like("lastname", lastNameFilter, MatchMode.ANYWHERE));
        }

        final List<Manager> managers = managerService.findByCriteria(
                criteriaList.toArray(new Criterion[criteriaList.size()]));

        final List<ManagerDTO> managersDTO = new ArrayList<ManagerDTO>(managers.size());



        for (Manager manager : managers) {
            final ManagerDTO managerDTO = dtoFactory.getByIface(ManagerDTO.class);
            managerAssembler.assembleDto(managerDTO, manager, null, dtoFactory);
            managersDTO.add(managerDTO);
        }

        return managersDTO;
    }

    /**
     * {@inheritDoc}
     */
    public List<RoleDTO> getAssignedManagerRoles(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> result = new ArrayList<RoleDTO>();
        final Manager manager = managerService.findSingleByCriteria(Restrictions.eq(EMAIL, userId));
        if (manager != null) {
            List<Role> roles = roleDao.findByNamedQuery(
                    "ASSIGNED.ROLES.BY.USER.EMAIL",
                    userId);
            fillRolesDTOs(result, roles);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<RoleDTO> getAvailableManagerRoles(final String userId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> result = new ArrayList<RoleDTO>();
        final Manager manager = managerService.findSingleByCriteria(Restrictions.eq(EMAIL, userId));
        if (manager != null) {
            List<Role> roles = roleDao.findByNamedQuery(
                    "AVAILABLE.ROLES.BY.USER.EMAIL",
                    userId);
            fillRolesDTOs(result, roles);
        }
        return result;
    }

    private void fillRolesDTOs(final List<RoleDTO> result, final List<Role> roles)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (Role role : roles) {
            final RoleDTO roleDTO = dtoFactory.getByIface(RoleDTO.class);
            roleAssembler.assembleDto(roleDTO, role, null, dtoFactory);
            result.add(roleDTO);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void addUser(final String userId, final String firstName, final String lastName)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        final Manager manager = managerService.getGenericDao().getEntityFactory().getByIface(Manager.class);

        manager.setEmail(userId);
        manager.setFirstname(firstName);
        manager.setLastname(lastName);
        manager.setPassword("TODO"); //TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        managerService.create(manager, null); //No particular shop at this moment, but  future

    }

    /**
     * {@inheritDoc}
     */
    public void updateUser(final String userId, final String firstName, final String lastName) {
        final Manager manager = managerService.findSingleByCriteria(Restrictions.eq(EMAIL, userId));
        if (manager != null) {
            manager.setFirstname(firstName);
            manager.setLastname(lastName);
            managerService.update(manager);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void resetPassword(final String userId) {

        final Manager manager = managerService.findSingleByCriteria(Restrictions.eq(EMAIL, userId));
        if (manager != null) {
            managerService.resetPassword(manager);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteUser(final String userId) {
        final Manager manager = managerService.findSingleByCriteria(Restrictions.eq(EMAIL, userId));
        if (manager != null) {
            final List<ManagerRole> assignedRoles = managerRoleDao.findByCriteria(Restrictions.eq(EMAIL, userId));
            for (ManagerRole managerRole : assignedRoles) {
                managerRoleDao.delete(managerRole);
            }
            managerService.delete(manager);
        }
    }

    public List<RoleDTO> getRolesList() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<RoleDTO> result = new ArrayList<RoleDTO>();
        List<Role> roles = roleDao.findAll();
        fillRolesDTOs(result, roles);
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public void addRole(final String role, final String description) {
        final Role roleEntity = roleDao.getEntityFactory().getByIface(Role.class);
        roleEntity.setCode(role);
        roleEntity.setDescription(description);
        roleDao.create(roleEntity);
    }

    /**
     * {@inheritDoc}
     */
    public void updateRole(final String role, final String decription) {
        final Role roleEntity = roleDao.findSingleByCriteria(Restrictions.eq(CODE, role));
        if (roleEntity != null) {
            roleEntity.setDescription(decription);
            roleDao.update(roleEntity);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteRole(final String role) {
        final Role roleEntity = roleDao.findSingleByCriteria(Restrictions.eq(CODE, role));
        if (roleEntity != null) {
            final List<ManagerRole> assignedRoles = managerRoleDao.findByCriteria(Restrictions.eq(CODE, role));
            for (ManagerRole managerRole : assignedRoles) {
                managerRoleDao.delete(managerRole);
            }
            roleDao.delete(roleEntity);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void grantRole(final String userId, final String role) {
        final Role roleEntity = roleDao.findSingleByCriteria(Restrictions.eq(CODE, role));
        final Manager manager = managerService.findSingleByCriteria(Restrictions.eq(EMAIL, userId));
        if (roleEntity != null && manager != null) {
            final ManagerRole managerRole = managerRoleDao.getEntityFactory().getByIface(ManagerRole.class);
            managerRole.setCode(role);
            managerRole.setEmail(userId);
            managerRoleDao.create(managerRole);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void revokeRole(final String userId, final String role) {
        final ManagerRole managerRole = managerRoleDao.findSingleByCriteria(
                Restrictions.eq(CODE, role),
                Restrictions.eq(EMAIL, userId)
        );
        if (managerRole != null) {
            managerRoleDao.delete(managerRole);
        }
    }


}
