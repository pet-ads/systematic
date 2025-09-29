package br.all.user.persistence

import br.all.application.user.repository.UserAccountRepository
import br.all.user.shared.TestDataFactory
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@SpringBootTest
@Transactional
@Tag("IntegrationTest")
@Tag("RepositoryTest")
class UserAccountRepositoryTest {

    @Autowired
    private lateinit var sut: UserAccountRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
    }

    @Nested
    @DisplayName("When saving and loading users")
    inner class SavingAndLoading {
        @Test
        fun `should save a complete UserAccountDto and load it back successfully`() {
            val originalDto = factory.createUserDto(password = passwordEncoder.encode(factory.rawPassword))

            sut.save(originalDto)
            val loadedDto = sut.loadFullUserAccountById(originalDto.id)

            assertNotNull(loadedDto)
            assertEquals(originalDto, loadedDto)
        }

        @Test
        fun `should load user credentials by username`() {
            val dto = factory.createUserDto(password = passwordEncoder.encode(factory.rawPassword))
            sut.save(dto)

            val credentials = sut.loadCredentialsByUsername(dto.username)

            assertNotNull(credentials)
            assertEquals(dto.id, credentials?.id)
            assertEquals(dto.username, credentials?.username)
        }

        @Test
        fun `should load a user profile by ID`() {
            val dto = factory.createUserDto(password = passwordEncoder.encode(factory.rawPassword))
            sut.save(dto)

            val profile = sut.loadUserProfileById(dto.id)

            assertNotNull(profile)
            assertEquals(dto.id, profile?.id)
            assertEquals(dto.name, profile?.name)
            assertEquals(dto.email, profile?.email)
        }

        @Test
        fun `should return null when loading a nonexistent user`() {
            assertNull(sut.loadFullUserAccountById(UUID.randomUUID()))
            assertNull(sut.loadCredentialsByUsername("nonexistent"))
            assertNull(sut.loadUserProfileById(UUID.randomUUID()))
        }
    }

    @Nested
    @DisplayName("When updating user data")
    inner class Updating {
        @Test
        fun `should update a user's password`() {
            val dto = factory.createUserDto(password = passwordEncoder.encode(factory.rawPassword))
            sut.save(dto)
            val newPassword = "new-secure-password-123"
            val newHashedPassword = passwordEncoder.encode(newPassword)

            sut.updatePassword(dto.id, newHashedPassword)
            val updatedCredentials = sut.loadCredentialsById(dto.id)

            assertNotNull(updatedCredentials)
            assertTrue(passwordEncoder.matches(newPassword, updatedCredentials!!.password))
            assertFalse(passwordEncoder.matches(factory.rawPassword, updatedCredentials.password))
        }

        @Test
        fun `should update and clear a user's refresh token`() {
            val dto = factory.createUserDto(password = passwordEncoder.encode(factory.rawPassword))
            sut.save(dto)
            val newToken = "refreshtoken-${UUID.randomUUID()}"

            sut.updateRefreshToken(dto.id, newToken)
            var credentials = sut.loadCredentialsById(dto.id)

            assertNotNull(credentials)
            assertEquals(newToken, credentials?.refreshToken)

            sut.updateRefreshToken(dto.id, null)
            credentials = sut.loadCredentialsById(dto.id)

            assertNotNull(credentials)
            assertNull(credentials?.refreshToken)
        }
    }

    @Nested
    @DisplayName("When checking for existence")
    inner class ExistenceChecks {
        @Test
        fun `should correctly check for existence by email and username`() {
            val dto = factory.createUserDto(password = passwordEncoder.encode(factory.rawPassword))
            sut.save(dto)

            assertTrue(sut.existsByEmail(dto.email))
            assertTrue(sut.existsByUsername(dto.username))
            assertFalse(sut.existsByEmail("nonexistent@test.com"))
            assertFalse(sut.existsByUsername("nonexistent.user"))
        }
    }

    @Nested
    @DisplayName("When deleting users")
    inner class Deleting {
        @Test
        fun `should delete the user from all related tables`() {
            val dto = factory.createUserDto(password = passwordEncoder.encode(factory.rawPassword))
            sut.save(dto)

            assertNotNull(sut.loadFullUserAccountById(dto.id))

            sut.deleteById(dto.id)

            assertNull(sut.loadFullUserAccountById(dto.id))
            assertFalse(sut.existsByUsername(dto.username))
        }
    }
}