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

package org.flcit.springboot.security.oauth2.api.converter;

import java.security.Principal;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.util.Assert;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class JwtPrincipalNameConverter implements JwtPrincipalConverter {

    private String claimName = JwtClaimNames.SUB;

    @Override
    public final Principal convert(Jwt jwt) {
        return () -> jwt.getClaimAsString(claimName);
    }

    public void setClaimName(String claimName) {
        Assert.hasText(claimName, "claimName cannot be empty");
        this.claimName = claimName;
    }

}
