
## How to create and name endpoints

### Root path:
*api/v1*

### SystematicStudy:
- **[OK]**[**POST**] */systematic-study*]: create a new systematic study
- **[OK]**[**PUT**] */systematic-study/{systematicStudyId}*]:  update an existing systematic study
- [**DELETE**] */systematic-study/{systematicStudyId}*]:  delete an existing systematic study
- **[OK]**[**GET**] */systematic-study/{systematicStudyId}*]:  get a systematic study by id
- **[OK]**[**GET**] */systematic-study/owner/{researcherId}*]:  get all systematic studies of a given reviewer
- **[OK]**[**GET**] */systematic-study/*]:  get all systematic studies

### Protocol:
- **[OK]**[**POST**]  */systematic-study/{systematicStudyId}/protocol*]: create a protocol for a given systematic study
- [**PUT**]  */systematic-study/{systematicStudyId}/protocol/{protocolId}*]: update the protocol of a systematic study
- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/protocol/{protocolId}*]: get the protocol of a systematic study

[//]: # (##### Research Question:)

[//]: # (- [**POST**]  */systematic-study/{systematicStudyId}/protocol/research-question*]: create a research question in the protocol)

[//]: # (- [**PUT**]  */systematic-study/{systematicStudyId}/protocol/research-question/{code}*]: update an existing research question in the protocol)

[//]: # (- [**DELETE**]  */systematic-study/{systematicStudyId}/protocol/research-question/{code}*]: delete an existing research question in the protocol)

[//]: # (- [**GET**]  */systematic-study/{systematicStudyId}/protocol/research-question/{code}*]: get a research question of a given protocol by code)

[//]: # (- [**GET**]  */systematic-study/{systematicStudyId}/protocol/research-question*]: get all research questions in the protocol)

[//]: # ()
[//]: # (##### Eligibility criteria)

[//]: # (- [**POST**]  */systematic-study/{systematicStudyId}/protocol/criteria*]: create a selection criterion in the protocol)

[//]: # (- [**PUT**]  */systematic-study/{systematicStudyId}/protocol/criteria/{code}*]: update an existing selection criterion in the  protocol)

[//]: # (- [**DELETE**]  */systematic-study/{systematicStudyId}/protocol/criteria/{code}*]: delete an existing selection criterion in the  protocol)

[//]: # (- [**GET**]  */systematic-study/{systematicStudyId}/protocol/criteria/{code}*]: get a selection criterion in a given protocol by code)

[//]: # (- [**GET**]  */systematic-study/{systematicStudyId}/protocol/criteria*]: get all selection criteria in the  protocol)

[//]: # ()
[//]: # (##### PICOC)

[//]: # (- [**PUT**]  */systematic-study/{systematicStudyId}/protocol/picoc*]: create or update the picoc of the protocol)

[//]: # (- [**DELETE**]  */systematic-study/{systematicStudyId}/protocol/picoc*]: delete an existing picoc of the protocol)

[//]: # (- [**GET**]  */systematic-study/{systematicStudyId}/protocol/picoc*]: get the picoc of the protocol)

##### Extraction Question:
- **[OK]**[**POST**]  */systematic-study/{systematicStudyId}/protocol/extraction-question/{questionType}*]: create an extraction question in the protocol
- [**PUT**]  */systematic-study/{systematicStudyId}/protocol/extraction-question/{code}*]: update an existing extraction question in the protocol
- [**DELETE**]  */systematic-study/{systematicStudyId}/protocol/extraction-question/{code}*]: delete an existing extraction question in the protocol
- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/protocol/extraction-question/{code}*]: get an extraction question of a given protocol by code
- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/protocol/extraction-question*]: get all extraction questions in the protocol


##### Risk of Bias Question:
- **[OK]**[**POST**]  */systematic-study/{systematicStudyId}/protocol/rob-question/{questionType}*]: create a risk of bias (rob) question in the protocol
- [**PUT**]  */systematic-study/{systematicStudyId}/protocol/rob-question/{code}*]: update an existing risk of bias (rob) question in the protocol
- [**DELETE**]  */systematic-study/{systematicStudyId}/protocol/rob-question/{code}*]: delete an existing risk of bias (rob) question in the protocol
- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/protocol/rob-question/{code}*]: get a risk of bias (rob) question of a given protocol by code
- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/protocol/rob-question*]: get all risk of bias (rob) questions in the protocol

### Search Session:
- [**POST**]  */systematic-study/{systematicStudyId}/search-session/*]: create a search session in the systematic study
- [**PUT**]  */systematic-study/{systematicStudyId}/search-session/{sessionId}*]: update an existing search session of a systematic study
- [**GET**]  */systematic-study/{systematicStudyId}/search-session/{sessionId}*]: get an existing search session of a systematic study
- [**GET**]  */systematic-study/{systematicStudyId}/search-session/*]: get all search sessions of a systematic review

### Study Review:
- **[OK]**[**POST**]  */systematic-study/{systematicStudyId}/study-review/*]: create a study review in the systematic study
- [**PUT**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}*]: update an existing study review of a systematic study
- [**DELETE**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}*]: delete an existing study review of a systematic study
- **[OK]**[**PATCH**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewIdToKeep}/duplicated/{studyReviewIdToIgnore}*]: mark an existing study as duplicated in the systematic study

- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}*]: get an existing study review of a systematic study
- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/study-review/*]: get all existing studies of a systematic review
- **[OK]**[**GET**]  */systematic-study/{systematicStudyId}/search-session/{sessionId}/study-review/*]: get all existing studies of a systematic review search source 

- [**PATCH**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}/extraction-answer*]: update the answer of a given extraction question defined in the protocol
- [**PATCH**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}/rob-answer*]: update the answer of a given risk of bias question defined in the protocol

- **[OK]**[**PATCH**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}/selection*]: update the selection status of study review
- **[OK]**[**PATCH**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}/extraction*]: update the selection status of study review
- **[OK]**[**PATCH**]  */systematic-study/{systematicStudyId}/study-review/{studyReviewId}/priority*]: update the reading priority of study review

## Resources

All requests must check if the user is authenticated and authorized to perform the request. 
If the user is not authenticated, the service must return HTTP 401. If the use is authenticated but has no authority to 
request, the service must return HTTP 403 (forbidden).

### POST

- 201: the resource was created and the response includes a URL to later access it.
- 404: it was not possible to create the resource because some resource ID (e.g., reviewId) in the path does not exist.
- 409: the resource to be included already exists or its properties violate attribute  of an existing resource.

### PUT

- 201: when applicable, the resource was created if not already available and the response includes a URL to later access it.
- 200: the updated was performed and the timestamp and the self link will be returned in response.
- 404: the resource to be updated does not exist **OR** it was not possible to update the resource because some resource ID in the path does not exist (e.g., reviewId)
- 409: some property of the resource being updated violates the attribute uniqueness of an existing resource

### PATCH

- 200: the updated was performed and the timestamp and the self link will be returned in response.
- 404: the resource to be patched does not exist **OR** it was not possible to update the resource because some resource ID in the path does not exist (e.g., reviewId).
- 409: it is not possible to change the resource from the current state to the requested one **OR** some property of the resource being updated violates the attribute uniqueness of an existing resource.

### DELETE

- 200: the resource was deleted and the timestamp and the link to find all resources will be returned in response.
- 404: the resource to be removed does not exist **OR** it was not possible to remove the resource because some resource ID in the path does not exist (e.g., reviewId).

### GET

- 200: the resource is available in the response **OR** a list of resources are available in the response **OR** the request is valid and an empty list is returned in response because no resource matches the query condition. 
- 404: the resource does not exist **OR** it was not possible to find the resource because some resource ID in the path does not exist (e.g., reviewId).



