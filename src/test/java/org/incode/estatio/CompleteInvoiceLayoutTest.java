package org.incode.estatio;

import java.util.Locale;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CompleteInvoiceLayoutTest {
    @Test
    public void moneyFieldFormattingWorks() throws Exception {

        Double value = Double.valueOf("123.01");
        final String format = String.format(Locale.FRANCE, "%1$.2f", value);
        Assertions.assertThat(format).isEqualTo("123,01");

    }
}