package br.all.application.protocol.create

import br.all.application.protocol.shared.ProtocolResponseModel
import java.util.*

interface CreateProtocolService {
    fun create(reviewId: UUID, requestModel: ProtocolRequestModel): ProtocolResponseModel
}
