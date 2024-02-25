package softuni.workshop.service.services;

import softuni.workshop.service.dtos.ProjectDto;

import java.util.List;

public interface ProjectService {

    void importProjects();

    boolean areImported();

    String readProjectsXmlFile();

    String exportFinishedProjectsAsXml();

    String exportFinishedProjectsAsText();

    List<ProjectDto> getFinishedProjects();
}
