package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID
import kotlin.collections.emptyList
import kotlin.test.Test

@Tag("UnitTest")
class SplitTest {
    private val reviewers = listOf(
        ResearcherId(UUID.randomUUID()),
        ResearcherId(UUID.randomUUID()),
    )

    private val reviewerStudies: Map<ResearcherId, Set<Long>> = mapOf(
        reviewers[0] to setOf(100L, 101L),
        reviewers[1] to setOf(102L),
    )

    @Nested
    @DisplayName("When creating a new Split")
    inner class WhenCreatingANewSplit {
        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And providing invalid states")
        inner class AndProvidingInvalidStates {
            @ParameterizedTest(name = "[{index}]: reviewPerStudy = {0}")
            @ValueSource(ints = [0, -1])
            fun `should not create split with review per study not greater than zero`(reviewPerStudy: Int) {
                assertThrows<IllegalArgumentException> { Split(reviewPerStudy) }
            }

            @Test
            fun `should not create split with review per study greater than number of reviewers`() {
                assertThrows<IllegalArgumentException> { Split(reviewers, reviewPerStudy = 3) }
            }

            @Test
            fun `should not create split without reviewers`() {
                assertThrows<IllegalArgumentException> { Split(emptyList(), reviewPerStudy = 1) }
            }
        }

        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @Test
            fun `should allow review per study equal to number of reviewers`() {
                assertDoesNotThrow { Split(reviewers, reviewPerStudy = reviewers.size) }
            }
        }
    }

    @Nested
    @DisplayName("When splitting")
    inner class WhenSplitting {
        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And providing invalid states")
        inner class AndProvidingInvalidStates{
            @Test
            fun `should not split when there are no available studies`() {
                val split = Split(reviewers, reviewPerStudy = 1)

                assertThrows<IllegalArgumentException> {
                    split.split(emptyList(), reviewerStudies)
                }
            }

            @Test
            fun `should not split when reviewer studies contain a researcher not configured in the split`() {
                val split = Split(reviewers, reviewPerStudy = 1)
                val notConfigured = ResearcherId(UUID.randomUUID())
                val studies = mapOf(notConfigured to setOf(1L))

                assertThrows<IllegalArgumentException> {
                    split.split(listOf(2L), studies)
                }
            }

            @Test
            fun `should not split when available study is already assigned to maximum of reviewers`() {
                val split = Split(reviewers, reviewPerStudy = 2)
                val fullyAssigned = 100L
                val studies = mapOf(
                    reviewers[0] to setOf(fullyAssigned),
                    reviewers[1] to setOf(fullyAssigned),
                )

                assertThrows<IllegalArgumentException> {
                    split.split(listOf(fullyAssigned), studies)
                }
            }
        }

        @Nested
        @Tag("ValidClasses")
        @DisplayName("And providing valid states")
        inner class AndProvidingValidStates{
            @Test
            fun `should give a single study to one reviewer when review per study is one`() {
                val split = Split(reviewers, reviewPerStudy = 1)
                val study = 1L

                val result = split.split(listOf(study), emptyMap())

                assertEquals(1, result.values.count { study in it })
            }

            @Test
            fun `should not give a study to a reviewer who already has it`() {
                val split = Split(reviewers, reviewPerStudy = 2)
                val study = 1L
                val reviewerStudies = mapOf(reviewers[0] to setOf(study))

                val result = split.split(listOf(study), reviewerStudies)

                assertFalse(study in result.getValue(reviewers[0]))
            }

            @Test
            fun `should give a study only to the missing reviewers when some already have it`() {
                val trio = List(3) { ResearcherId(UUID.randomUUID()) }
                val split = Split(trio, reviewPerStudy = 2)
                val study = 1L
                val reviewerStudies = mapOf(trio[0] to setOf(study))

                val result = split.split(listOf(study), reviewerStudies)

                assertAll(
                    { assertFalse(study in result.getValue(trio[0])) },
                    { assertEquals(1, trio.drop(1).count { study in result.getValue(it) }) }
                )
            }

            @Test
            fun `should give every study to exactly reviewPerStudy reviewers`() {
                val split = Split(reviewers, reviewPerStudy = 2)
                val studies = List(10) { it.toLong() + 1 }

                val result = split.split(studies, emptyMap())

                studies.forEach { study ->
                    assertEquals(2, result.values.count { study in it })
                }
            }
        }
    }
}
