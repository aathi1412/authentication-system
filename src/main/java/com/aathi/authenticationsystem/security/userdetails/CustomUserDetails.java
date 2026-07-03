package com.aathi.authenticationsystem.security.userdetails;

import com.aathi.authenticationsystem.entity.Role;
import com.aathi.authenticationsystem.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }


    @Override
    public boolean isAccountNonLocked() {
        return !user.isAccountLocked();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public long getId(){
        return user.getId();
    }

    public Role getRole(){
        return user.getRole();
    }

    public String getName(){
        return user.getName();
    }
}
