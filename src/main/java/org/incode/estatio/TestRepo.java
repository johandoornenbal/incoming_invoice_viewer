package org.incode.estatio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import org.incode.estatio.dom.DomainObject;
import lombok.Getter;
import lombok.Setter;

@Service
public class TestRepo {

    public TestRepo(){
        this.domainObjects = new ArrayList<>();
    }

    @Getter @Setter
    public List<DomainObject> domainObjects;

}
