package br.all.infrastructure

import br.all.application.user.CredentialsService
import br.all.application.user.find.LoadAccountCredentialsService
import br.all.infrastructure.api.CredentialServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CredentialServiceTest {
    @MockK
    private lateinit var accountCredentialService: LoadAccountCredentialsService
    @InjectMockKs
    private lateinit var credentialService: CredentialServiceImpl

    @Test
    fun `should load account credentials and return correct response`() {

        val uuid = UUID.randomUUID()
        val userSimpleCredentials = LoadAccountCredentialsService.UserSimpleCredentials(
            id = uuid,
            username = "test",
            authorities = setOf("ROLE")
        )
        val expectedResponse = CredentialsService.ResponseModel(
            userSimpleCredentials.id,
            userSimpleCredentials.username,
            userSimpleCredentials.authorities
        )

        every { accountCredentialService.loadSimpleCredentialsById(uuid) } returns userSimpleCredentials

        val actualResponse = credentialService.loadCredentials(uuid)

        verify(exactly = 1) { accountCredentialService.loadSimpleCredentialsById(uuid) }
        assertEquals(expectedResponse, actualResponse)
    }
}