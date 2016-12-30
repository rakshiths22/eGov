/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.pgr.rest.web.oauth2.provider;

import java.util.ArrayList;
import java.util.List;

import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.config.security.authentication.SecureUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    /**
     * TO-Do:Need to remove this and provide authentication for web, based on authentication_code.
     */
    private final String WEB_CHEAT_PASSWORD = "Pgr-weB-pa$$word";

    @Override
    public Authentication authenticate(final Authentication authentication) {

        final String userName = authentication.getName();
        final String password = authentication.getCredentials().toString();
        User user;
        
        SecureUser su ;

        if (userName.contains("@") && userName.contains("."))
            user = userService.getUserByEmailId(userName);
        else
            user = userService.getUserByUsername(userName);
        if (user == null)
            throw new OAuth2Exception("Invalid login credentials");

        final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

        if (WEB_CHEAT_PASSWORD.equals(password) || bcrypt.matches(password, user.getPassword())) {

            if (!user.isActive())
                throw new OAuth2Exception("Please activate your account");
            /**
             * We assume that there will be only one type. If it is multimple then we have change below code Seperate by comma or
             * other and iterate
             */
            final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
			/*
			 * org.springframework.security.core.userdetails.User Springuser =
			 * new org.springframework.security.core.userdetails.User( userName,
			 * userName, authorities);
			 */
            	su = new SecureUser(user);

            final List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + user.getType()));
            return new UsernamePasswordAuthenticationToken(su, password, grantedAuths);
        } else
            throw new OAuth2Exception("Invalid login credentials");
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}