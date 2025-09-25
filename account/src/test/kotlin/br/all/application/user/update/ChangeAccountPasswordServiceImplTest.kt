package br.all.application.user.update

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.utils.TestDataFactory
import br.all.domain.shared.service.PasswordEncoderPort
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertIs

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class ChangeAccountPasswordServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var repository: UserAccountRepository

    @MockK
    private lateinit var encoder: PasswordEncoderPort

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: ChangeAccountPasswordPresenter

    private lateinit var sut: ChangeAccountPasswordServiceImpl
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setup() {
        sut = ChangeAccountPasswordServiceImpl(repository, encoder)
        factory = TestDataFactory()
    }

    @Nested
    @DisplayName("When changing a user's password")
    inner class WhenChangingPassword {

        @Test
        fun `should update password when all inputs are valid`() {
            val userCredentials = factory.accountCredentials().copy(password = "oldHashedPassword")
            val request = ChangeAccountPasswordService.RequestModel(
                userId = userCredentials.id,
                oldPassword = "plainOldPassword",
                newPassword = "plainNewPassword",
                confirmPassword = "plainNewPassword"
            )
            val newHashedPassword = "newHashedPassword"

            every { repository.loadCredentialsById(userCredentials.id) } returns userCredentials
            every { encoder.matches(request.oldPassword, userCredentials.password) } returns true
            every { encoder.matches(request.newPassword, userCredentials.password) } returns false
            every { encoder.encode(request.newPassword) } returns newHashedPassword

            val userIdSlot = slot<UUID>()
            val passwordSlot = slot<String>()
            every { repository.updatePassword(capture(userIdSlot), capture(passwordSlot)) } returns Unit

            val responseSlot = slot<ChangeAccountPasswordService.ResponseModel>()
            every { presenter.prepareSuccessView(capture(responseSlot)) } returns Unit

            sut.changePassword(presenter, request)

            verify(exactly = 1) { presenter.prepareSuccessView(any()) }
            verify(exactly = 0) { presenter.prepareFailView(any()) }
            verify(exactly = 1) { repository.updatePassword(userCredentials.id, newHashedPassword) }

            assertEquals(userCredentials.id, userIdSlot.captured)
            assertEquals(newHashedPassword, passwordSlot.captured)
            assertEquals(userCredentials.id, responseSlot.captured.userId)
        }

        @Test
        fun `should prepare fail view when user does not exist`() {
            val nonExistentUserId = UUID.randomUUID()
            val request = ChangeAccountPasswordService.RequestModel(
                userId = nonExistentUserId,
                oldPassword = "any", newPassword = "any", confirmPassword = "any"
            )

            every { repository.loadCredentialsById(nonExistentUserId) } returns null

            val exceptionSlot = slot<Exception>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.changePassword(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
            verify(exactly = 0) { presenter.prepareSuccessView(any()) }
            verify(exactly = 0) { repository.updatePassword(any(), any()) }

            assertIs<NoSuchElementException>(exceptionSlot.captured)
            assertEquals("User with id $nonExistentUserId doesn't exist!", exceptionSlot.captured.message)
        }

        @Test
        fun `should prepare fail view when old password is incorrect`() {
            val userCredentials = factory.accountCredentials().copy(password = "oldHashedPassword")
            val request = ChangeAccountPasswordService.RequestModel(
                userId = userCredentials.id,
                oldPassword = "wrongOldPassword",
                newPassword = "plainNewPassword",
                confirmPassword = "plainNewPassword"
            )

            every { repository.loadCredentialsById(userCredentials.id) } returns userCredentials
            every { encoder.matches(request.oldPassword, userCredentials.password) } returns false

            val exceptionSlot = slot<Exception>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.changePassword(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
            verify(exactly = 0) { presenter.prepareSuccessView(any()) }

            assertIs<IllegalArgumentException>(exceptionSlot.captured)
            assertEquals("Invalid old password provided!", exceptionSlot.captured.message)
        }

        @Test
        fun `should prepare fail view when new password and confirmation do not match`() {
            val userCredentials = factory.accountCredentials().copy(password = "oldHashedPassword")
            val request = ChangeAccountPasswordService.RequestModel(
                userId = userCredentials.id,
                oldPassword = "plainOldPassword",
                newPassword = "plainNewPassword",
                confirmPassword = "doesNotMatch"
            )

            every { repository.loadCredentialsById(userCredentials.id) } returns userCredentials
            every { encoder.matches(request.oldPassword, userCredentials.password) } returns true

            val exceptionSlot = slot<Exception>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.changePassword(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
            verify(exactly = 0) { presenter.prepareSuccessView(any()) }

            assertIs<IllegalArgumentException>(exceptionSlot.captured)
            assertEquals("Confirm password does not match new password!", exceptionSlot.captured.message)
        }

        @Test
        fun `should prepare fail view when new password is the same as the old one`() {
            val userCredentials = factory.accountCredentials().copy(password = "oldHashedPassword")
            val request = ChangeAccountPasswordService.RequestModel(
                userId = userCredentials.id,
                oldPassword = "plainOldPassword",
                newPassword = "plainOldPassword",
                confirmPassword = "plainOldPassword"
            )

            every { repository.loadCredentialsById(userCredentials.id) } returns userCredentials
            every { encoder.matches(request.oldPassword, userCredentials.password) } returns true
            every { encoder.matches(request.newPassword, userCredentials.password) } returns true

            val exceptionSlot = slot<Exception>()
            every { presenter.prepareFailView(capture(exceptionSlot)) } returns Unit

            sut.changePassword(presenter, request)

            verify(exactly = 1) { presenter.prepareFailView(any()) }
            verify(exactly = 0) { presenter.prepareSuccessView(any()) }

            assertIs<IllegalArgumentException>(exceptionSlot.captured)
            assertEquals("New password cannot be the same as the old password!", exceptionSlot.captured.message)
        }
    }
}
