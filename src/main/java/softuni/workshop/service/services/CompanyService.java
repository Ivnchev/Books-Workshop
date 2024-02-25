package softuni.workshop.service.services;

import java.io.IOException;

public interface CompanyService {

    void importCompanies();

    boolean areImported();

    String readCompaniesXmlFile() throws IOException;
}
