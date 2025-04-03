package com.example.JWTlogin.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUser extends BaseModel implements UserDetails {
    String Name;
    String Passwordd; 


    @Column(name = "email",unique = true)
    String email;
    @Enumerated(EnumType.STRING)
    Role AppUserRole;
    @Column(name = "PhoneNo", unique = true)
    Integer PhoneNo;
    @OneToMany
    List<Ticket> Tickets;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(AppUserRole.name());
        return Collections.singleton(authority);
    }

    @Override
    public String getPassword() {
        return this.Passwordd;
    }

    @Override
    public String getUsername() {
        return this.email;
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
}
