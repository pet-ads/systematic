package br.all.domain.shared.exception.ris

class RisUnknownEntryTypeException(val typeName: String) :
    RisParseException("Unknown or unsupported RIS entry type: '$typeName'")