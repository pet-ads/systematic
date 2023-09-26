package br.all.domain.shared.ddd

import java.util.*

data class ProtocolId(override val value: UUID) : UUIDValueObject(value){

}
