package br.all.infrastructure.shared

import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service


@Service
class SequenceGeneratorService (private val mongoOperations: MongoOperations){
    fun next(sequenceName: String): Long {
        val query = query(Criteria.where("_id").`is`(sequenceName))
        val update = Update().inc("sequence", 1)
        val options = FindAndModifyOptions.options().returnNew(true).upsert(true)
        val counter = mongoOperations.findAndModify(query, update, options, DatabaseSequence::class.java)
        return counter?.sequence ?: 1L
    }

    fun reset(sequenceName: String) {
        val query = query(Criteria.where("_id").`is`(sequenceName))
        val update = Update().set("sequence", 1)
        val options = FindAndModifyOptions.options().returnNew(true).upsert(true)
        mongoOperations.findAndModify(query, update, options, DatabaseSequence::class.java)
    }

}