package ar.edu.utn.frro.web.rest;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frro.security.xauth.Token;
import ar.edu.utn.frro.security.xauth.TokenProvider;
import ar.edu.utn.frro.web.rest.dto.LoginDTO;

import com.codahale.metrics.annotation.Timed;

@RestController
@RequestMapping("/api")
public class UserXAuthTokenController {

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate",
            method = RequestMethod.POST)
    @Timed
    public Token authorize(@Valid @RequestBody LoginDTO loginDTO) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails details = this.userDetailsService.loadUserByUsername(loginDTO.getUsername());
        return tokenProvider.createToken(details);
    }
}
