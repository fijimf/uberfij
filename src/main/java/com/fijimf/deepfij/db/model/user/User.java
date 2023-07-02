package com.fijimf.deepfij.db.model.user;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "`user`")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    private String email;
    private boolean locked;
    private boolean activated;
    @Column(name = "expire_credentials_at")
    private LocalDateTime expireCredentialsAt;// TIMESTAMP NULL

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    public User() {
    }

    public User(long id, String username, String password, String email, boolean activated) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.activated = activated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    /**
     * @return true as in this implementation accounts do not expire
     */
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return LocalDateTime.now().isBefore(expireCredentialsAt);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean isEnabled() {
        return isActivated() && !isLocked();
    }

    private boolean isLocked() {
        return locked;
    }

    public boolean isActivated() {
        return activated;
    }

    public Collection<? extends GrantedAuthority> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles == null ? null : roles.stream().map(a -> new SimpleGrantedAuthority("ROLE_" + a.getName())).collect(Collectors.toList());
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public LocalDateTime getExpireCredentialsAt() {
        return expireCredentialsAt;
    }

    public void setExpireCredentialsAt(LocalDateTime expireCredentialsAt) {
        this.expireCredentialsAt = expireCredentialsAt;
    }

    public void setRoles(List<Role> rs) {
        roles = rs;
    }

}
