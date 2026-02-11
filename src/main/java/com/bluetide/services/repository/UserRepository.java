package com.bluetide.services.repository;

import com.bluetide.services.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByCompanyId(String companyId);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
