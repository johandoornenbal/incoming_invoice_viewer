package org.incode.estatio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.incode.estatio.dom.DomainObject;
import org.incode.estatio.dom.TaskObject;
import lombok.Getter;
import lombok.Setter;

import org.incode.estatio.services.EstatioClient;

@Service
public class RefDataRepo {

    // TODO: not sure this works ...
    @Autowired
    protected CredentialsService credentialsService;

    private RefDataFeederThread thread;

    @Getter @Setter
    private List<DomainObject> organisations;

    @Getter @Setter
    private List<TaskObject> tasks;

    public void retrieveRefData() {
        // Start the data feed thread
        thread = new RefDataFeederThread( this);
        thread.start();
    }


    // TODO: not sure this works
    private static class RefDataFeederThread extends Thread {

        private final RefDataRepo repo;

        public RefDataFeederThread(RefDataRepo repo) {
            this.repo = repo;
        }

        @Override
        public void run() {
            if (repo.credentialsService!=null && repo.credentialsService.hasCredentials()) {
                final List<DomainObject> allOrganisations = EstatioClient
                        .allOrganisations(repo.credentialsService.getUsername(), repo.credentialsService.getPassword());
                repo.setOrganisations(allOrganisations);
            }
            this.interrupt();
            repo.thread = null;
        }

    }

}
