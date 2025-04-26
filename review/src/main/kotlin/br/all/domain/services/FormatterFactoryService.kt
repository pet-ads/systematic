package br.all.domain.services

import br.all.application.protocol.repository.ProtocolDto

class FormatterFactoryService (
    private val csvFormatterService: CsvFormatterService
) {
    fun format(
        type: String,
        protocol: ProtocolDto,
    ): String {
        return when (type) {
            "csv" -> csvFormatterService.formatProtocol(protocol)
            else -> {"Unsupported format $type"}
        }
    }
}