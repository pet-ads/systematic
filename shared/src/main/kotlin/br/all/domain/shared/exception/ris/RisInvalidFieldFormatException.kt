package br.all.domain.shared.exception.ris

class RisInvalidFieldFormatException(val fieldName: String, val value: String, val expectedFormat: String) :
    RisParseException("Invalid format for field '$fieldName'. Expected $expectedFormat, but got '$value'.")