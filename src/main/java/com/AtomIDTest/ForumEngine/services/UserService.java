package com.AtomIDTest.ForumEngine.services;

import com.AtomIDTest.ForumEngine.models.Role;
import com.AtomIDTest.ForumEngine.models.User;
import com.AtomIDTest.ForumEngine.repositories.UserRepository;
import com.AtomIDTest.ForumEngine.util.InvalidRegistrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }


    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new InvalidRegistrationException("User with the same name already exists");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new InvalidRegistrationException("user with the same email already exists");
        }

        return save(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));

    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        System.out.println(getCurrentUser().toString());
        user.setRole(Role.ROLE_ADMIN);
        System.out.println(user);
        save(user);
    }


}
