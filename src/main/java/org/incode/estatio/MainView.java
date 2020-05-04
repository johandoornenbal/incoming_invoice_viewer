package org.incode.estatio;

import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;

import org.springframework.beans.factory.annotation.Autowired;

import org.incode.estatio.dom.DomainObject;
import org.incode.estatio.dom.TaskObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.incode.estatio.services.EstatioClient;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
@Push
@PreserveOnRefresh
public class MainView extends AppLayout {

    public MainView(@Autowired TestRepo testRepo, @Autowired RefDataRepo refDataRepo, @Autowired CredentialsService credentialsService){
        credentialsService.setUsername("estatio-user-fr");
        credentialsService.setPassword("pass");
        this.credentialsService = credentialsService;
        this.drawerLayout = new DrawerLayout(testRepo);
        this.refDataRepo = refDataRepo;

        addToNavbar(new DrawerToggle());
        addToNavbar(new RouterLink("Start all over again", MainView.class));
        addToDrawer(getDrawerLayout());
        setContent(new TestLayout(testRepo));
    }

    private FeederThread thread;

    @Getter
    private CredentialsService credentialsService;

    @Getter @Setter
    private List<DomainObject> organisations;

    @Getter @Setter
    private List<TaskObject> tasks;

    @Getter
    private DrawerLayout drawerLayout;

    @Getter
    private RefDataRepo refDataRepo;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // Start the data feed thread
        thread = new FeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        thread.interrupt();
        thread = null;
    }

    private static class FeederThread extends Thread {
        private final UI ui;
        private final MainView view;

        private int count = 0;

        public FeederThread(UI ui, MainView view) {
            this.ui = ui;
            this.view = view;
        }

        @SneakyThrows @Override
        public void run() {
            while (view.getCredentialsService()==null || !view.credentialsService.hasCredentials()){
                sleep(10);
            }
            ui.access(()->{
                view.getDrawerLayout().getTextField().setValue("loading tasks");
            });
            if (view.getTasks()==null){
                view.setTasks(
                        EstatioClient.getTasks(view.getCredentialsService().getUsername(), view.getCredentialsService().getPassword())
                );
            }
            ui.access(()->{
                view.getDrawerLayout().getTextField().setValue("done loading tasks");
                view.getDrawerLayout().getTasks().setItems(view.getTasks());
            });
            ui.access(()->{
                view.getDrawerLayout().getTextField().setValue("loading orgs");
            });
            if (view.getOrganisations()==null){
                final List<DomainObject> organisations = EstatioClient
                        .allOrganisations(view.getCredentialsService().getUsername(), view.getCredentialsService().getPassword());
                view.setOrganisations(
                        organisations
                );
                view.refDataRepo.setOrganisations(organisations);
            }
            ui.access(()->{
                view.getDrawerLayout().getTextField().setValue("done loading orgs");
                view.getDrawerLayout().getOrganisations().setItems(view.getOrganisations());
            });

        }
    }

}


