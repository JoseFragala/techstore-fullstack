package com.fragala.techstore.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fragala.techstore.entity.Role;
import com.fragala.techstore.repository.RoleRepository;

/**
 * Initializes fixed system roles required for the application to function.
 *
 * <p>This component exists because some data belongs to the application's core configuration
 * rather than to business fixtures. Roles such as ADMIN, CUSTOMER, and SELLER are part of the
 * authorization model, so the application should guarantee they exist at startup.
 *
 * <p>Architecturally, this is an infrastructure/bootstrap component. It runs after Spring Boot
 * finishes creating the application context and uses the repository layer to create only missing
 * records in an idempotent way.
 *
 * <p>It intentionally does not create sample users, products, orders, or any other fake business
 * data. Its responsibility is limited to mandatory system data.
 */
@Component
public class RoleDataInitializer implements ApplicationRunner {

    // This list centralizes the fixed roles so startup logic stays easy to read and maintain.
    private static final List<Role> DEFAULT_ROLES = List.of(
        new Role("ADMIN", "Administrator role with access to management and configuration features."),
        new Role("CUSTOMER", "Default role for shoppers who browse products and place orders."),
        new Role("SELLER", "Seller role responsible for managing catalog and sales operations.")
    );

    private final RoleRepository roleRepository;

    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Creates default roles that are missing from the database.
     *
     * <p>The method is transactional so all role inserts happen consistently in one startup unit
     * of work. Each role is checked by name before insertion, which makes repeated application
     * starts safe and prevents duplicate records.
     *
     * @param args command-line arguments provided by Spring Boot at startup
     */
    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Role defaultRole : DEFAULT_ROLES) {
            if (!roleRepository.existsByName(defaultRole.getName())) {
                roleRepository.save(new Role(defaultRole.getName(), defaultRole.getDescription()));
            }
        }
    }
}
