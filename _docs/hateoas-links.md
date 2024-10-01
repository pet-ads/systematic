## Names for all end-points links to be used with hateoas

# The first one is the end-point name in the controller the second one is how it should be named in hateoas


### Protocol
| Controller  | HATEOAS         |
|-------------|-----------------|
| findById    | find-protocol   |
| putProtocol | update-protocol |


### Question
# Extraction
| Controller                 | HATEOAS                                  |
|----------------------------|------------------------------------------|
| createTextualQuestion      | create-textual-extraction-question       |
| createPickListQuestion     | create-pick-list-extraction-question     |
| createLabeledScaleQuestion | create-labeled-scale-extraction-question |
| createNumberScaleQuestion  | create-numberScale-extraction-question   |
| findQuestion               | find-extraction-question                 |
| findAllBySystematicStudyId | find-all-review-extraction-questions     |
# Risk of Bias
| Controller                 | HATEOAS                           |
|----------------------------|-----------------------------------|
| createTextualQuestion      | create-textual-rob-question       |
| createPickListQuestion     | create-pick-list-rob-question     |
| createLabeledScaleQuestion | create-labeled-scale-rob-question |
| createNumberScaleQuestion  | create-numberScale-rob-question   |
| findQuestion               | find-rob-question                 |
| findAllBySystematicStudyId | find-all-review-rob-questions     |


### Review
| Controller                      | HATEOAS          |
|---------------------------------|------------------|
| postSystematicStudy             | create-review    |
| findSystematicStudy             | find-review      |
| findAllSystematicStudies        | find-all-reviews |
| findAllSystematicStudiesByOwner | find-my-reviews  |
| updateSystematicStudy           | update-review    |


### Search
| Controller                 | HATEOAS                 |
|----------------------------|-------------------------|
| createSearchSession        | create-session          |
| findAllSearchSessions      | find-all-sessions       |
| findSearchSession          | find-session            |
| findSearchSessionsBySource | find-sessions-by-source |
| updateSearchSession        | update-session          |


### Security
| Controller   | HATEOAS       |
|--------------|---------------|
| authenticate | authenticate  |
| refreshToken | refresh-token |


### Study
| Controller                        | HATEOAS                        |
|-----------------------------------|--------------------------------|
| createStudyReview                 | create-study                   |
| findAllStudyReviews               | find-all-studies               |
| findAllStudyReviewsBySession      | find-all-studies-by-session    |
| findAllStudyReviewsBySource       | find-all-studies-by-source     |
| findStudyReview                   | find-study                     |
| updateStudyReview                 | update-study                   |
| updateStudyReviewSelectionStatus  | update-study-selection-status  |
| updateStudyReviewExtractionStatus | update-study-extraction-status |
| updateStudyReviewReadingPriority  | update-study-reading-priority  |
| riskOfBiasAnswer                  | study-rob-answer               |
| markAsDuplicated                  | mark-study-as-duplicated       |


### User
| Controller   | HATEOAS       |
|--------------|---------------|
| registerUser | register-user |

