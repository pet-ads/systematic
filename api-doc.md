
## Resources

### Root path:
*api/v1/systematic*

### Review:
- [**POST** *researcher/{researcherId}/review*]: create a new systematic study
- [**PUT** *researcher/{researcherId}/review/{reviewId}*]:  update an existing systematic study
- [**DELETE** *researcher/{researcherId}/review/{reviewId}*]:  delete an existing systematic study
- [**GET** *researcher/{researcherId}/review/{reviewId}*]:  get a systematic study by id
- [**GET** *researcher/{researcherId}/review/owner/{researcherId}*]:  get all systematic studies of a given reviewer
- [**GET** *researcher/{researcherId}/review/*]:  get all systematic studies

### Protocol:
- [**POST**  *researcher/{researcherId}/review/{reviewId}/protocol*]: create a protocol for a given systematic study
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/protocol/{protocolId}*]: update the protocol of a systematic study
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/{protocolId}*]: get the protocol of a systematic study

##### Research Question:
- [**POST**  *researcher/{researcherId}/review/{reviewId}/protocol/research-question*]: create a research question in the protocol
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/protocol/research-question/{code}*]: update an existing research question in the protocol
- [**DELETE**  *researcher/{researcherId}/review/{reviewId}/protocol/research-question/{code}*]: delete an existing research question in the protocol
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/research-question/{code}*]: get a research question of a given protocol by code
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/research-question*]: get all research questions in the protocol

##### Selection criteria
- [**POST**  *researcher/{researcherId}/review/{reviewId}/protocol/criteria*]: create a selection criterion in the protocol
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/protocol/criteria/{code}*]: update an existing selection criterion in the  protocol
- [**DELETE**  *researcher/{researcherId}/review/{reviewId}/protocol/criteria/{code}*]: delete an existing selection criterion in the  protocol
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/criteria/{code}*]: get a selection criterion in a given protocol by code
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/criteria*]: get all selection criteria in the  protocol

##### PICOC
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/protocol/picoc*]: create or update the picoc of the protocol
- [**DELETE**  *researcher/{researcherId}/review/{reviewId}/protocol/picoc*]: delete an existing picoc of the protocol
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/picoc*]: get the picoc of the protocol

##### Extraction Question:
- [**POST**  *researcher/{researcherId}/review/{reviewId}/protocol/extraction-question*]: create an extraction question in the protocol
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/protocol/extraction-question/{code}*]: update an existing extraction question in the protocol
- [**DELETE**  *researcher/{researcherId}/review/{reviewId}/protocol/extraction-question/{code}*]: delete an existing extraction question in the protocol
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/extraction-question/{code}*]: get an extraction question of a given protocol by code
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/extraction-question*]: get all extraction questions in the protocol


##### Research Question:
- [**POST**  *researcher/{researcherId}/review/{reviewId}/protocol/rob-question*]: create a risk of bias (rob) question in the protocol
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/protocol/rob-question/{code}*]: update an existing risk of bias (rob) question in the protocol
- [**DELETE**  *researcher/{researcherId}/review/{reviewId}/protocol/rob-question/{code}*]: delete an existing risk of bias (rob) question in the protocol
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/rob-question/{code}*]: get a risk of bias (rob) question of a given protocol by code
- [**GET**  *researcher/{researcherId}/review/{reviewId}/protocol/rob-question*]: get all risk of bias (rob) questions in the protocol


### Search Session:
- [**POST**  *researcher/{researcherId}/review/{reviewId}/search-session/*]: create a search session in the systematic study
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/search-session/{sessionId}*]: update an existing search session of a systematic study
- [**GET**  *researcher/{researcherId}/review/{reviewId}/search-session/{sessionId}*]: get an existing search session of a systematic study
- [**GET**  *researcher/{researcherId}/review/{reviewId}/search-session/*]: get all search sessions of a systematic review

### Study Review:
- [**POST**  *researcher/{researcherId}/review/{reviewId}/study-review/*]: create a study review in the systematic study
- [**PUT**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}*]: update an existing study review of a systematic study
- [**DELETE**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}*]: delete an existing study review of a systematic study
- [**PATCH**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewIdtoKeep}/duplicated/{studyReviewIdtoIgnore}*]: mark an existing study as duplicated in the systematic study

- [**GET**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}*]: get an existing study review of a systematic study
- [**GET**  *researcher/{researcherId}/review/{reviewId}/study-review/*]: get all existing studies of a systematic review
- [**GET**  *researcher/{researcherId}/review/{reviewId}/search-session/{sessionId}/study-review/*]: get all existing studies of a systematic review search session

- [**PATCH**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}/extraction-answer*]: update the answer of a given extraction question defined in the protocol
- [**PATCH**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}/rob-answer*]: update the answer of a given risk of bias question defined in the protocol

- [**PATCH**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}/selection*]: update the selection status of study review
- [**PATCH**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}/extraction*]: update the selection status of study review
- [**PATCH**  *researcher/{researcherId}/review/{reviewId}/study-review/{studyReviewId}/priority*]: update the reading priority of study review

## Resources

In the future, all requests must check if the user is authenticated and authorized to perform the request. 
If the user is not authenticated, the service must return HTTP 401. If the use is authenticated but has no authority to 
request, the service must return HTTP 403 (forbidden).

### POST

- 201: the resource was created and the response includes a URL to later access it.
- 404: it was not possible to create the resource because some resource ID (e.g., reviewId) in the path does not exist.
- 409: the resource to be included already exists or its properties violate attribute unicity of an existing resource.

### PUT

- 201: when applicable, the resource was created if not already available and the response includes a URL to later access it.
- 204: the update was performed and no content will be returned in response.
- 404: the resource to be updated does not exist **OR** it was not possible to update the resource because some resource ID in the path does not exist (e.g., reviewId)
- 409: some property of the resource being updated violates the attribute unicity of an existing resource

### PATCH

- 204: the updated was performed and no content will be returned in response.
- 404: the resource to be patched does not exist **OR** it was not possible to update the resource because some resource ID in the path does not exist (e.g., reviewId).
- 409: it is not possible to change the resource from the current state to the requested one **OR** some property of the resource being updated violates the attribute unicity of an existing resource.

### DELETE

- 204: the resource was deleted and no content will be returned in response.
- 404: the resource to be removed does not exist **OR** it was not possible to remove the resource because some resource ID in the path does not exist (e.g., reviewId).

### GET

- 200: the resource is avaliable in the response **OR** a list of resources are available in the response **OR** the request is valid and an empty list is returned in response because no resource matches the query condition. 
- 404: the resource does not exist **OR** it was not possible to find the resource because some resource ID in the path does not exist (e.g., reviewId).



