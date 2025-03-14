package KirisShygys.dto;

import jakarta.validation.constraints.NotBlank;

public class AccountRequest {

    @NotBlank(message = "Account name cannot be empty")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
