package br.all.domain.shared.exception.ris

class RisMissingRequiredFieldException(val fieldName: String) :
    RisParseException("Missing required field: '$fieldName'")