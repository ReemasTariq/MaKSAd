package maksadpro;

import java.util.List;
import java.util.regex.Pattern;

public class SignUpData {

    private String fullName;
    private String email;
    private String phone;
    private String password;

    private String dateOfBirth;
    private String gender;

    private String preferredType;

    private List<String> interests;
    private List<String> skills;   

    private final String role = "Volunteer"; 

    public SignUpData() {}

   
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getPreferredType() { return preferredType; }
    public List<String> getInterests() { return interests; }
    public List<String> getSkills() { return skills; }
    public String getRole() { return role; }

    
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPreferredType(String preferredType) { this.preferredType = preferredType; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    
    public void validate() {

      
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (!fullName.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("Full name must contain letters only.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email must contain '@' and a valid domain.");
        }

        if (phone == null || !phone.matches("^05\\d{8}$")) {
            throw new IllegalArgumentException("Phone number must start with 05 and be 10 digits.");
        }

      
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }

     
        if (dateOfBirth == null ||
                !Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", dateOfBirth)) {
            throw new IllegalArgumentException("Date of birth must be in format YYYY-MM-DD.");
        }

       
        if (gender == null ||
                !(gender.equalsIgnoreCase("Male") ||
                  gender.equalsIgnoreCase("Female"))) {
            throw new IllegalArgumentException("Gender must be Male or Female.");
        }

      
        if (preferredType == null || preferredType.trim().isEmpty()) {
            throw new IllegalArgumentException("Preferred type must be selected.");
        }

       
    }

    @Override
    public String toString() {
        return "SignUpData{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", preferredType='" + preferredType + '\'' +
                ", interests=" + interests +
                ", skills=" + skills +
                ", role='" + role + '\'' +
                '}';
    }
}
