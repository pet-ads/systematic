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
    private lateinit var repository: UserAccountRepository

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: RetrieveUserProfilePresenter

    private lateinit var sut: RetrieveUserProfileServiceImpl
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        sut = RetrieveUserProfileServiceImpl(repository)
        factory = TestDataFactory()
    }

    @Nested
    @DisplayName("When retrieving a user profile")
    inner class WhenRetrievingUserProfile {

        @Test
        fun `should retrieve user profile and prepare success view`() {
            val userAccountDto = factory.userAccountDto()
            val request = RetrieveUserProfileService.RequestModel(userId = userAccountDto.id)

            every { repository.loadFullUserAccountById(userAccountDto.id) } returns userAccountDto

            val responseSlot = slot<RetrieveUserProfileService.ResponseModel>()
            every { presenter.prepareSuccessView(capture(responseSlot)) } returns Unit

            sut.retrieveData(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }

            val capturedResponse = responseSlot.captured
            assertEquals(userAccountDto.id, capturedResponse.userId)
            assertEquals(userAccountDto.name, capturedResponse.name)
            assertEquals(userAccountDto.username, capturedResponse.username)
            assertEquals(userAccountDto.email, capturedResponse.email)
            assertEquals(userAccountDto.affiliation, capturedResponse.affiliation)
            assertEquals(userAccountDto.country, capturedResponse.country)
            assertEquals(userAccountDto.authorities, capturedResponse.authorities)
        }

        @Test
        fun `should prepare fail view when user profile is not found`() {
            val userId = UUID.randomUUID()
            val request = RetrieveUserProfileService.RequestModel(userId = userId)

            every { repository.loadFullUserAccountById(userId) } returns null

            val exceptionSlot = slot<NoSuchElementException>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.retrieveData(presenter, request)

            verify(exactly = 0) { presenter.prepareSuccessView(any()) }
            verify(exactly = 1) { presenter.prepareFailView(any()) }

            val capturedException = exceptionSlot.captured
            assertEquals("User with id $userId doesn't exist!", capturedException.message)
        }
    }
}
