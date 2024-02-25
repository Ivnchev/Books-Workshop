package softuni.workshop.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.workshop.service.services.CompanyService;
import softuni.workshop.service.services.EmployeeService;
import softuni.workshop.service.services.ProjectService;

import java.io.IOException;

@Controller
@RequestMapping(path = "/import")
public class ImportController extends BaseController {

    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final CompanyService companyService;

    @Autowired
    public ImportController(ProjectService projectService, EmployeeService employeeService, CompanyService companyService) {
        this.projectService = projectService;
        this.employeeService = employeeService;
        this.companyService = companyService;
    }

    @GetMapping("/xml")
    public ModelAndView xml() {

//        if(areAllImported()) {
//            return this.redirect("/home");
//        }

        ModelAndView modelAndView = super.view("xml/import-xml");

        boolean[] areImported = new boolean[]{
                this.companyService.areImported(),
                this.projectService.areImported(),
                this.employeeService.areImported()
        };

        modelAndView.addObject("areImported", areImported);

        return modelAndView;
    }

    @GetMapping("/companies")
    public ModelAndView companies() throws IOException {

        ModelAndView view = super.view("xml/import-companies");

        view.addObject("companies", this.companyService.readCompaniesXmlFile());

        return view;
    }

    @PostMapping("/companies")
    public ModelAndView companiesConfirm() {

        this.companyService.importCompanies();

        return this.redirect(redirectPaths());
    }

    @GetMapping("/projects")
    public ModelAndView projects() {

        ModelAndView view = super.view("xml/import-projects");

        view.addObject("projects", this.projectService.readProjectsXmlFile());

        return view;
    }

    @PostMapping("/projects")
    public ModelAndView projectsConfirm() {

        this.projectService.importProjects();

        return this.redirect(redirectPaths());
    }

    @GetMapping("/employees")
    public ModelAndView employees() {

        ModelAndView view = super.view("xml/import-employees");

        view.addObject("employees", this.employeeService.readEmployeesXmlFile());

        return view;
    }

    @PostMapping("/employees")
    public ModelAndView employeesConfirm() {

        this.employeeService.importEmployees();

        return this.redirect(redirectPaths());
    }

    //utility methods
    private boolean areAllImported() {
        return this.employeeService.areImported() &&
                this.companyService.areImported() &&
                this.projectService.areImported();
    }

    private String redirectPaths() {

        return areAllImported() ? "/home" : "/import/xml";
    }
}
