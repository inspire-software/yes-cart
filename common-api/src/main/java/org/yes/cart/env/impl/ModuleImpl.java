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

package org.yes.cart.env.impl;

import org.yes.cart.env.Module;

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 21/03/2018
 * Time: 20:33
 */
public class ModuleImpl implements Module {

    private String functionalArea;
    private String name;
    private String subName;
    private Instant loaded = Instant.now();

    @Override
    public String getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(final String functionalArea) {
        this.functionalArea = functionalArea;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getSubName() {
        return subName;
    }

    public void setSubName(final String subName) {
        this.subName = subName;
    }

    @Override
    public Instant getLoaded() {
        return loaded;
    }

    public void setLoaded(final Instant loaded) {
        this.loaded = loaded;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleImpl)) return false;

        final ModuleImpl module = (ModuleImpl) o;

        if (functionalArea != null ? !functionalArea.equals(module.functionalArea) : module.functionalArea != null)
            return false;
        if (name != null ? !name.equals(module.name) : module.name != null) return false;
        if (subName != null ? !subName.equals(module.subName) : module.subName != null) return false;
        return loaded != null ? loaded.equals(module.loaded) : module.loaded == null;
    }

    @Override
    public int hashCode() {
        int result = functionalArea != null ? functionalArea.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (subName != null ? subName.hashCode() : 0);
        result = 31 * result + (loaded != null ? loaded.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModuleImpl{" +
                "functionalArea='" + functionalArea + '\'' +
                ", name='" + name + '\'' +
                ", subName='" + subName + '\'' +
                ", loaded=" + loaded +
                '}';
    }
}
