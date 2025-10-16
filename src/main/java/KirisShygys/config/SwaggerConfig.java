package KirisShygys.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kiris Shyǵys API")
                        .version("1.0.0")
                        .description("Backend-сервис для управления финансами и финансовой грамотности.")
                        .contact(new Contact()
                                .name("Dilnaza Baidakhanova")
                                .email("dilnazbaidakhanova@gmail.com")
                                .url("https://github.com/dillnaza"))
                        .license(new License().name("MIT License")));
    }
}
