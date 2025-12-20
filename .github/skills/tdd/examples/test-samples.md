# Test Samples - TDD Examples

This document provides sample test implementations following TDD best practices for Java Spring Boot applications.

---

## Unit Test Examples

### Example 1: Service Layer Test with Mocks

```java
package com.example.service;

import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    
    @Mock
    private EmailValidator emailValidator;

    @InjectMocks
    private UserService userService;

    private UserDTO validUserDTO;

    @BeforeEach
    void setUp() {
        validUserDTO = new UserDTO();
        validUserDTO.setEmail("user@example.com");
        validUserDTO.setName("John Doe");
    }

    @Test
    void shouldCreateUserWhenValidData() {
        // Given - Valid user data and mocked dependencies
        when(emailValidator.isValid(any())).thenReturn(true);
        when(repository.existsByEmail(any())).thenReturn(false);
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(validUserDTO.getEmail());
        when(repository.save(any(User.class))).thenReturn(savedUser);
        
        // When - Create user
        UserDTO result = userService.createUser(validUserDTO);
        
        // Then - Verify user was created
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("user@example.com");
        
        // Verify interactions
        verify(emailValidator).isValid("user@example.com");
        verify(repository).existsByEmail("user@example.com");
        verify(repository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailInvalid() {
        // Given - Invalid email
        when(emailValidator.isValid(any())).thenReturn(false);
        
        // When & Then - Should throw exception
        assertThatThrownBy(() -> userService.createUser(validUserDTO))
            .isInstanceOf(InvalidEmailException.class)
            .hasMessage("Invalid email format: user@example.com");
        
        // Verify repository was never called
        verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given - Email already exists
        when(emailValidator.isValid(any())).thenReturn(true);
        when(repository.existsByEmail(any())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(validUserDTO))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessage("Email already registered: user@example.com");
        
        verify(repository, never()).save(any());
    }

    @Test
    void shouldFindUserByIdWhenExists() {
        // Given - User exists
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        
        // When - Find user
        Optional<UserDTO> result = userService.findById(1L);
        
        // Then - User is found
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Given - User doesn't exist
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // When - Find non-existent user
        Optional<UserDTO> result = userService.findById(999L);
        
        // Then - Result is empty
        assertThat(result).isEmpty();
    }

    @Test
    void shouldCaptureArgumentWhenSaving() {
        // Given
        when(emailValidator.isValid(any())).thenReturn(true);
        when(repository.existsByEmail(any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        
        // When
        userService.createUser(validUserDTO);
        
        // Then - Verify exact user data saved
        verify(repository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("user@example.com");
        assertThat(savedUser.getName()).isEqualTo("John Doe");
    }
}
```

---

## Integration Test Examples

### Example 2: REST Controller Integration Test

```java
package com.example.controller;

import com.example.AbstractIntegrationTest;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() throws Exception {
        // When & Then - GET /api/users
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        // Given - User registration request
        String requestBody = """
            {
                "email": "newuser@example.com",
                "name": "New User",
                "password": "SecurePass123!"
            }
            """;
        
        // When & Then - POST /api/users
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.name").value("New User"))
            .andExpect(jsonPath("$.password").doesNotExist()); // Password should not be returned
    }

    @Test
    void shouldReturnUserWhenIdExists() throws Exception {
        // Given - Existing user in database
        User user = new User();
        user.setEmail("existing@example.com");
        user.setName("Existing User");
        User saved = userRepository.save(user);
        
        // When & Then - GET /api/users/{id}
        mockMvc.perform(get("/api/users/" + saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(saved.getId()))
            .andExpect(jsonPath("$.email").value("existing@example.com"))
            .andExpect(jsonPath("$.name").value("Existing User"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        // When & Then - GET non-existent user
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given - Existing user
        User user = new User();
        user.setEmail("old@example.com");
        user.setName("Old Name");
        User saved = userRepository.save(user);
        
        String updateBody = """
            {
                "name": "Updated Name"
            }
            """;
        
        // When & Then - PUT /api/users/{id}
        mockMvc.perform(put("/api/users/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.email").value("old@example.com")); // Email unchanged
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given - Existing user
        User user = new User();
        user.setEmail("todelete@example.com");
        User saved = userRepository.save(user);
        
        // When - DELETE /api/users/{id}
        mockMvc.perform(delete("/api/users/" + saved.getId()))
            .andExpect(status().isNoContent());
        
        // Then - User no longer exists
        mockMvc.perform(get("/api/users/" + saved.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateRequiredFields() throws Exception {
        // Given - Request missing required fields
        String invalidBody = """
            {
                "name": "Only Name"
            }
            """;
        
        // When & Then - Should return 400 Bad Request
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))))
            .andExpect(jsonPath("$.errors[*].field", hasItem("email")));
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        // Given - Existing user
        User existing = new User();
        existing.setEmail("duplicate@example.com");
        existing.setName("First User");
        userRepository.save(existing);
        
        String duplicateRequest = """
            {
                "email": "duplicate@example.com",
                "name": "Second User",
                "password": "Password123!"
            }
            """;
        
        // When & Then - Should reject duplicate
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateRequest))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void shouldFilterUsersByName() throws Exception {
        // Given - Multiple users
        User user1 = new User();
        user1.setEmail("alice@example.com");
        user1.setName("Alice Smith");
        userRepository.save(user1);
        
        User user2 = new User();
        user2.setEmail("bob@example.com");
        user2.setName("Bob Johnson");
        userRepository.save(user2);
        
        User user3 = new User();
        user3.setEmail("alice2@example.com");
        user3.setName("Alice Brown");
        userRepository.save(user3);
        
        // When & Then - Filter by name
        mockMvc.perform(get("/api/users?name=Alice"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].name", everyItem(containsString("Alice"))));
    }

    @Test
    void shouldPaginateResults() throws Exception {
        // Given - Many users
        for (int i = 0; i < 25; i++) {
            User user = new User();
            user.setEmail("user" + i + "@example.com");
            user.setName("User " + i);
            userRepository.save(user);
        }
        
        // When & Then - Get paginated results
        mockMvc.perform(get("/api/users?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(10)))
            .andExpect(jsonPath("$.totalElements").value(25))
            .andExpect(jsonPath("$.totalPages").value(3))
            .andExpect(jsonPath("$.number").value(0));
    }
}
```

