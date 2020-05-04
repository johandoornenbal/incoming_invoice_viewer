package org.incode.estatio;

import java.net.URLEncoder;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;

import org.springframework.beans.factory.annotation.Autowired;

import org.incode.estatio.dom.DomainObject;
import org.incode.estatio.dom.TaskObject;
import lombok.Getter;

public class DrawerLayout extends VerticalLayout {

    public DrawerLayout(@Autowired TestRepo testRepo){

        Div div = new Div();

        this.textField = new TextField("test textfield");

        this.tasks = new ComboBox<>();
        tasks.setLabel("tasks");
        tasks.setClearButtonVisible(true);
        tasks.setItemLabelGenerator(TaskObject::getDescription);
        tasks.addValueChangeListener(event->{
            if (event!=null && event.getValue()!=null) {
                final String title = event.getValue().getDescription() != null ? event.getValue().getDescription() : "";
                final String href =
                        event.getValue().getObject().getHref() != null ? event.getValue().getObject().getHref() : "";
                String val = "Task with description: " + title + " and object: " + href;
                text.setText(val);
                if (event.getValue().getObject().getHref().contains("Invoice")){
                    final String href1 = event.getValue().getObject().getHref();
                    final String encoded = URLEncoder.encode(href1);
                    final RouterLink invoiceLink = new RouterLink(event.getValue().getDescription(),
                            CompleteInvoiceLayout.class,
                            encoded);
                    HorizontalLayout h = new HorizontalLayout();
                    h.add(invoiceLink);
                    div.add(h);
                }
            } else {
                text.setText("");
            }
        });

        this.organisations = new ComboBox<>();
        organisations.setLabel("organisations");
        organisations.setClearButtonVisible(true);
        organisations.setItemLabelGenerator(DomainObject::getTitle);
        organisations.addValueChangeListener(event->{
            if (event!=null && event.getValue()!=null) {
                final String title = event.getValue().getTitle() != null ? event.getValue().getTitle() : "";
                final String href = event.getValue().getHref() != null ? event.getValue().getHref() : "";
                String val = "Object with title: " + title + " and href: " + href;
                text.setText(val);
            } else {
                text.setText("");
            }
        });

        this.text = new Text("");

        Button button = new Button("Test button");
        Paragraph p = new Paragraph();
        p.setWidth("20px");
        p.add(text);
        div.setWidthFull();
        div.add(p);

        add(textField, tasks, organisations, button, div);

    }

    @Getter
    private TextField textField;

    @Getter
    private ComboBox<TaskObject> tasks;

    @Getter
    private ComboBox<DomainObject> organisations;

    @Getter
    private Text text;

}
