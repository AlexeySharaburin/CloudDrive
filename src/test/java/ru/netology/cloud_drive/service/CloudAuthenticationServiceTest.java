
package ru.netology.cloud_drive.service;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.netology.cloud_drive.component.JwtTokenUtil;
import ru.netology.cloud_drive.model.AuthRequest;

import java.util.Collection;
import java.util.Map;

public class CloudAuthenticationServiceTest {

    AuthenticationManager authenticationManagerMock = Mockito.mock(AuthenticationManager.class);
    JwtTokenUtil jwtTokenUtilMock = Mockito.mock(JwtTokenUtil.class);
    JwtUserDetailsService jwtUserDetailsServiceMock = Mockito.mock(JwtUserDetailsService.class);

    Map<String, String> tokenRepositoryMock = Mockito.mock(Map.class);

    CloudAuthenticationService cloudAuthenticationService = new CloudAuthenticationService(
            authenticationManagerMock,
            jwtTokenUtilMock,
            jwtUserDetailsServiceMock,
            tokenRepositoryMock);

    String testUsername = "User";
    String testPassword = "111";
    AuthRequest testAuthRequest = new AuthRequest(testUsername, testPassword);

    String testAuthToken = "Bearer_testAuthToken";
    String testAuthTokenSplit = "testAuthToken";

    UsernamePasswordAuthenticationToken testUPAT = new UsernamePasswordAuthenticationToken(testUsername, testPassword);
//    UsernamePasswordAuthenticationToken [Principal=MyUserPrincipal(userData=UserData(id=1, username=Alexey, password=$2a$10$kDCPdypIhqCL72gqHLHmteUnK/7TEI4HMpvTIz4h684UaLWt65ijm, dataPath=cloud_drive_0000001, isEnable=true)), Credentials=[PROTECTED], Authenticated=true, Details=null, Granted Authorities=[]]

    UserDetails testUserDetails = new UserDetails() {
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public String getPassword() {
            return testPassword;
        }

        @Override
        public String getUsername() {
            return testUsername;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    };

    @Before
    public void mockCreate() {
        Mockito.when(jwtUserDetailsServiceMock.loadUserByUsername(testUsername))
                .thenReturn(testUserDetails);
        Mockito.when(jwtTokenUtilMock.generateToken(testUserDetails))
                .thenReturn(testAuthToken);
    }
    @Test
    void testCreateAuthenticationTokenCloudAuthenticationService() throws Exception {
        mockCreate();
        String result = cloudAuthenticationService.createAuthenticationToken(testAuthRequest);
        String expected = testAuthToken;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockAuth() {
        Mockito.when(authenticationManagerMock.authenticate(new UsernamePasswordAuthenticationToken(testUsername,testPassword)))
                .thenReturn(testUPAT);
    }
    @Test
    void testAuthenticateCloudAuthenticationService() {
        mockAuth();
        Boolean result = cloudAuthenticationService.authenticate(testUsername, testPassword);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockRemove() {
        Mockito.when(tokenRepositoryMock.remove(testAuthTokenSplit))
                .thenReturn(testUsername);
    }
    @Test
    void testRemoveTokenCloudAuthenticationService() {
        mockRemove();
        Boolean result = cloudAuthenticationService.removeToken(testAuthToken);
        Boolean expected = true;
        Assertions.assertEquals(expected, result);
    }

    @Before
    public void mockGet() {
        Mockito.when(tokenRepositoryMock.get(testAuthTokenSplit))
                .thenReturn(testUsername);
    }
    @Test
    void testGetUsernameByTokenFromMapCloudAuthenticationService() {
        mockGet();
        String result = cloudAuthenticationService.getUsernameByTokenFromMap(testAuthToken);
        String expected = testUsername;
        Assertions.assertEquals(expected, result);
    }
}

