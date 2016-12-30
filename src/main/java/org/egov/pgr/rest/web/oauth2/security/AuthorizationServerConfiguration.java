package org.egov.pgr.rest.web.oauth2.security;

import java.util.Arrays;

import org.egov.pgr.rest.web.oauth2.provider.CustomAuthenticationProvider;
import org.egov.pgr.rest.web.oauth2.provider.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private static String REALM = "PGR_REST_OAUTH_REALM";

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    /*
     * @Autowired
     * @Qualifier("authenticationManagerBean") private AuthenticationManager authenticationManager;
     */

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(customAuthenticationProvider));
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient("pgr-mobile-client")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit", "client_credentials")
                .authorities("ROLE_APP", "ROLE_CITIZEN", "ROLE_ADMIN", "ROLE_EMPLOYEE")
                .scopes("read", "write")
                .secret("pgr-mobile-secret");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager())
                .pathMapping("/oauth/token", "/users/_login")
                .tokenEnhancer(customTokenEnhancer);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.realm(REALM + "/client");
    }

}