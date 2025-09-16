package br.all.domain.shared.exception.bibtex

class MalformedEntryException(reason: String) :
    BibtexParseException("Malformed BibTeX entry: $reason")