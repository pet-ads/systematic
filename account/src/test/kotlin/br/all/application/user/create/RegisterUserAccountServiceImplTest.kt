package br.all.application.user.create

import br.all.application.shared.UniquenessViolationException
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.utils.TestDataFactory
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifyOrder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class RegisterUserAccountServiceImplTest {

    @MockK(relaxed = true) private lateinit var presenter: RegisterUserAccountPresenter
    @MockK(relaxUnitFun = true) private lateinit var userAccountRepository: UserAccountRepository

    private lateinit var sut: RegisterUserAccountServiceImpl
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        sut = RegisterUserAccountServiceImpl(userAccountRepository)
        factory = TestDataFactory()
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully registering a user")
    inner class WhenSuccessfullyRegisteringUser {
        @Test
        fun `should register a new user`() {
            val request = factory.registerRequest()

            every { userAccountRepository.existsByEmail(request.email) } returns false
            every { userAccountRepository.existsByUsername(request.username) } returns false

            sut.register(presenter, request)

            verifyOrder {
                userAccountRepository.save(any())
                presenter.prepareSuccessView(any())
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When failing to register a user")
    inner class WhenFailingToRegisterUser {
        @Test
        fun `should not register user with existing email`() {
            val request = factory.registerRequest()

            every { userAccountRepository.existsByEmail(request.email) } returns true
            every { userAccountRepository.existsByUsername(request.username) } returns false

            print(request.email)

            sut.register(presenter, request)

            verifyOrder {
                presenter.prepareFailView(ofType(UniquenessViolationException::class))
                presenter.isDone()
            }
        }

        @Test
        fun `should not register user with existing username`() {
            val request = factory.registerRequest()

            every { userAccountRepository.existsByEmail(request.email) } returns false
            every { userAccountRepository.existsByUsername(request.username) } returns true

            sut.register(presenter, request)

            verifyOrder {
                presenter.prepareFailView(ofType(UniquenessViolationException::class))
                presenter.isDone()
            }
        }
    }
}