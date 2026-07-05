package com.aathi.authenticationsystem.security.userdetails;

import com.aathi.authenticationsystem.enums.Role;
import com.aathi.authenticationsystem.entity.User;
import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record CustomUserDetails(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + getRole().name()));
        Set<SimpleGrantedAuthority> permissionsAuthorities = getRole().getPermissions().stream()
                .map(permissions -> new SimpleGrantedAuthority(permissions.name()))
                .collect(Collectors.toSet());

        authorities.addAll(permissionsAuthorities);

        return authorities;
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

    public long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }

    public String getName() {
        return user.getName();
    }
}
