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

package org.flcit.springboot.security.oauth2.api.context;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class SecurityContext {

    private SecurityContext() { }

    /**
     * @return
     * @throws NotAuthenticatedException
     */
    public static Principal getCurrentUser() throws NotAuthenticatedException {
        final Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
        if (authentification == null || !authentification.isAuthenticated()) {
            throw new NotAuthenticatedException("NOT AUTHENTICATED !!!");
        }
        if (authentification.getPrincipal() instanceof Principal principal) {
            return principal;
        }
        throw new NotAuthenticatedException("I DON'T KNOW THIS PRINCIPAL AUTHENTICATED !!!");
        
    }

    /**
     * @return
     * @throws NotAuthenticatedException
     */
    public static String getCurrentName() throws NotAuthenticatedException {
        final Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
        if (authentification == null || !authentification.isAuthenticated()) {
            throw new NotAuthenticatedException("NOT AUTHENTICATED !!!");
        }
        if (authentification.getPrincipal() == null) {
            return null;
        } else if (authentification.getPrincipal() instanceof Principal principal) {
            return principal.getName();
        }
        return authentification.getPrincipal() != null  ? authentification.getPrincipal().toString() : null;
    }

}
