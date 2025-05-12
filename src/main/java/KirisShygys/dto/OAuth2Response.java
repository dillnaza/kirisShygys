package KirisShygys.dto;

public class OAuth2Response {
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;

    public OAuth2Response(String name, String email, String accessToken, String refreshToken) {
        this.name = name;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
