package br.all.domain.shared.exception.bibtex

class BibtexMalformedEntryException(reason: String) :
    BibtexParseException("Malformed BibTeX entry: $reason")