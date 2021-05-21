package umn.ac.id.aswitch;

public class RegisHandler {
    String username, password, email, telp;

    public RegisHandler() {
    }

    public RegisHandler(String username, String password, String email, String telp) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.telp = telp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelp() {
        return telp;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }
}
