/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.Auditable;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractDtoServiceImpl<DTOIFACE extends Identifiable, DTOIMPL, IFACE extends Auditable> implements GenericDTOService<DTOIFACE> {

    protected final DtoFactory dtoFactory;

    protected final Assembler assembler;

    protected GenericService<IFACE> service;

    private final AdaptersRepository adaptersRepository;

    /** {@inheritDoc}*/
    public GenericService<IFACE> getService() {
        return service;
    }

    /** {@inheritDoc} */
    public List<DTOIFACE> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<IFACE> entities = service.findAll();
        final List<DTOIFACE> dtos = new ArrayList<DTOIFACE>(entities.size());
        fillDTOs(entities, dtos);
        return dtos;
    }

    /** {@inheritDoc} */
    public DTOIFACE getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getById(id, getAdaptersRepository());
    }

    /** {@inheritDoc} */
    public DTOIFACE getById(final long id, final Map converters)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final IFACE entity = service.getById(id);
        if (entity != null) {
            final DTOIFACE dto = (DTOIFACE) dtoFactory.getByIface(getDtoIFace());
            assembler.assembleDto(dto, entity, converters, dtoFactory);
            return dto;
        }
        return null;
    }

    /** {@inheritDoc} */
    public DTOIFACE getNew() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        return (DTOIFACE) dtoFactory.getByIface( getDtoIFace());
    }

    /** {@inheritDoc} */
    public void remove(final long id) {
        service.delete(service.getById(id));
    }


    /**
     * Convert list of  entities to dtos
     * @param entities list of entities
     * @throws UnmappedInterfaceException in case of config errors
     * @throws UnableToCreateInstanceException ion case of dto creating errors
     * @return list of dto
     */
    public List<DTOIFACE> getDTOs(final Collection<IFACE> entities)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<DTOIFACE> result = new ArrayList<DTOIFACE>();
        if (entities != null) {
            fillDTOs(entities, result);
        }
        return result;
    }

    /**
     * Fill dtos from entities
     * @param entities list of entities
     * @param dtos list of dtos
     * @throws UnmappedInterfaceException in case of config errors
     * @throws UnableToCreateInstanceException ion case of dto creating errors
     */
    public void fillDTOs(final Collection<IFACE> entities, final Collection<DTOIFACE> dtos)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (IFACE entity : entities) {
            DTOIFACE dto = (DTOIFACE) dtoFactory.getByIface(getDtoIFace());
            assembler.assembleDto(dto, entity, getAdaptersRepository(), dtoFactory);
            dtos.add(dto);
        }
    }

    /**
     * Construct base remote service.
     * @param dtoFactory {@link DtoFactory}
     * @param service {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public AbstractDtoServiceImpl(final DtoFactory dtoFactory,
                                  final GenericService<IFACE> service,
                                  final AdaptersRepository adaptersRepository) {
        this.dtoFactory = dtoFactory;
        this.service = service;
        this.assembler = DTOAssembler.newAssembler(getDtoImpl(), getEntityIFace());
        this.adaptersRepository = adaptersRepository;
    }

    /**
     * Get the converters repository.
     * @return converters repository.
     */
    public Map<String, Object> getAdaptersRepository() {
        if (adaptersRepository == null) {
            return null;
        }
        return adaptersRepository.getAll();
    }

    /**
     * @return {@link EntityFactory}
     */
    public EntityFactory getEntityFactory() {
        return service.getGenericDao().getEntityFactory();
    }

    /**
     *
     * @return {@link DtoFactory}
     */
    public DtoFactory getDtoFactory() {
        return dtoFactory;
    }

    /**
     * @return {@link DTOAssembler}
     */
    public Assembler getAssembler() {
        return assembler;
    }


    /** {@inheritDoc}*/
    @SuppressWarnings({"unchecked"})
    public DTOIFACE create(final DTOIFACE instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        IFACE iface = (IFACE) getEntityFactory().getByIface(getEntityIFace());
        assembler.assembleEntity(instance, iface, getAdaptersRepository(), getDtoFactory());
        iface = service.create(iface);
        return getById(iface.getId());
    }



    /** {@inheritDoc}*/
    public DTOIFACE update(final DTOIFACE instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        IFACE iface = service.getById(instance.getId());
        assembler.assembleEntity(instance, iface, getAdaptersRepository(), dtoFactory);
        updatePostProcess(instance, iface);
        iface = service.update(iface);
        return getById(iface.getId());
    }

    /**
     * Use this hook in subclasses to modify entity after it had been populated with
     * assembler from DTO.
     *
     * @param dto dto with update information
     * @param entity entity to update
     */
    protected void updatePostProcess(DTOIFACE dto, IFACE entity) {
        // extension hook
    }



    /** {@inheritDoc}*/
   /* public abstract DTOIFACE update(DTOIFACE instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException; */

    /**
     * Get the dto interface.
     * @return dto interface.
     */
    public abstract Class<DTOIFACE> getDtoIFace();

    /**
     * Get the dto implementation class.
     * @return dto implementation class.
     */
    public abstract Class<DTOIMPL> getDtoImpl();

    /**
     * Get the entity interface.
     * @return entity interface.
     */
    public abstract Class<IFACE> getEntityIFace();




    /**
     * Get the attribute codes. Used by business entity, that has attributes.
     * @param attrValues list of attribute values.
     * @return list of attribute codes.
     */
    protected List<String> getCodes(final List<? extends AttrValueDTO> attrValues) {
        final List<String> codes = new ArrayList<String>(attrValues.size());
        for(AttrValueDTO attrValueCategoryDTO : attrValues) {
            if (attrValueCategoryDTO != null && attrValueCategoryDTO.getAttributeDTO() != null) {
                codes.add(
                        attrValueCategoryDTO.getAttributeDTO().getCode()
                );
            }
        }
        return codes;
    }


}
