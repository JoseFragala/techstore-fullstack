package com.fragala.techstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fragala.techstore.dto.request.CreateAddressRequest;
import com.fragala.techstore.entity.Address;
import com.fragala.techstore.entity.User;
import com.fragala.techstore.mapper.AddressMapper;
import com.fragala.techstore.repository.AddressRepository;
import com.fragala.techstore.repository.RoleRepository;
import com.fragala.techstore.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
class AddressServiceIntegrationTest {

    @Autowired
    private AddressService addressService;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactions;
    private Long userId;
    private Long originalAddressId;

    @BeforeEach
    void setUp() {
        transactions = new TransactionTemplate(transactionManager);
        // Commit the fixture before invoking the service's own transaction.
        transactions.executeWithoutResult(status -> {
            User user = new User();
            user.setName("Address integration user");
            user.changeEmail("address-" + UUID.randomUUID() + "@example.com");
            user.changePassword("integration-test-placeholder");
            user.changePhone("11999999999");
            user.changeRole(roleRepository.findAll().stream()
                .filter(role -> role.getName().equals("CUSTOMER"))
                .findFirst().orElseThrow());
            user = userRepository.saveAndFlush(user);
            userId = user.getId();

            Address original = addressMapper.toEntity(request("Home", "Main Street"));
            original.setUser(user);
            originalAddressId = addressRepository.saveAndFlush(original).getId();
        });
    }

    @AfterEach
    void cleanUp() {
        if (userId == null) {
            return;
        }
        transactions.executeWithoutResult(status -> {
            // Delete only this test's records, preserving system roles and other users.
            addressRepository.deleteAll(addressRepository.findAll().stream()
                .filter(address -> address.getUser().getId().equals(userId)).toList());
            addressRepository.flush();
            userRepository.deleteById(userId);
        });
    }

    @Test
    void create_shouldRestorePreviousDefault_whenNewAddressPersistenceFails() {
        // The missing street violates the entity's non-null persistence constraint.
        assertThrows(DataIntegrityViolationException.class,
            () -> addressService.create(request("Work", null)));

        // Reload after rollback in a separate persistence context.
        transactions.executeWithoutResult(status -> {
            Address original = addressRepository.findById(originalAddressId).orElseThrow();
            assertTrue(original.isDefaultAddress());
            assertEquals(originalAddressId,
                addressRepository.findByUser_IdAndDefaultAddressTrue(userId)
                    .orElseThrow().getId());
            assertEquals(1L, addressCount());
        });
    }

    @Test
    void create_shouldReplacePreviousDefault_whenNewAddressIsValid() {
        Long newAddressId = addressService.create(request("Work", "Second Street")).getId();

        transactions.executeWithoutResult(status -> {
            assertFalse(addressRepository.findById(originalAddressId)
                .orElseThrow().isDefaultAddress());
            assertEquals(newAddressId,
                addressRepository.findByUser_IdAndDefaultAddressTrue(userId)
                    .orElseThrow().getId());
            assertEquals(2L, addressCount());
        });
    }

    private long addressCount() {
        return addressRepository.findAll().stream()
            .filter(address -> address.getUser().getId().equals(userId)).count();
    }

    private CreateAddressRequest request(String name, String street) {
        return new CreateAddressRequest(name, street, "100", null,
            "Downtown", "Sao Paulo", "SP", "01001000", "Brazil", true, userId);
    }
}
