package ru.netology.cloud_drive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.netology.cloud_drive.CloudDriveApplication;
import ru.netology.cloud_drive.component.JwtTokenUtil;
import ru.netology.cloud_drive.exception.ErrorBadCredentials;
import ru.netology.cloud_drive.exception.ErrorUnauthorized;
import ru.netology.cloud_drive.model.AuthRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CloudAuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService jwtUserDetailsService;

    public Map<String, String> tokenRepository = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudDriveApplication.class);

    @Autowired
    public CloudAuthenticationService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService jwtUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    public CloudAuthenticationService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService jwtUserDetailsService, Map<String, String> tokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.tokenRepository = tokenRepository;
    }

    public String createAuthenticationToken(AuthRequest authRequest) throws Exception {
        String username = authRequest.getLogin();
        authenticate(username, authRequest.getPassword());
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);
        LOGGER.info("User {} authorizated in CloudDrive. JWT: {}", username, token);
        tokenRepository.put(token, username);
        return token;
    }

    public Boolean authenticate(String username, String password) {
        Boolean result = false;
        try {
            var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (authentication != null) {
                result = true;
            }
        } catch (DisabledException e) {
            LOGGER.error("Unauthorized error");
            throw new ErrorUnauthorized("Unauthorized error");
        } catch (BadCredentialsException e) {
            LOGGER.error("Bad Credentials");
            throw new ErrorBadCredentials("Bad Credentials");
        } finally {
            return result;
        }
    }

    public Boolean removeToken(String authToken) {
        String token = authToken.substring(7);
        String username = tokenRepository.get(token);
        LOGGER.info("User {} logged out of CloudDrive. JWT is disabled.", username);
        return tokenRepository.remove(token) != null;
    }

    public String getUsernameByTokenFromMap(String authToken) {
        return tokenRepository.get(authToken.substring(7));
    }

}
