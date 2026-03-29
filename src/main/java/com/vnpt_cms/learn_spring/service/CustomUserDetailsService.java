package com.vnpt_cms.learn_spring.service;

import com.vnpt_cms.learn_spring.entity.ScRole;
import com.vnpt_cms.learn_spring.entity.ScRolePermission;
import com.vnpt_cms.learn_spring.entity.ScUser;
import com.vnpt_cms.learn_spring.entity.ScUserRole;
import com.vnpt_cms.learn_spring.repository.ScUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ScUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ScUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(ScUser user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (ScUserRole userRole : user.getUserRoles()) {
            ScRole role = userRole.getRole();

            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            for (ScRolePermission rp : role.getRolePermissions()) {
                authorities.add(
                        new SimpleGrantedAuthority(rp.getPermission().getName())
                );
            }
        }

        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
