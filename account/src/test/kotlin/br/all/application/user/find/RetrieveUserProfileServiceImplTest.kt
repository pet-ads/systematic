package br.all.application.user.find

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.utils.TestDataFactory
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.NoSuchElementException

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class RetrieveUserProfileServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var userAccountRepository: UserAccountRepository

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: RetrieveUserProfilePresenter

    private lateinit var sut: RetrieveUserProfileServiceImpl
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        sut = RetrieveUserProfileServiceImpl(userAccountRepository)
        factory = TestDataFactory()
    }

    @Nested
    @DisplayName("When retrieving a user profile")
    inner class WhenRetrievingUserProfile {

        @Test
        fun `should retrieve user profile and prepare success view`() {
            val userProfile = factory.userProfile()
            val userCredentials = factory.accountCredentials().copy(id = userProfile.id)
            val request = RetrieveUserProfileService.RequestModel(userId = userProfile.id)

            every { userAccountRepository.loadUserProfileById(request.userId) } returns userProfile
            every { userAccountRepository.loadCredentialsById(request.userId) } returns userCredentials

            val responseSlot = slot<RetrieveUserProfileService.ResponseModel>()
            every { presenter.prepareSuccessView(capture(responseSlot)) } returns Unit

            sut.retrieveData(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }

            val capturedResponse = responseSlot.captured
            assertEquals(userProfile.id, capturedResponse.userId)
            assertEquals(userProfile.name, capturedResponse.name)
            assertEquals(userCredentials.username, capturedResponse.username)
            assertEquals(userProfile.email, capturedResponse.email)
            assertEquals(userProfile.affiliation, capturedResponse.affiliation)
            assertEquals(userProfile.country, capturedResponse.country)
            assertEquals(userCredentials.authorities, capturedResponse.authorities)
        }

        @Test
        fun `should prepare fail view when user profile is not found`() {
            val userId = UUID.randomUUID()
            val request = RetrieveUserProfileService.RequestModel(userId = userId)

            every { userAccountRepository.loadUserProfileById(userId) } returns null

            val exceptionSlot = slot<NoSuchElementException>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.retrieveData(presenter, request)

            verify(exactly = 0) { presenter.prepareSuccessView(any()) }
            verify(exactly = 1) { presenter.prepareFailView(any()) }

            val capturedException = exceptionSlot.captured
            assertEquals("User with id $userId doesn't exist!", capturedException.message)
        }

        @Test
        fun `should prepare fail view when user credentials are not found`() {
            val userProfile = factory.userProfile()
            val request = RetrieveUserProfileService.RequestModel(userId = userProfile.id)

            every { userAccountRepository.loadUserProfileById(request.userId) } returns userProfile
            every { userAccountRepository.loadCredentialsById(request.userId) } returns null

            val exceptionSlot = slot<NoSuchElementException>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.retrieveData(presenter, request)

            verify(exactly = 0) { presenter.prepareSuccessView(any()) }
            verify(exactly = 1) { presenter.prepareFailView(any()) }

            val capturedException = exceptionSlot.captured
            assertEquals("Account credentials with id ${request.userId} doesn't exist!", capturedException.message)
        }
    }
}
