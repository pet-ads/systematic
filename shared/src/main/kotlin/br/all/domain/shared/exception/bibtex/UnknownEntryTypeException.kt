package br.all.domain.shared.exception.bibtex

class UnknownEntryTypeException(val typeName: String) :
    BibtexParseException("Unknown BibTeX entry type: '$typeName'")