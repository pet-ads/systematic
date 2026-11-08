package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertTrue

@Tag("UnitTest")
class ReplicationTest {
    private val mainReviewer = ResearcherId(UUID.randomUUID())
    private val secondaryA = ResearcherId(UUID.randomUUID())
    private val secondaryB = ResearcherId(UUID.randomUUID())
    private val studies = (1L..10L).toList()

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

            @Test
            fun `should allow secondary percentages above 100 when studies are shared`() {
                val secondaries = mapOf(
                    ResearcherId(UUID.randomUUID()) to 60.0,
                    ResearcherId(UUID.randomUUID()) to 50.0,
                )

                assertDoesNotThrow {
                    Replication(mainReviewer, secondaries, noStudySharedBySecondaries = false)
                }
            }

            @Test
            fun `should assign every available study to the main reviewer`() {
                val replication = Replication(mainReviewer)

                val result = replication.assign(studies, emptyMap())

                assertEquals(studies.toSet(), result.getValue(mainReviewer))
            }

            @Test
            fun `should assign to a secondary the configured percentage of studies`() {
                val replication = Replication(mainReviewer, mapOf(secondaryA to 30.0))

                val result = replication.assign(studies, emptyMap())

                assertEquals(3, result.getValue(secondaryA).size)
            }

            @Test
            fun `should not share studies between secondaries when configured`() {
                val replication = Replication(mainReviewer, mapOf(secondaryA to 50.0, secondaryB to 50.0))

                val result = replication.assign(studies, emptyMap())

                assertTrue(result.getValue(secondaryA).intersect(result.getValue(secondaryB)).isEmpty())
                assertEquals(10, result.getValue(secondaryA).size + result.getValue(secondaryB).size)
            }

            @Test
            fun `should not exceed the number of studies when rounding without sharing`() {
                val replication = Replication(mainReviewer, mapOf(secondaryA to 50.0, secondaryB to 50.0))
                val fiveStudies = (1L..5L).toList()

                val result = replication.assign(fiveStudies, emptyMap())

                val a = result.getValue(secondaryA)
                val b = result.getValue(secondaryB)
                assertTrue(a.intersect(b).isEmpty())
                assertEquals(5, a.size + b.size)
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

            @ParameterizedTest(name = "[{index}]: percentage = {0}")
            @ValueSource(doubles = [-0.1, 100.1])
            fun `should not allow a reviewer percentage outside 0 to 100`(percentage: Double) {
                val secondaries = mapOf(ResearcherId(UUID.randomUUID()) to percentage)

                assertThrows<IllegalArgumentException> {
                    Replication(mainReviewer, secondaries, noStudySharedBySecondaries = false)
                }
            }
        }

    }
}