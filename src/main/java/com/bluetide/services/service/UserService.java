package com.bluetide.services.service;

import com.bluetide.services.dto.PageResponse;
import com.bluetide.services.exception.ResourceNotFoundException;
import com.bluetide.services.models.User;
import com.bluetide.services.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public PageResponse<User> findAllPaged(int page, int size) {
        Page<User> result = userRepository.findAll(PageRequest.of(page, size));
        return PageResponse.<User>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findByCompanyId(String companyId) {
        return userRepository.findByCompanyId(companyId);
    }

    public User create(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public User update(String id, User userDetails) {
        User existingUser = getById(id);

        if (userDetails.getDisplayName() != null) {
            existingUser.setDisplayName(userDetails.getDisplayName());
        }
        if (userDetails.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        }
        if (userDetails.getProfileImageUrl() != null) {
            existingUser.setProfileImageUrl(userDetails.getProfileImageUrl());
        }
        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }
        if (userDetails.getCompanyId() != null) {
            existingUser.setCompanyId(userDetails.getCompanyId());
        }
        if (userDetails.getOwnedPropertyIds() != null) {
            existingUser.setOwnedPropertyIds(userDetails.getOwnedPropertyIds());
        }

        existingUser.setUpdatedAt(new Date());
        return userRepository.save(existingUser);
    }

    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    public void deactivate(String id) {
        User user = getById(id);
        user.setIsActive(false);
        user.setUpdatedAt(new Date());
        userRepository.save(user);
    }

    public void activate(String id) {
        User user = getById(id);
        user.setIsActive(true);
        user.setUpdatedAt(new Date());
        userRepository.save(user);
    }
}

