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

package org.yes.cart.service.vo;

import java.util.List;

/**
 * User: denispavlov
 * Date: 06/08/2016
 * Time: 13:38
 */
public interface VoAssemblySupport {

    <V, D> List<V> assembleVos(final Class<V> voc,
                               final Class<D> dtoc,
                               final List<D> dtos);

    <V, D> V assembleVo(final Class<V> voc,
                        final Class<D> dtoc,
                        final V vo,
                        final D dto);

    <V, D> D assembleDto(final Class<D> dtoc,
                         final Class<V> voc,
                         final D dto,
                         final V vo);

    <V, D> VoAssembler<V, D> with(final Class<V> voc,
                                  final Class<D> dtoc);


    interface VoAssembler<V, D> {

        List<V> assembleVos(final List<D> dtos);

        V assembleVo(final V vo, final D dto);

        D assembleDto(final D dto, final V vo);

    }
}
