package com.biniyogbuddy.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BiniyogBuddy API")
                        .description("Stock trading journal API for beginner investors in Bangladesh (DSE/CSE)")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer openApiExamplesCustomizer() {
        return new OpenApiExamplesCustomizer();
    }

    private static class OpenApiExamplesCustomizer implements OpenApiCustomizer {

        private static final Map<String, Map<String, Object>> SCHEMA_EXAMPLES = Map.of(
                "AuthRegisterRequest", Map.of(
                        "email", "mdrajinmashrursiam@gmail.com",
                        "password", "iamsiam5000",
                        "username", "rajin_siam",
                        "fullName", "Md. Rajin Mashrur Siam"
                ),
                "AuthLoginRequest", Map.of(
                        "email", "mdrajinmashrursiam@gmail.com",
                        "password", "iamsiam5000"
                ),
                "RefreshTokenRequest", Map.of(
                        "refreshToken", ""
                ),
                "LogoutRequest", Map.of(
                        "refreshToken", ""
                ),
                "StockJournalRequest", Map.ofEntries(
                        Map.entry("stockName", "Square Pharmaceuticals Ltd."),
                        Map.entry("dseCode", "SQURPHARMA"),
                        Map.entry("cseCode", "SQURPHARMA"),
                        Map.entry("sector", "PHARMACEUTICALS"),
                        Map.entry("purchasePrice", 230.50),
                        Map.entry("quantity", 100),
                        Map.entry("tradeType", "LEARNING_ONLY")
                )
        );

        @Override
        public void customise(OpenAPI openApi) {
            var schemas = openApi.getComponents().getSchemas();
            if (schemas == null) {
                return;
            }
            SCHEMA_EXAMPLES.forEach((schemaName, examples) -> {
                var schema = schemas.get(schemaName);
                if (schema != null && schema.getProperties() != null) {
                    examples.forEach((property, value) -> {
                        var propertySchema = (io.swagger.v3.oas.models.media.Schema<?>) schema.getProperties().get(property);
                        if (propertySchema != null) {
                            propertySchema.setExample(value);
                        }
                    });
                }
            });
        }
    }
}
