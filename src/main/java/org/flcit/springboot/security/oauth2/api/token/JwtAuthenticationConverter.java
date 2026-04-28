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

package org.flcit.springboot.security.oauth2.api.token;

import java.security.Principal;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class JwtAuthenticationConverter implements JwtAuthenticationTokenConverter {

    private final Converter<Jwt, Principal> jwtPrincipalConverter;
    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter;
    private Converter<Jwt, Object> jwtDetailsConverter;

    public JwtAuthenticationConverter(Converter<Jwt, Principal> jwtPrincipalConverter, Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter) {
        this(jwtPrincipalConverter, grantedAuthoritiesConverter, null);
    }

    public JwtAuthenticationConverter(Converter<Jwt, Principal> jwtPrincipalConverter, Converter<Jwt, Collection<GrantedAuthority>> grantedAuthoritiesConverter, Converter<Jwt, Object> jwtDetailsConverter) {
        Assert.notNull(jwtPrincipalConverter, "jwtPrincipalConverter cannot be null");
        Assert.notNull(grantedAuthoritiesConverter, "grantedAuthoritiesConverter cannot be null");
        this.jwtPrincipalConverter = jwtPrincipalConverter;
        this.jwtGrantedAuthoritiesConverter = grantedAuthoritiesConverter;
        this.jwtDetailsConverter = jwtDetailsConverter;
    }

    @Override
    public final AbstractAuthenticationToken convert(Jwt jwt) {
        final OAuth2TokenAuthenticationToken authentication = new OAuth2TokenAuthenticationToken(
                jwt,
                this.jwtPrincipalConverter.convert(jwt),
                this.jwtGrantedAuthoritiesConverter.convert(jwt));
        if (jwtDetailsConverter != null) {
            authentication.setDetails(authentication);
        }
        authentication.setAuthenticated(true);
        return authentication;
    }

    public JwtAuthenticationConverter detailsConverter(Converter<Jwt, Object> jwtDetailsConverter) {
        this.jwtDetailsConverter = jwtDetailsConverter;
        return this;
    }

}
