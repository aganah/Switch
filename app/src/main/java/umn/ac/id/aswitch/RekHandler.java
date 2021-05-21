package umn.ac.id.aswitch;

public class RekHandler {
    Double saldo;
    String username;

    public RekHandler() {}

    public RekHandler(Double saldo, String username) {
        this.saldo = saldo;
        this.username = username;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
