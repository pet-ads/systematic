package br.all.application.user.find

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.utils.TestDataFactory
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.NoSuchElementException
import kotlin.test.assertFailsWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class LoadAccountCredentialsServiceImplTest {

    @MockK(relaxUnitFun = true) private lateinit var userAccountRepository: UserAccountRepository

    private lateinit var sut: LoadAccountCredentialsServiceImpl
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        sut = LoadAccountCredentialsServiceImpl(userAccountRepository)
        factory = TestDataFactory()
    }

    @Nested
    @DisplayName("When loading authentication credentials by username")
    inner class WhenLoadingAuthenticationCredentialsByUsername {
        @Test
        fun `should load authentication credentials`() {
            val dto = factory.accountCredentials()

            every { userAccountRepository.loadCredentialsByUsername(dto.username)} returns dto

            assertDoesNotThrow { sut.loadAuthenticationCredentialsByUsername(dto.username) }
        }
        @Test
        fun `should not load credentials when repo returns null`() {
            val name = "test"

            every { userAccountRepository.loadCredentialsByUsername(name)} returns null

            assertFailsWith<NoSuchElementException> { sut.loadAuthenticationCredentialsByUsername(name) }
        }
    }

    @Nested
    @DisplayName("When loading simple credentials by token")
    inner class WhenLoadingSimpleCredentialsByToken {
        @Test
        fun `should load simple credentials by token`() {
            val dto = factory.accountCredentials()

            every { dto.refreshToken?.let { userAccountRepository.loadCredentialsByToken(it) } } returns dto

            assertDoesNotThrow { dto.refreshToken?.let { sut.loadSimpleCredentialsByToken(it) } }
        }
        @Test
        fun `should not load credentials when repo returns null`() {
            val token = "test"

            every { userAccountRepository.loadCredentialsByToken(token)} returns null

            assertFailsWith<NoSuchElementException> { sut.loadSimpleCredentialsByToken(token) }
        }
    }

    @Nested
    @DisplayName("When loading simple credentials by id")
    inner class WhenLoadingSimpleCredentialsById {
        @Test
        fun `should load simple credentials by id`() {
            val dto = factory.accountCredentials()

            every { userAccountRepository.loadCredentialsById(dto.id) } returns dto

            assertDoesNotThrow { sut.loadSimpleCredentialsById(dto.id) }
        }
        @Test
        fun `should not load credentials when repo returns null`() {
            val id = UUID.randomUUID()

            every { userAccountRepository.loadCredentialsById(id)} returns null

            assertFailsWith<NoSuchElementException> { sut.loadSimpleCredentialsById(id) }
        }
    }


}