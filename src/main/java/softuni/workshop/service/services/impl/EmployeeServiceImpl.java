package softuni.workshop.service.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.workshop.data.entities.Company;
import softuni.workshop.data.entities.Employee;
import softuni.workshop.data.entities.Project;
import softuni.workshop.data.repositories.CompanyRepository;
import softuni.workshop.data.repositories.EmployeeRepository;
import softuni.workshop.data.repositories.ProjectRepository;
import softuni.workshop.exceptions.CustomXmlException;
import softuni.workshop.exceptions.EntityNotFoundException;
import softuni.workshop.service.dtos.EmployeeDto;
import softuni.workshop.service.dtos.EmployeeRootDto;
import softuni.workshop.service.dtos.ProjectDto;
import softuni.workshop.service.services.EmployeeService;
import softuni.workshop.util.XmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final static String EMPLOYEES_PATH = "src/main/resources/files/xmls/employees.xml";
    private final XmlParser xmlParser;
    private final ModelMapper mapper;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public EmployeeServiceImpl(XmlParser xmlParser, ModelMapper mapper, EmployeeRepository employeeRepository, CompanyRepository companyRepository, ProjectRepository projectRepository) {
        this.xmlParser = xmlParser;
        this.mapper = mapper;
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public void importEmployees() {
        EmployeeRootDto employeeRootDto = this.xmlParser.parseXml(EmployeeRootDto.class, EMPLOYEES_PATH);

        for (EmployeeDto employeeDto : employeeRootDto.getEmployeeDtoList()) {

            Employee employee = this.mapper.map(employeeDto, Employee.class);

            Company company = this.companyRepository.findByName(employee.getProject().getCompany().getName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Company with name: %s not found !", employee.getProject().getCompany().getName())
                    ));

            Project project = this.projectRepository.findByNameAndStartDateAndCompany(
                    employee.getProject().getName(),
                    employee.getProject().getStartDate(),
                    company
                    )
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Project with name: %s not found !", employee.getProject().getName())
                    ));

            employee.setProject(project);

            this.employeeRepository.saveAndFlush(employee);
        }
    }

    @Override
    public boolean areImported() {

       return this.employeeRepository.count() > 0;
    }

    @Override
    public String readEmployeesXmlFile() {
        try {

            return String.join("\n", Files.readAllLines(Path.of(EMPLOYEES_PATH)));

        } catch (IOException e) {

            throw new CustomXmlException(e.getMessage(), e);
        }
    }

    @Override
    public String exportEmployeesWithAgeAboveAsXml(int age) {

        List<EmployeeDto> employeeDtos = getEmployeesWithAgeAbove(age);

        return this.xmlParser.exportXml(new EmployeeRootDto(employeeDtos), EmployeeRootDto.class);
    }

    @Override
    public String exportEmployeesWithAgeAboveAsText(int age) {
        StringBuilder sb = new StringBuilder();

        getEmployeesWithAgeAbove(age)
                .forEach(e ->
                        sb.append("Employee Name: ").append(e.getFirstName()).append(" ").append(e.getLastName())
                                .append("\n\tAge: ").append(e.getAge())
                                .append("\n\tProject name: ").append(e.getProject().getName())
                                .append("\n")
                );

        return sb.toString();
    }

    @Override
    public List<EmployeeDto> getEmployeesWithAgeAbove(int age) {
        List<Employee> employees = this.employeeRepository.findAllByAgeGreaterThan(age);

        return employees.stream()
                .map(e -> this.mapper.map(e, EmployeeDto.class))
                .collect(Collectors.toList());
    }
}
