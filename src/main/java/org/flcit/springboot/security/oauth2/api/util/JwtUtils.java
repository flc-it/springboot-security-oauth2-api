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

package org.flcit.springboot.security.oauth2.api.util;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class JwtUtils {

    private JwtUtils() { }

    public static final List<String> prefix(List<String> values, String prefix) {
        if (!StringUtils.hasLength(prefix)) {
            return values;
        }
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                values.set(i, prefix + values.get(i));
            }
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static final Map<String, Object> castToMap(Object authorities) {
        return (Map<String, Object>) authorities;
    }

    @SuppressWarnings("unchecked")
    public static final List<String> castToCollection(Object authorities) {
        return (List<String>) authorities;
    }

}
