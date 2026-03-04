package com.authservice.config;

import com.authservice.model.Role;
import com.authservice.model.RoleType;
import com.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Arrays.stream(RoleType.values()).forEach(type -> {
                Role role = new Role();
                role.setName(type);
                roleRepository.save(role);
            });
            log.info(" Roles seeded: INVESTOR, ADVISOR, ADMIN");
        } else {
            log.info("  Roles table already populated — skipping seed.");
        }
    }
}  