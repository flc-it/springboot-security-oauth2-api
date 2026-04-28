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

package org.flcit.springboot.security.oauth2.api.properties.keycloak;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class KeycloakOAuth2ResourceServerProperties {

    public static final String DEFAULT_PREFIX_AUTHORITY_REALM = "REALM_";

    private String resourceId;
    private String prefixAuthorityScope = "SCOPE_";
    private String prefixAuthorityResource = "ROLE_";
    private String prefixAuthorityRealm = DEFAULT_PREFIX_AUTHORITY_REALM;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPrefixAuthorityScope() {
        return prefixAuthorityScope;
    }

    public void setPrefixAuthorityScope(String prefixAuthorityScope) {
        this.prefixAuthorityScope = prefixAuthorityScope;
    }

    public String getPrefixAuthorityResource() {
        return prefixAuthorityResource;
    }

    public void setPrefixAuthorityResource(String prefixAuthorityResource) {
        this.prefixAuthorityResource = prefixAuthorityResource;
    }

    public String getPrefixAuthorityRealm() {
        return prefixAuthorityRealm;
    }

    public void setPrefixAuthorityRealm(String prefixAuthorityRealm) {
        this.prefixAuthorityRealm = prefixAuthorityRealm;
    }

}
