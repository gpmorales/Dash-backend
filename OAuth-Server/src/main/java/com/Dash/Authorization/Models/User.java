package com.Dash.Authorization.Models;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Collection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ActiveUsers")
public class User implements UserDetails {

    @Id
    private String Id;

    @NotNull
    @NotEmpty
    private String firstName;

    private String lastName;

    @NotNull
    @NotEmpty
    @Indexed(unique = true)
    private String email;

    @NotNull
    @NotEmpty
    private String password;
    private String phoneNumber;

    private boolean enabled;

    public String getId() { return Id; }

    /** UserDetails interface methods concretely implemented here */
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
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
        return enabled;
    }

    // @Data creates the getters and setters for this class's defined attributes
    // and a default no-arg constructor

    // @Builder creates a build pattern class that allows you
    // to create complex objects by chaining the fields where you define the attributes
    // ex : Person me = Person.builder().age(12).name("bob").phone(1234) ... etc
}
