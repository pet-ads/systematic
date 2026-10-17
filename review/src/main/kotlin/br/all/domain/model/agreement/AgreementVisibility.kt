package br.all.domain.model.agreement

enum class AgreementVisibility {
    ALL,
    FINISHED_ONLY,
    NONE;

    companion object {
        val DEFAULT = FINISHED_ONLY
    }
}