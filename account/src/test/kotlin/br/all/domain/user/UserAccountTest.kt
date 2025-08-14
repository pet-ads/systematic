package br.all.domain.user

import br.all.domain.shared.user.Email
import br.all.domain.shared.user.Text
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

@Tag("UnitTest")
class UserAccountTest{

    private val faker = Faker()
    @Test
    fun `should not allow creation of user account with blank username`(){
        assertFailsWith<IllegalArgumentException> { createUser(username = Username("")) }
    }

    @Test
    fun `should not allow creation of user account with invalid username`(){
        assertFailsWith<IllegalArgumentException> { createUser(username = Username("has space")) }
    }

    @Test
    fun `should not allow creation of user account with blank email`(){
        assertFailsWith<IllegalArgumentException> { createUser(email = Email("")) }
    }

    @Test
    fun `should not allow creation of user account with invalid email`(){
        assertFailsWith<IllegalArgumentException> { createUser(email = Email("no.at")) }
    }

    @Test
    fun `should not allow creation of user account with blank country`(){
        assertFailsWith<IllegalArgumentException> { createUser(country = Text("")) }
    }

    @Test
    fun `should not allow creation of user account with invalid country`(){
        assertFailsWith<IllegalArgumentException> { createUser(email = Email(" fake!")) }
    }

    @Test
    fun `should create account credentials with default values`(){
        val user = createUser()
        Assertions.assertEquals(user.accountCredentials.refreshToken, null)
        Assertions.assertTrue(user.accountCredentials.isCredentialsNonExpired)
        Assertions.assertTrue(user.accountCredentials.isAccountNonExpired)
        Assertions.assertTrue(user.accountCredentials.isAccountNonLocked)
        Assertions.assertTrue(user.accountCredentials.isEnabled)
    }

    @Test
    fun `should change username in account credentials`(){
        val user = createUser()
        val username = Username("new_name")
        user.changeUsername(username)
        Assertions.assertEquals(user.accountCredentials.username, username)
    }

    @Test
    fun `should change password in account credentials`(){
        val user = createUser()
        val password = "new_password"
        user.changePassword(password)
        Assertions.assertEquals(user.accountCredentials.password, password)
    }

    private fun createUser(
        id: UUID = UUID.randomUUID(),
        createdAt: LocalDateTime = LocalDateTime.now(),
        email: Email = Email(faker.internet.email()),
        country: Text = Text(faker.address.countryCode()),
        affiliation: String = faker.lorem.words(),
        username: Username = Username(faker.name.firstName()),
        password: String = faker.pearlJam.songs(),
        authorities: Set<Authority> = setOf(Authority.USER)
        ) = UserAccount(
        UserAccountId(id),
        createdAt, email, country, affiliation, username, password, authorities
    )

}