package br.all.shared

import br.all.application.user.repository.UserAccountDto
import br.all.application.user.repository.UserAccountRepository
import br.all.security.service.ApplicationUser
import io.github.serpro69.kfaker.Faker
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TestHelperService(private val repository: UserAccountRepository) {

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

    fun deleteApplicationUser(id: UUID) {
        repository.deleteById(id)
    }
}