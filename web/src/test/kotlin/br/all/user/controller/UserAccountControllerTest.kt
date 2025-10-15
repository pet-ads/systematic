package br.all.user.controller

import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserAccountRepository
import br.all.security.service.ApplicationUser
import br.all.security.service.AuthenticationService
import br.all.shared.TestHelperService
import br.all.user.shared.TestDataFactory
import jakarta.transaction.Transactional
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Tag("IntegrationTest")
@Tag("ControllerTest")
@DisplayName("User Account Controller Integration Tests")
class UserAccountControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userAccountRepository: UserAccountRepository,
    @Autowired private val testHelperService: TestHelperService,
    @Autowired private val passwordEncoder: PasswordEncoder
) {
    private lateinit var factory: TestDataFactory
    private lateinit var testUser: ApplicationUser
    private lateinit var userDto: UserAccountDto

    @MockitoBean
    private lateinit var authenticationService: AuthenticationService

    private val testRefreshToken = "test-refresh-token-${UUID.randomUUID()}"

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()

        testUser = testHelperService.createApplicationUser()

        userDto = factory.createUserDto(
            id = testUser.id,
            email = testUser.username,
            password = passwordEncoder.encode(factory.rawPassword)
        )
        userAccountRepository.save(userDto)
        userAccountRepository.updateRefreshToken(testUser.id, testRefreshToken)
    }

    private fun registerUrl() = "/api/v1/user"
    private fun profileUrl() = "/api/v1/user/profile"
    private fun changePasswordUrl() = "/api/v1/user/change-password"

    @Nested
    @DisplayName("When registering a new user")
    inner class WhenRegisteringUser {
        @Test
        fun `should create a new user and return 201 Created`() {
            val newUserEmail = "new.user@test.com"
            val requestJson = factory.createValidRegisterRequestJson(email = newUserEmail)

            mockMvc.perform(
                post(registerUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.email").value(newUserEmail))

            assertTrue(userAccountRepository.existsByEmail(newUserEmail))
        }

        @Test
        fun `should return 409 Conflict when email already exists`() {
            val requestJson = factory.createValidRegisterRequestJson(email = testUser.username)

            mockMvc.perform(
                post(registerUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            )
                .andExpect(status().isConflict)
        }
    }

    @Nested
    @DisplayName("When retrieving user profile")
    inner class WhenRetrievingUserProfile {
        @Test
        fun `should return user profile and 200 OK for authenticated user`() {
            mockMvc.perform(
                get(profileUrl())
                    .with(user(testUser))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.userId").value(testUser.id.toString()))
                .andExpect(jsonPath("$.name").value(userDto.name))
                .andExpect(jsonPath("$.email").value(userDto.email))
        }

        @Test
        fun `should return 401 Unauthorized for unauthenticated user`() {
            testHelperService.testForUnauthenticatedUser(mockMvc, get(profileUrl()))
        }
    }

    @Nested
    @DisplayName("When updating user profile")
    inner class WhenUpdatingUserProfile {
        @Test
        fun `should update user profile and return 200 OK`() {
            val newName = "Updated Name"
            val updateRequestJson = factory.createPatchProfileRequestJson(name = newName)

            mockMvc.perform(
                patch(profileUrl())
                    .with(user(testUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateRequestJson)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.name").value(newName))

            val updatedProfile = userAccountRepository.loadUserProfileById(testUser.id)
            assertEquals(newName, updatedProfile?.name)
        }
    }

    @Nested
    @DisplayName("When changing user password")
    inner class WhenChangingUserPassword {
        @Test
        fun `should return 400 Bad Request if old password is incorrect`() {
            val requestJson = factory.createChangePasswordRequestJson(oldPassword = "wrong-password")

            mockMvc.perform(
                put(changePasswordUrl())
                    .with(user(testUser))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            )
                .andExpect(status().isBadRequest)
        }
    }
}