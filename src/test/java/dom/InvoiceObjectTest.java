package dom;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.incode.estatio.dom.InvoiceObject;

class InvoiceObjectTest {

    @Test
    void parseLocalDateToString() {

        final LocalDate date = LocalDate.of(2020, 2, 29);
        final String string = InvoiceObject.parseLocalDateToString(date);
        Assertions.assertThat(string).isEqualTo("2020-02-29");

    }
}