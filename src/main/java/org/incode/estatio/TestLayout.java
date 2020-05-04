package org.incode.estatio;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import org.springframework.beans.factory.annotation.Autowired;

import org.incode.estatio.dom.DomainObject;

public class TestLayout extends HorizontalLayout {

    private TestRepoFeederThread thread;
    private TestRepo testRepo;
    private ComboBox<DomainObject> testDropdown;
    private Text text;

    public TestLayout(@Autowired TestRepo repo){
        this.testRepo = repo;
        testDropdown = new ComboBox<>();
        testDropdown.setItems(testRepo.getDomainObjects());
        testDropdown.setLabel("test dropdown");
        testDropdown.setItemLabelGenerator(DomainObject::getTitle);
        testDropdown.setWidth("30%");
        testDropdown.addValueChangeListener((event)->{
            final String title = event.getValue().getTitle() !=null ? event.getValue().getTitle() : "";
            final String href = event.getValue().getHref() !=null ? event.getValue().getHref() : "";
            String val = "Object with title: " + title + " and href: " + href;
            text.setText(val);
            testDropdown.setValue(event.getValue());
        });

        text = new Text("no choice ...");
        add(testDropdown, text);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // Start the data feed thread
        thread = new TestRepoFeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        thread.interrupt();
        thread = null;
    }

    private static class TestRepoFeederThread extends Thread {

        private TestLayout view;
        private UI ui;

        public TestRepoFeederThread(UI ui, TestLayout view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                try {
                    sleep(3000);
                    final DomainObject object = new DomainObject();
                    object.setTitle("obj"+i);
                    object.setHref("href"+i);
                    // the testRepo service stays alive while the layout can be re-instantiated upon a browser refresh, so we access UI only when adding new items
                    if (!view.testRepo.getDomainObjects().stream().filter(o->o.getHref().equals(object.getHref())).findFirst().isPresent()) {
                        view.testRepo.getDomainObjects().add(object);
                        ui.access(() -> {
                            final DomainObject preValue = view.testDropdown.getValue();
                            view.testDropdown.setItems(view.testRepo.getDomainObjects());
                            view.testDropdown.setValue(preValue);
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
