    package br.all.domain.model.protocol.question

    import br.all.domain.model.protocol.ProtocolId
    import br.all.domain.shared.ddd.Notification
    import kotlin.IllegalArgumentException

    class NumberScale(
        private val questionId: QuestionId,
        protocolId: ProtocolId,
        code: String,
        description: String,
        private val higher: Int,
        private val lower: Int,
        ):
        Question<Int>(questionId, protocolId, code, description) {
        override fun validateAnswer(value: Int?): Int {
            if (value == null)
                throw IllegalArgumentException("Answer can not be null.")
            if (value > higher || value < lower)
                throw IllegalArgumentException("Answer is out of bounds [$lower, $higher]: $value")
            return value
        }

        override fun validate(): Notification {
            val notification = super.validate()
            if (higher > lower)
                notification.addError("Val \"Higher\" must have a higher value than Val\"lower\".")
            return notification
        }

        override fun toString(): String {
            return "NumberScale(questionId=$questionId, higher=$higher, lower=$lower)"
        }
    }