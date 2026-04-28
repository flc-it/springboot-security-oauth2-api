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

package org.flcit.springboot.security.oauth2.api.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.flcit.springboot.security.oauth2.api.domain.DefaultAuthorityRole;
import org.junit.jupiter.api.Test;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
class WebSecurityConfigurationTest {

    @Test
    void getAuthoritiesAdminTest() {
        assertEquals(DefaultAuthorityRole.ADMIN, new DefaultWebSecurityConfiguration().getAuthoritiesAdmin()[0]);
    }

    @Test
    void getAuthoritiesApiTest() {
        assertEquals(DefaultAuthorityRole.ACCESS, new DefaultWebSecurityConfiguration().getAuthoritiesApi()[0]);
    }

}
