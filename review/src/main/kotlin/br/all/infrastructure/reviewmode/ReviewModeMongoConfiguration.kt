package br.all.infrastructure.reviewmode

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

@Configuration
class ReviewModeMongoConfiguration(
    mappingContext: MongoMappingContext
) {
    init {
        mappingContext.getPersistentEntity(SplitDocument::class.java)
        mappingContext.getPersistentEntity(ReplicationDocument::class.java)
    }
}