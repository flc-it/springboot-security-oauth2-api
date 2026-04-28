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

package org.flcit.springboot.security.oauth2.api.configuration.keycloak;

import org.flcit.springboot.security.oauth2.api.configuration.WebSecurityConfiguration;
import org.flcit.springboot.security.oauth2.api.domain.DefaultAuthorityRole;
import org.flcit.springboot.security.oauth2.api.properties.keycloak.KeycloakOAuth2ResourceServerProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class DefaultKeycloakWebSecurityConfiguration implements WebSecurityConfiguration {

    @Override
    public GrantedAuthority[] getAuthoritiesAdmin() {
        return new GrantedAuthority[] { new SimpleGrantedAuthority(KeycloakOAuth2ResourceServerProperties.DEFAULT_PREFIX_AUTHORITY_REALM + "ADMIN") };
    }

    @Override
    public GrantedAuthority[] getAuthoritiesApi() {
        return new GrantedAuthority[] { DefaultAuthorityRole.ACCESS };
    }

}
