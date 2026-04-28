/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flcit.springboot.security.oauth2.api;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

import org.flcit.springboot.commons.test.util.ContextRunnerUtils;
import org.flcit.springboot.commons.test.util.MvcUtils;
import org.flcit.springboot.commons.test.util.PropertyTestUtils;
import org.flcit.springboot.security.oauth2.api.configuration.DefaultWebSecurityConfiguration;
import org.flcit.springboot.security.oauth2.api.configuration.WebSecurityConfiguration;
import org.flcit.springboot.security.oauth2.api.converter.JwtPrincipalNameConverter;
import org.flcit.springboot.security.oauth2.api.domain.DefaultAuthorityAdmin;
import org.flcit.springboot.security.oauth2.api.token.JwtAuthenticationConverter;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
class SecurityKeycloakAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    HealthEndpointAutoConfiguration.class,
                    InfoEndpointAutoConfiguration.class,
                    EndpointAutoConfiguration.class,
                    WebEndpointAutoConfiguration.class,
                    SecurityAutoConfiguration.class,
                    WebMvcAutoConfiguration.class,
                    SecurityOAuth2ApiAutoConfiguration.class,
                    SecurityOAuth2ConverterAutoConfiguration.class));

    private static final GrantedAuthority[] ROLES_API = new GrantedAuthority[] { new SimpleGrantedAuthority("READ"), new SimpleGrantedAuthority("WRITE") };
    private static final GrantedAuthority[] ROLES_ADMIN = new GrantedAuthority[] { new SimpleGrantedAuthority("ADMIN") };

    private static final WebSecurityConfiguration SECURITY_CONFIGURATION = new WebSecurityConfiguration() {

        @Override
        public GrantedAuthority[] getAuthoritiesApi() {
            return ROLES_API;
        }

        @Override
        public GrantedAuthority[] getAuthoritiesAdmin() {
            return ROLES_ADMIN;
        }

    };

    private static final JwtDecoder JWT_DECODER = new JwtDecoder() {
        @Override
        public Jwt decode(String token) throws JwtException {
            return Jwt.withTokenValue(token).build();
        }
    };

    private static final JwtAuthenticationConverter JWT_AUTHENTICATION_CONVERTER = new JwtAuthenticationConverter(
            new Converter<Jwt, Principal>() {
                
                @Override
                public Principal convert(Jwt source) {
                    return new JwtPrincipalNameConverter().convert(source);
                }
            },
            new Converter<Jwt, Collection<GrantedAuthority>>() {
                
                @Override
                public Collection<GrantedAuthority> convert(Jwt source) {
                    return Collections.emptyList();
                }
            });

    private static final WebSecurityConfiguration SECURITY_CONFIGURATION_NO_ROLES_API = new WebSecurityConfiguration() {

        @Override
        public GrantedAuthority[] getAuthoritiesApi() {
            return new GrantedAuthority[] { };
        }

        @Override
        public GrantedAuthority[] getAuthoritiesAdmin() {
            return ROLES_ADMIN;
        }

    };

    private static final WebSecurityConfiguration SECURITY_CONFIGURATION_NO_ROLES_ADMIN = new WebSecurityConfiguration() {

        @Override
        public GrantedAuthority[] getAuthoritiesApi() {
            return ROLES_API;
        }

        @Override
        public GrantedAuthority[] getAuthoritiesAdmin() {
            return new GrantedAuthority[] { };
        }

    };

    private static final String[] ACTUATOR_PUBLIC_ENDPOINTS = new String[] {
            "/actuator/health",
            "/actuator/info"
    };
    private static final String[] ACTUATOR_ADMIN_ENDPOINTS = new String[] {
            "/actuator/env",
            "/actuator/beans",
            "/actuator/loggers",
            "/actuator/mappings",
            "/actuator/configprops",
            "/actuator/shutdown",
            "/actuator/metrics",
            "/actuator/threaddump",
            "/actuator/heapdump"
    };
    private static final String[] OPENAPI_ALL_ENDPOINTS = new String[] {
            "/v3/api-docs",
            "/swagger-ui.html",
            "/swagger-ui/"
    };

    @Test
    void defaultWebSecurityConfigurationBean() {
        ContextRunnerUtils.assertHasSingleBean(this.contextRunner
        .withBean(JwtDecoder.class, () -> JWT_DECODER)
        .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER), DefaultWebSecurityConfiguration.class);
    }

    @Test
    void webSecurityConfigurationBeanOk() {
        ContextRunnerUtils.assertHasSingleBean(
                this.contextRunner
                .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION)
                .withBean(JwtDecoder.class, () -> JWT_DECODER)
                .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER),
                SecurityFilterChain.class);
    }

    @Test
    void webSecurityConfigurationBeanNoRolesApi() {
        ContextRunnerUtils.assertHasFailed(
                this.contextRunner
                .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION_NO_ROLES_API)
                .withBean(JwtDecoder.class, () -> JWT_DECODER)
                .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        );
    }

    @Test
    void webSecurityConfigurationBeanNoRolesAdmin() {
        ContextRunnerUtils.assertHasFailed(
                this.contextRunner
                .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION_NO_ROLES_ADMIN)
                .withBean(JwtDecoder.class, () -> JWT_DECODER)
                .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        );
    }

    @Test
    void webSecurityConfigurationBeanActuatorPublic() {
        this.contextRunner
        .withPropertyValues("management.endpoints.web.exposure.include=*")
        .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION)
        .withBean(JwtDecoder.class, () -> JWT_DECODER)
        .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        .run(context ->
            MvcUtils.assertGetResponseStatusNoMvc(context, ACTUATOR_PUBLIC_ENDPOINTS)
        );
    }

    @Test
    void webSecurityConfigurationBeanActuatorAdminKO() {
        this.contextRunner
        .withPropertyValues("management.endpoints.web.exposure.include=*")
        .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION)
        .withBean(JwtDecoder.class, () -> JWT_DECODER)
        .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        .run(context ->
            MvcUtils.assertGetResponseStatusNoMvc(context, HttpStatus.UNAUTHORIZED, ACTUATOR_ADMIN_ENDPOINTS)
        );
    }

    @Test
    void webSecurityConfigurationBeanActuatorAdmin() {
        this.contextRunner
        .withPropertyValues("management.endpoints.web.exposure.include=*")
        .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION)
        .withBean(JwtDecoder.class, () -> JWT_DECODER)
        .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        .run(context ->
            MvcUtils.assertGetResponseStatus(context, new String[] { DefaultAuthorityAdmin.ADMIN.toString() }, HttpStatus.OK, ACTUATOR_ADMIN_ENDPOINTS)
        );
    }

    @Test
    void noOpenApiConfigurationBean() {
        this.contextRunner
        .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION)
        .withBean(JwtDecoder.class, () -> JWT_DECODER)
        .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        .run(context ->
            MvcUtils.assertGetResponseStatusNoMvc(context, HttpStatus.UNAUTHORIZED, OPENAPI_ALL_ENDPOINTS)
        );
    }

    @Test
    void openApiSimpleDocsConfigurationOnlyNoPrdBean() {
        this.contextRunner
        .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION)
        .withBean(JwtDecoder.class, () -> JWT_DECODER)
        .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        .withPropertyValues(PropertyTestUtils.getValue("server.exec", "environment", "prd"))
        .withBean(GroupedOpenApi.class, this::groupedOpenApi)
        .run(context ->
            MvcUtils.assertGetResponseStatusNoMvc(context, HttpStatus.UNAUTHORIZED, OPENAPI_ALL_ENDPOINTS)
        );
    }

    @Test
    void openApiSimpleDocsConfigurationDevBean() {
        this.contextRunner
        .withBean(WebSecurityConfiguration.class, () -> SECURITY_CONFIGURATION)
        .withBean(GroupedOpenApi.class, this::groupedOpenApi)
        .withBean(JwtDecoder.class, () -> JWT_DECODER)
        .withBean(JwtAuthenticationConverter.class, () -> JWT_AUTHENTICATION_CONVERTER)
        .withPropertyValues(PropertyTestUtils.getValue("server.exec", "environment", "dev"))
        .run(context -> {
            MvcUtils.assertGetResponseStatusNoMvc(context, OPENAPI_ALL_ENDPOINTS);
        });
    }

    private GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("API")
                .pathsToMatch("/api/**")
                .build();
    }

}
