package br.all.utils

import br.all.protocol.controller.ProtocolController
import br.all.protocol.requests.PutRequest
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.linkTo
import java.util.UUID

class LinksFactory {

    fun findProtocol(systematicStudyId: UUID): Link =
        linkTo<ProtocolController> { findById(systematicStudyId) }.withRel("find-protocol").withType("GET")

    fun updateProtocol(systematicStudyId: UUID): Link =
        linkTo<ProtocolController> {
            putProtocol(
                systematicStudyId,
                request = PutRequest()
            )
        }.withRel("update-protocol").withType("PUT")
}