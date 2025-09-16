package br.all.domain.shared.exception.bibtex

class BibtexUnknownEntryTypeException(val typeName: String) :
    BibtexParseException("Unknown BibTeX entry type: '$typeName'")