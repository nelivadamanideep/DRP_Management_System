package com.erpms.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger UI wiring.
 *
 * <p>Swagger UI is served from <code>/api/swagger-ui.html</code>
 * and the raw JSON spec from <code>/api/v3/api-docs</code>.
 *
 * <p>Adds a <em>bearerAuth</em> security scheme so every protected endpoint
 * can be invoked from the Swagger UI once the operator pastes a JWT.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Enterprise Research Project Management System (ERPMS)",
                version = "1.0.0",
                description = """
                        Production-grade REST API powering the ERPMS platform.

                        Covers authentication, projects, milestones, tasks, teams, departments,
                        documents, equipment, inventory, procurement, budget, notifications,
                        audit logs, reporting and AI-assisted intelligence services.
                        """,
                contact = @Contact(name = "ERPMS Platform Team", email = "platform@erpms.local"),
                license = @License(name = "Proprietary")
        ),
        servers = {
                @Server(url = "/api", description = "Default (relative to gateway)")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
