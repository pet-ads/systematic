package br.all.domain.model.reviewmode

class Split(val reviewPerStudy: Int) : ReviewMode {
    init {
        require(reviewPerStudy > 0) { "Review per study must be greater than zero" }
    }
}
