package br.all.domain.shared.exception.bibtex

class MissingRequiredFieldException(val fieldName: String) :
    BibtexParseException("Missing required field: '$fieldName'")