---

## Repository Test Example

### Example 3: JPA Repository Test

```java
package com.example.repository;

import com.example.AbstractIntegrationTest;
import com.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        // When - Save user
        User saved = userRepository.save(testUser);
        entityManager.flush();
        
        // Then - User is saved with ID
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        
        // And can be retrieved
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldFindByEmail() {
        // Given - Saved user
        userRepository.save(testUser);
        entityManager.flush();
        
        // When - Find by email
        Optional<User> found = userRepository.findByEmail("test@example.com");
        
        // Then - User is found
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        // When - Search for non-existent email
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        // Then - Result is empty
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given - Saved user
        userRepository.save(testUser);
        entityManager.flush();
        
        // When & Then - Email exists
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }

    @Test
    void shouldFindAllActiveUsers() {
        // Given - Active and inactive users
        User activeUser1 = createUser("active1@example.com", true);
        User activeUser2 = createUser("active2@example.com", true);
        User inactiveUser = createUser("inactive@example.com", false);
        
        userRepository.saveAll(List.of(activeUser1, activeUser2, inactiveUser));
        entityManager.flush();
        
        // When - Find active users
        List<User> activeUsers = userRepository.findAllByActiveTrue();
        
        // Then - Only active users returned
        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).extracting("email")
            .containsExactlyInAnyOrder("active1@example.com", "active2@example.com");
    }

    @Test
    void shouldDeleteUserById() {
        // Given - Saved user
        User saved = userRepository.save(testUser);
        Long userId = saved.getId();
        entityManager.flush();
        
        // When - Delete user
        userRepository.deleteById(userId);
        entityManager.flush();
        
        // Then - User no longer exists
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    private User createUser(String email, boolean active) {
        User user = new User();
        user.setEmail(email);
        user.setName("User " + email);
        user.setActive(active);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
```

---

## Key Patterns Demonstrated

### 1. Test Naming Convention
- `shouldDoSomethingWhenCondition()` format
- Clear, descriptive names that explain behavior

### 2. Test Structure (Given-When-Then)
- **Given:** Setup test data and mocks
- **When:** Execute the action under test
- **Then:** Verify the outcome

### 3. Assertion Style
- Use AssertJ for fluent assertions
- `assertThat(result).isEqualTo(expected)`
- More readable than traditional JUnit assertions

### 4. Mock Usage
- Mock external dependencies in unit tests
- Use `@Mock` and `@InjectMocks` annotations
- Verify interactions with `verify()`

### 5. Integration Test Patterns
- Extend `AbstractIntegrationTest` for database tests
- Use `@Transactional` to rollback changes
- Test complete user workflows

### 6. Edge Cases
- Test null inputs
- Test empty collections
- Test boundary conditions
- Test error paths

---

## Anti-Patterns to Avoid

❌ **Don't:**
- Test multiple behaviors in one test
- Use hard-coded values without context
- Share mutable state between tests
- Skip test cleanup
- Test implementation details

✅ **Do:**
- One assertion concept per test
- Use meaningful test data
- Keep tests independent
- Clean up after tests
- Test behavior, not implementation
