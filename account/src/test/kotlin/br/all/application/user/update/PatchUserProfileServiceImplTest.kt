package br.all.application.user.update

import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.utils.TestDataFactory
import com.mongodb.assertions.Assertions.assertTrue
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class PatchUserProfileServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var repository: UserAccountRepository

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: PatchUserProfilePresenter

    private lateinit var sut: PatchUserProfileServiceImpl
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setup() {
        sut = PatchUserProfileServiceImpl(repository)
        factory = TestDataFactory()
    }

    @Nested
    @DisplayName("When patching a user profile")
    inner class WhenPatchingUserProfile {

        @Test
        fun `should update all fields and prepare success view when all entries are valid`() {
            val existingUser = factory.userAccountDto()
            val request = PatchUserProfileService.RequestModel(
                userId = existingUser.id,
                name = "New Valid Name",
                email = "new.valid@email.com",
                affiliation = "New Affiliation",
                country = "New Country"
            )

            every { repository.loadFullUserAccountById(existingUser.id) } returns existingUser

            val responseSlot = slot<PatchUserProfileService.ResponseModel>()
            every { presenter.prepareSuccessView(capture(responseSlot)) } returns Unit

            val savedUserSlot = slot<UserAccountDto>()
            every { repository.save(capture(savedUserSlot)) } returns Unit

            sut.patchProfile(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
            verify(exactly = 1) { repository.save(any()) }

            val capturedResponse = responseSlot.captured
            assertEquals(request.name, capturedResponse.name)
            assertEquals(request.email, capturedResponse.email)
            assertEquals(request.affiliation, capturedResponse.affiliation)
            assertEquals(request.country, capturedResponse.country)
            assertTrue(capturedResponse.invalidEntries.isEmpty())

            val capturedSavedUser = savedUserSlot.captured
            assertEquals(request.name, capturedSavedUser.name)
            assertEquals(request.email, capturedSavedUser.email)

            assertEquals(existingUser.id, capturedSavedUser.id)
            assertEquals(existingUser.username, capturedSavedUser.username)
        }

        @Test
        fun `should update valid fields, collect errors, and prepare success view when some entries are invalid`() {
            val existingUser = factory.userAccountDto()
            val request = PatchUserProfileService.RequestModel(
                userId = existingUser.id,
                name = "Another Valid Name",
                email = "invalid-email",
                affiliation = "Another Valid Affiliation",
                country = ""
            )

            every { repository.loadFullUserAccountById(existingUser.id) } returns existingUser

            val responseSlot = slot<PatchUserProfileService.ResponseModel>()
            every { presenter.prepareSuccessView(capture(responseSlot)) } returns Unit

            val savedUserSlot = slot<UserAccountDto>()
            every { repository.save(capture(savedUserSlot)) } returns Unit

            sut.patchProfile(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
            verify(exactly = 1) { repository.save(any()) }

            val capturedResponse = responseSlot.captured
            assertEquals(2, capturedResponse.invalidEntries.size)
            assertEquals("email", capturedResponse.invalidEntries[0].field)
            assertEquals("invalid-email", capturedResponse.invalidEntries[0].entry)
            assertEquals("country", capturedResponse.invalidEntries[1].field)
            assertEquals("", capturedResponse.invalidEntries[1].entry)

            assertEquals(request.name, capturedResponse.name)
            assertEquals(request.affiliation, capturedResponse.affiliation)
            assertEquals(existingUser.email, capturedResponse.email)
            assertEquals(existingUser.country, capturedResponse.country)

            val capturedSavedUser = savedUserSlot.captured
            assertEquals(request.name, capturedSavedUser.name)
            assertEquals(request.affiliation, capturedSavedUser.affiliation)
            assertEquals(existingUser.email, capturedSavedUser.email)
            assertEquals(existingUser.country, capturedSavedUser.country)
        }

        @Test
        fun `should prepare fail view when user does not exist`() {
            val nonExistentUserId = UUID.randomUUID()
            val request = PatchUserProfileService.RequestModel(
                userId = nonExistentUserId,
                name = "Any Name",
                email = "any@email.com",
                affiliation = "Any",
                country = "Any"
            )

            every { repository.loadFullUserAccountById(nonExistentUserId) } returns null

            val exceptionSlot = slot<NoSuchElementException>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.patchProfile(presenter, request)

            verify(exactly = 0) { presenter.prepareSuccessView(any()) }
            verify(exactly = 1) { presenter.prepareFailView(any()) }
            verify(exactly = 0) { repository.save(any()) }

            val capturedException = exceptionSlot.captured
            assertEquals("User with id $nonExistentUserId doesn't exist!", capturedException.message)
        }
    }
}