package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class UserPostDTO {//new

    private String name;

    private String username;

    private String password;

    private String token;

    private Long userID;

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //I think this is needed to get the password, backend gets it from frontend
    public String getPassword() {return password;}

    //I don't think this is needed
    //public void setPassword(String password) {this.password = password;}

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public Long getUserID() {return userID;}

    public void setUserID(Long userID) {this.userID = userID;}
}
