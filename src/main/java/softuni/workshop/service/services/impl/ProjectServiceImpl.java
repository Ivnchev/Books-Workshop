package softuni.workshop.service.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.workshop.data.entities.Company;
import softuni.workshop.data.entities.Project;
import softuni.workshop.data.repositories.CompanyRepository;
import softuni.workshop.data.repositories.ProjectRepository;
import softuni.workshop.exceptions.CustomXmlException;
import softuni.workshop.exceptions.EntityNotFoundException;
import softuni.workshop.service.dtos.ProjectDto;
import softuni.workshop.service.dtos.ProjectRootDto;
import softuni.workshop.service.services.ProjectService;
import softuni.workshop.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {

    private final static String PROJECTS_PATH = "src/main/resources/files/xmls/projects.xml";
    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final XmlParser xmlParser;
    private final ModelMapper mapper;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, CompanyRepository companyRepository, XmlParser xmlParser, ModelMapper mapper) {
        this.projectRepository = projectRepository;
        this.companyRepository = companyRepository;
        this.xmlParser = xmlParser;
        this.mapper = mapper;
    }

    @Override
    public void importProjects(){
        ProjectRootDto projectRootDto = this.xmlParser.parseXml(ProjectRootDto.class, PROJECTS_PATH);

        for (ProjectDto projectDto : projectRootDto.getProjectDtoList()) {

            Project project = this.mapper.map(projectDto, Project.class);

            Company company = this.companyRepository.findByName(project.getCompany().getName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Company with name: %s not found !", projectDto.getCompany().getName())
                    ));

            project.setCompany(company);

            this.projectRepository.saveAndFlush(project);
        }
    }

    @Override
    public boolean areImported() {
       return this.projectRepository.count() > 0;
    }

    @Override
    public String readProjectsXmlFile() {
        try {

            return String.join("\n", Files.readAllLines(Path.of(PROJECTS_PATH)));

        } catch (IOException e) {

            throw new CustomXmlException(e.getMessage(), e);
        }
    }

    @Override
    public String exportFinishedProjectsAsXml(){

        List<ProjectDto> projectDtos = getFinishedProjects();

        return this.xmlParser.exportXml(new ProjectRootDto(projectDtos), ProjectRootDto.class);
    }

    @Override
    public String exportFinishedProjectsAsText(){

        StringBuilder sb = new StringBuilder();

        getFinishedProjects()
                .forEach(p ->
                        sb.append("Project Name: ").append(p.getName())
                                .append("\n\tDescription: ").append(p.getDescription())
                                .append("\n\tPayment: ").append(p.getPayment())
                                .append("\n")
                );

        return sb.toString();
    }

    @Override
    public List<ProjectDto> getFinishedProjects() {

        List<Project> projects = this.projectRepository.findAllByFinished(true);

        return projects.stream()
                .map(p -> this.mapper.map(p, ProjectDto.class))
                .collect(Collectors.toList());
    }
}
