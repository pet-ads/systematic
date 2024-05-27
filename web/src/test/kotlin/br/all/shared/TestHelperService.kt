package br.all.shared

import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserAccountRepository
import br.all.security.service.ApplicationUser
import io.github.serpro69.kfaker.Faker
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.stereotype.Service
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.*

@Service
class TestHelperService(
    private val repository: UserAccountRepository,
    private val mockMvc: MockMvc
) {

    private val faker = Faker()

    fun createApplicationUser(): ApplicationUser {
        val userDto = UserAccountDto(
            id = UUID.randomUUID(),
            username = faker.name.firstName(),
            password = faker.fallout.locations(),
            email = faker.internet.email(),
            country = faker.address.country(),
            affiliation = faker.university.name(),
            createdAt = LocalDateTime.now(),
            authorities = setOf("USER"),
            refreshToken = null,
            isAccountNonExpired = true,
            isAccountNonLocked = true,
            isCredentialsNonExpired = true,
            isEnabled = true
        )
        val applicationUser = ApplicationUser(userDto.id, userDto.username, userDto.password)
        repository.save(userDto)
        return applicationUser
    }

    fun createUnauthorizedApplicationUser(): ApplicationUser {
        val userDto = UserAccountDto(
            id = UUID.randomUUID(),
            username = faker.name.firstName(),
            password = faker.fallout.locations(),
            email = faker.internet.email(),
            country = faker.address.country(),
            affiliation = faker.university.name(),
            createdAt = LocalDateTime.now(),
            authorities = setOf(),
            refreshToken = null,
            isAccountNonExpired = true,
            isAccountNonLocked = true,
            isCredentialsNonExpired = true,
            isEnabled = true
        )
        val applicationUser = ApplicationUser(userDto.id, userDto.username, userDto.password)
        repository.save(userDto)
        return applicationUser
    }

    fun testForUnauthorizedUser(requestBuilder: MockHttpServletRequestBuilder, action: MockMvcAction) {
        val unauthorizedUser = createUnauthorizedApplicationUser()

        val request = requestBuilder
            .with(SecurityMockMvcRequestPostProcessors.user(unauthorizedUser))
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.action(request)
            .andExpect(status().isForbidden)

        deleteApplicationUser(unauthorizedUser.id)
    }

    fun testForUnauthenticatedUser(requestBuilder: MockHttpServletRequestBuilder, action: MockMvcAction) {
        val request = requestBuilder
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.action(request)
            .andExpect(status().isUnauthorized)
    }

    fun deleteApplicationUser(id: UUID) {
        repository.deleteById(id)
    }
}

typealias MockMvcAction = MockMvc.(MockHttpServletRequestBuilder) -> ResultActions