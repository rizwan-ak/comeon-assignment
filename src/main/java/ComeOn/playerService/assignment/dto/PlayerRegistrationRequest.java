package comeon.playerservice.assignment.dto;

import java.time.LocalDate;

public class PlayerRegistrationRequest {
    public String email;
    public String password;
    public String name;
    public String surname;
    public LocalDate dateOfBirth;
    public String address;
}
