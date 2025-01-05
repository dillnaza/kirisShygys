package KirisShygys.dto;

public class OAuth2Response {
    private String name;
    private String email;

    public OAuth2Response(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
