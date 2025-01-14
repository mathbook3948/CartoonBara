package com.jjangtrio.project1_back.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Permission;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.UserRepository;

@Service
public class UserForLoginService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(username).get(0);
        Permission permission = permissionRepository.findById(user.getUserNum())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(PermissionRole.getRole(permission.getPermissionMaster())));
        authorities.add(new SimpleGrantedAuthority(UserRole.getRole(permission.getPermissionState())));

        UserForLogin userForLogin = new UserForLogin(user.getUserNum(), user.getUserNickname(), user.getUserPwd(),
                authorities);
        return userForLogin;
    }
}
