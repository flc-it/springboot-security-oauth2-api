# springboot-security-keycloak-api

## Présentation
Le projet *springboot-security-oauth2-api* est la librairie pour la sécurisation d'une API via un Serveur d'Autorisation Oauth2 (mode bearer-only).

## Fonctionnalités

- Sécurise l'API via un Serveur d'Autorisation en mode bearer-only : **org.flcit.springboot.security.oauth2.api.properties.Oauth2ResourceServerProperties**
- Customisation du mapping entre le token et le Context de Sécurité Spring Boot : Package **org.flcit.springboot.security.oauth2.api.converter.** & Classe **org.flcit.springboot.security.oauth2.api.token.JwtAuthenticationTokenConverter**

## Frameworks
- [Spring boot](https://spring.io/projects/spring-boot) [@3.5.14](https://docs.spring.io/spring-boot/3.5/index.html)
    - [Spring OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)

## Security Configuration

### La configuration de la securité s'effectue via 2 properties obligatoires
Url de base du serveur d'authorisation :
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://keycloak/realms/apis
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/certs
```

### Configuration pour un Serveur d'Auhtorisation Keycvloak
Nom de la Ressource ID dans le Serveur d'Authorization :
```properties
spring.security.oauth2.resourceserver.jwt.provider=keycloak
spring.security.oauth2.resourceserver.jwt.keycloak.resource-id=@project.parent.artifactId@
```

### et 3 dernières properties optionnelles
```properties
prefix-authority-scope=SCOPE_
prefix-authority-resource=
prefix-authority-realm=REALM_
```

### Bean de configuration Optionnel à fournir
**org.flcit.springboot.security.oauth2.api.configuration.WebSecurityConfiguration**

- L'implémentation de la méthode fournissant la liste des rôles de l'API :
```java
public abstract String[] getRolesApi();
```
L'accès de tous les endpoints est automatiquement protégé par au moins l'un de ces Rôles, pour les définir sur chaque controller / endpoint il faut utiliser l'annotation [@RolesAllowed](https://docs.oracle.com/javaee/7/api/javax/annotation/security/RolesAllowed.html).
- La méthode fournissant la liste des Rôles Admin doit aussi être implentéee (Ces rôles protègent les endpoints actuator, hors health et info qui sont public) :
```java
public String[] getRolesAdmin() {
    return new String[] { Role.ADMIN.toString() };
}
```

### Exemple d'implémentation

Implémentation de la classe de sécurité :
```java
@Configuration
public class WebSecurityConfiguration extends org.flcit.springboot.security.oauth2.api.configuration.WebSecurityConfiguration {

    @Override
    public String[] getRolesApi() {
        return Utils.toStringArray(RoleApi.values());
    }

    @Override
    public String[] getRolesAdmin() {
        return Utils.toStringArray(RoleAdmin.values());
    }

}
```

En utilisant un Enum comme liste de rôles applicatifs :
```java
public enum RoleApi {

    FLC_SEARCH_PERSON,
    FLC_READ_CONTRACT;

}
```

## Génération des docs OpenAPI et sécurisation
Gère la librairie de génération de la documentation OpenAPI 3 et son UI avec Swagger-ui :
- [Springdoc-openapi](https://springdoc.org)

Important :  
Les endpoint OpenAPI et Swagger-ui (**/v3/docs/** + **/swagger-ui.html**) sont ouvert uniquement en environement hors prd ET si un Bean OpenAPI est détecté.