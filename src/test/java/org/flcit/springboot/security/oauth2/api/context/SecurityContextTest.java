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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.flcit.springboot.commons.test.MockitoBaseTest;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
class SecurityContextTest implements MockitoBaseTest {

    private static final String USERNAME = "USERNAME";

    @Mock
    private org.springframework.security.core.context.SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Principal principal;

    @BeforeEach
    void init() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUserTests() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThrows(NotAuthenticatedException.class, SecurityContext::getCurrentUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        assertThrows(NotAuthenticatedException.class, SecurityContext::getCurrentUser);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(null);
        assertThrows(NotAuthenticatedException.class, SecurityContext::getCurrentUser);
        when(authentication.getPrincipal()).thenReturn(principal);
        assertEquals(principal, SecurityContext.getCurrentUser());
    }

    @Test
    void getCurrentNameTest() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThrows(NotAuthenticatedException.class, SecurityContext::getCurrentName);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        assertThrows(NotAuthenticatedException.class, SecurityContext::getCurrentName);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(null);
        assertNull(SecurityContext.getCurrentName());
        when(authentication.getPrincipal()).thenReturn(USERNAME);
        assertEquals(USERNAME, SecurityContext.getCurrentName());
        when(authentication.getPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn(USERNAME);
        assertEquals(USERNAME, SecurityContext.getCurrentName());
    }

}
