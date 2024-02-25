package softuni.workshop.service.models;

import java.util.Set;

public class UserServiceModel {
    private String username;
    private String password;
    private String email;
    private Set<RoleServiceModel> authorities;

}
