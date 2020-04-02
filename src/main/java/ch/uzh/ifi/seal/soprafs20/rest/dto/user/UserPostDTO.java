package ch.uzh.ifi.seal.soprafs20.rest.dto.user;

public class UserPostDTO {

    private String name;

    private String username;

    private String password;

    private String birthDate;

    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {return birthDate;}

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getToken() {return token;}

    public void setToken(String token) {
        this.token = token;
    }

}
