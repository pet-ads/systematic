package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.user.ResearcherId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertTrue

@Tag("UnitTest")
class ReplicationTest {
    val mainReviewer = ResearcherId(UUID.randomUUID())

    @Nested
    @DisplayName("When creating a new Replication")
    inner class WhenCreatingANewReplication {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @Test
            fun `should main reviewer be part of reviewers`() {
                val sut = Replication(mainReviewer)

                assertTrue(mainReviewer in sut.reviewers)
            }
        }
    }
}