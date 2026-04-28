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

package org.flcit.springboot.security.oauth2.api.converter.keycloak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.flcit.springboot.security.oauth2.api.converter.JwtGrantedAuthorityConverter;
import org.flcit.springboot.security.oauth2.api.properties.keycloak.KeycloakOAuth2ResourceServerProperties;
import org.flcit.springboot.security.oauth2.api.util.JwtUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class KeycloakJwtGrantedAuthorityConverter implements JwtGrantedAuthorityConverter {

    private final KeycloakOAuth2ResourceServerProperties properties;

    public KeycloakJwtGrantedAuthorityConverter(KeycloakOAuth2ResourceServerProperties properties) {
        Assert.notNull(properties, "properties cannot be null");
        this.properties = properties;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        addAuthorities(grantedAuthorities, getScope(jwt), properties.getPrefixAuthorityScope());
        addAuthorities(grantedAuthorities, getRealmAccess(jwt), properties.getPrefixAuthorityRealm());
        addAuthorities(grantedAuthorities, getResourceAccess(jwt, properties.getResourceId()), properties.getPrefixAuthorityResource());
        return grantedAuthorities;
    }

    private static final void addAuthorities(final Collection<GrantedAuthority> grantedAuthorities, List<String> authorities, String prefix) {
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(prefix(authority, prefix)));
        }
    }

    private static final String prefix(String authority, String prefix) {
        return StringUtils.hasLength(prefix) ? prefix + authority : authority;
    }

    private static final List<String> getScope(Jwt jwt) {
        final String scope = jwt.getClaimAsString("scope");
        return StringUtils.hasLength(scope) ? List.of(scope.split(" ")) : null;
    }

    private static final List<String> getRealmAccess(Jwt jwt) {
        Map<String, Object> resources = jwt.getClaimAsMap("realm_access");
        return resources == null ? null : JwtUtils.castToCollection(resources.get("roles"));
    }

    private static final List<String> getResourceAccess(Jwt jwt, String resourceId) {
        Map<String, Object> resources = jwt.getClaimAsMap("resource_access");
        if (resources == null) {
            return Collections.emptyList();
        }
        resources = JwtUtils.castToMap(resources.get(resourceId));
        return resources == null ? null : JwtUtils.castToCollection(resources.get("roles"));
    }

}
