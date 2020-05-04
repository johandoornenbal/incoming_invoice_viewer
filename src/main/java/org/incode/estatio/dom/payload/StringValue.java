package org.incode.estatio.dom.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StringValue {
    public StringValue(){}
    public StringValue(final String value){
        this.value = value;
    }
    private String value;
}
