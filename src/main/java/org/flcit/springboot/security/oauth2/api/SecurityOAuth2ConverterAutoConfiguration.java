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

import org.flcit.springboot.security.oauth2.api.converter.JwtDetailsConverter;
import org.flcit.springboot.security.oauth2.api.converter.JwtGrantedAuthorityConverter;
import org.flcit.springboot.security.oauth2.api.converter.JwtPrincipalConverter;
import org.flcit.springboot.security.oauth2.api.converter.JwtPrincipalNameConverter;
import org.flcit.springboot.security.oauth2.api.converter.keycloak.KeycloakJwtGrantedAuthorityConverter;
import org.flcit.springboot.security.oauth2.api.properties.Oauth2ResourceServerProperties;
import org.flcit.springboot.security.oauth2.api.token.JwtAuthenticationConverter;
import org.flcit.springboot.security.oauth2.api.token.JwtAuthenticationTokenConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@AutoConfiguration
@ConditionalOnMissingBean(JwtAuthenticationTokenConverter.class)
public class SecurityOAuth2ConverterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JwtPrincipalConverter.class)
    JwtPrincipalConverter defaultJwtPrincipalConverter(OAuth2ResourceServerProperties properties) {
        final JwtPrincipalNameConverter converter = new JwtPrincipalNameConverter();
        final PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(properties.getJwt().getPrincipalClaimName()).to(converter::setClaimName);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean(JwtGrantedAuthorityConverter.class)
    JwtGrantedAuthorityConverter defaultJwtGrantedAuthorityConverter(OAuth2ResourceServerProperties properties) {
        final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        final PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(properties.getJwt().getAuthorityPrefix()).to(grantedAuthoritiesConverter::setAuthorityPrefix);
        map.from(properties.getJwt().getAuthoritiesClaimDelimiter())
            .to(grantedAuthoritiesConverter::setAuthoritiesClaimDelimiter);
        map.from(properties.getJwt().getAuthoritiesClaimName())
            .to(grantedAuthoritiesConverter::setAuthoritiesClaimName);
        return grantedAuthoritiesConverter::convert;
    }   

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
    JwtAuthenticationConverter jwtAuthenticationConverter(JwtPrincipalConverter jwtPrincipalConverter, JwtGrantedAuthorityConverter jwtGrantedAuthorityConverter, @Nullable JwtDetailsConverter jwtDetailsConverter) {
        return new JwtAuthenticationConverter(jwtPrincipalConverter, jwtGrantedAuthorityConverter, jwtDetailsConverter);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(JwtGrantedAuthorityConverter.class)
    @ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.provider", havingValue = "keycloak")
    static class KeycloakAuthenticationTokenConverterConfiguration {
        @Bean
        @ConditionalOnMissingBean(JwtGrantedAuthorityConverter.class)
        JwtGrantedAuthorityConverter keycloakJwtGrantedAuthorityConverter(Oauth2ResourceServerProperties properties) {
            return new KeycloakJwtGrantedAuthorityConverter(properties.getKeycloak());
        }
    }

}
