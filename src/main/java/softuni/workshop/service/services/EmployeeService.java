package softuni.workshop.service.services;

import softuni.workshop.service.dtos.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    void importEmployees();

    boolean areImported();

    String readEmployeesXmlFile();

    String exportEmployeesWithAgeAboveAsXml(int age);
    String exportEmployeesWithAgeAboveAsText(int age);
    List<EmployeeDto> getEmployeesWithAgeAbove(int age);
}
