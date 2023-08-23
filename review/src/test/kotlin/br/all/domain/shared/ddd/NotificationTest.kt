package br.all.domain.shared.ddd

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NotificationTest {
    @Test
    fun `Should notification with no errors present no error message`(){
        val notification = Notification()
        assertEquals("", notification.message())
        assertTrue(notification.hasNoErrors())
    }

    @Test
    fun `Should notification with one error present error message`(){
        val notification = Notification()
        notification.addError("message")
        assertEquals("message", notification.message())
        assertFalse(notification.hasNoErrors())
    }

    @Test
    fun `Should notification of more than one message present composite message`(){
        val notification = Notification()
        notification.addError("message")
        notification.addError("other message")
        assertEquals("message | other message", notification.message())
    }
}
