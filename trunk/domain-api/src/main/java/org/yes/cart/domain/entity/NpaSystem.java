package org.yes.cart.domain.entity;

import java.util.Map;

/**
 * TODO kill this interface.
 */
public interface NpaSystem extends Auditable {

    String getCode();

    void setCode(String code);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Map<String, AttrValue> getAttribute();

    void setAttribute(Map<String, AttrValue> attribute);

    long getNpaSystemId();

    void setNpaSystemId(long npaSystemId);
}
