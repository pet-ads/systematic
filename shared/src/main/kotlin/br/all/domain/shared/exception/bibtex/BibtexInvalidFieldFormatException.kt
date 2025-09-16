package br.all.domain.shared.exception.bibtex

class BibtexInvalidFieldFormatException(val fieldName: String, val value: String, val expectedFormat: String) :
    BibtexParseException("Invalid format for field '$fieldName'. Expected $expectedFormat, but got '$value'.")