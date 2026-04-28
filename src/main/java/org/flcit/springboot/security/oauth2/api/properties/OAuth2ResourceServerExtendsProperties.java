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

package org.flcit.springboot.security.oauth2.api.properties;

import org.flcit.springboot.security.oauth2.api.properties.keycloak.KeycloakOAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@ConfigurationProperties("spring.security.oauth2.resourceserver.jwt")
public class OAuth2ResourceServerExtendsProperties {

    private OAuth2ServerAuthorizationProvider provider;
    private KeycloakOAuth2ResourceServerProperties keycloak;

    public OAuth2ServerAuthorizationProvider getProvider() {
        return provider;
    }
    public void setProvider(OAuth2ServerAuthorizationProvider provider) {
        this.provider = provider;
    }
    public KeycloakOAuth2ResourceServerProperties getKeycloak() {
        return keycloak;
    }
    public void setKeycloak(KeycloakOAuth2ResourceServerProperties keycloak) {
        this.keycloak = keycloak;
    }

}
