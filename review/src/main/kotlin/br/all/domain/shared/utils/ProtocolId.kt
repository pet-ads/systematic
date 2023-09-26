package br.all.domain.shared.utils

import br.all.domain.shared.utils.UUIDValueObject
import java.util.*

//TODO: To remove. This class is already implemented in its own aggregate (model/protocol)
data class ProtocolId(override val value: UUID) : UUIDValueObject(value){

}
