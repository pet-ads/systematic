package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudy
import br.all.domain.shared.user.ResearcherId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
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

            @Test
            fun `should allow secondary percentages summing exactly 100 when studies are not shared`() {
                val secondaries = mapOf(
                    ResearcherId(UUID.randomUUID()) to 60.0,
                    ResearcherId(UUID.randomUUID()) to 40.0,
                )

                assertDoesNotThrow {
                    Replication(mainReviewer, secondaries, noStudySharedBySecondaries = true)
                }
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And providing invalid states")
        inner class AndProvidingInvalidStates {
            @Test
            fun `should not allow secondary percentages above 100 when studies are not shared`() {
                val secondaries = mapOf(
                    ResearcherId(UUID.randomUUID()) to 50.1,
                    ResearcherId(UUID.randomUUID()) to 50.0,
                )

                assertThrows<IllegalArgumentException> {
                    Replication(mainReviewer, secondaries, noStudySharedBySecondaries = true)
                }
            }
        }

    }
}