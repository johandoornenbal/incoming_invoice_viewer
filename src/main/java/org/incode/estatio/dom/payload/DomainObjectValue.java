package org.incode.estatio.dom.payload;

import org.incode.estatio.dom.DomainObject;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DomainObjectValue {
    public DomainObjectValue(final DomainObject value){
        this.value = value;
    }
    private DomainObject value;
}
