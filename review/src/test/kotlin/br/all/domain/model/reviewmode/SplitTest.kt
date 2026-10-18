package br.all.domain.model.reviewmode

import br.all.domain.shared.user.ResearcherId
import br.all.infrastructure.study.StudyReviewId
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
        }
    }
}
