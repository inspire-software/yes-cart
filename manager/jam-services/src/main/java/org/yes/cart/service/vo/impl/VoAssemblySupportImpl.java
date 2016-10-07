/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.service.vo.VoAssemblySupport;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 06/08/2016
 * Time: 13:42
 */
public class VoAssemblySupportImpl implements VoAssemblySupport {

    private final BeanFactory voBeans;
    private final BeanFactory dtoBeans;
    private final AdaptersRepository voAdapters;
    private final AdaptersRepository dtoAdapters;

    public VoAssemblySupportImpl(final BeanFactory voBeans,
                                 final BeanFactory dtoBeans,
                                 final AdaptersRepository voAdapters,
                                 final AdaptersRepository dtoAdapters) {
        this.voBeans = voBeans;
        this.dtoBeans = dtoBeans;
        this.voAdapters = voAdapters;
        this.dtoAdapters = dtoAdapters;
    }

    /** {@inheritDoc} */
    @Override
    public <V, D> List<V> assembleVos(final Class<V> voc, final Class<D> dtoc, final List<D> dtos) {

        final List<V> rez = new ArrayList<>(dtos.size());
        DTOAssembler.newAssembler(voc, dtoc).assembleDtos(rez, dtos, this.voAdapters.getAll(), this.voBeans);
        return rez;

    }

    /** {@inheritDoc} */
    @Override
    public <V, D> V assembleVo(final Class<V> voc, final Class<D> dtoc, final V vo, final D dto) {
        DTOAssembler.newAssembler(voc, dtoc).assembleDto(vo, dto, this.voAdapters.getAll(), this.voBeans);
        return vo;
    }

    @Override
    public <V, D> D assembleDto(final Class<D> dtoc, final Class<V> voc, final D dto, final V vo) {
        DTOAssembler.newAssembler(voc, dtoc).assembleEntity(vo, dto, this.voAdapters.getAll(), this.voBeans);
        return dto;
    }

    @Override
    public <V, D> VoAssembler<V, D> with(final Class<V> voc, final Class<D> dtoc) {

        final Assembler asm = DTOAssembler.newAssembler(voc, dtoc);

        return new VoAssembler<V, D>() {
            @Override
            public List<V> assembleVos(final List<D> dtos) {
                final List<V> rez = new ArrayList<>(dtos.size());
                asm.assembleDtos(rez, dtos, VoAssemblySupportImpl.this.voAdapters.getAll(), VoAssemblySupportImpl.this.voBeans);
                return rez;
            }

            @Override
            public V assembleVo(final V vo, final D dto) {
                asm.assembleDto(vo, dto, VoAssemblySupportImpl.this.voAdapters.getAll(), VoAssemblySupportImpl.this.voBeans);
                return vo;
            }

            @Override
            public D assembleDto(final D dto, final V vo) {
                asm.assembleEntity(vo, dto, VoAssemblySupportImpl.this.voAdapters.getAll(), VoAssemblySupportImpl.this.voBeans);
                return dto;
            }
        };
    }
}
