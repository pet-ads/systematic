package br.all.application.question.repository
interface QuestionRepository <T, K> {
    fun create(dto: T)
    fun findById(id: K): T
    fun update(dto: T)
}