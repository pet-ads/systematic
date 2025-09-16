package br.all.domain.shared.exception.bibtex

class BibtexMissingRequiredFieldException(val fieldName: String) :
    BibtexParseException("Missing required field: '$fieldName'")