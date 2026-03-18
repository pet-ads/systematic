import br.all.infrastructure.user.TokenStatus
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime

import java.util.UUID;

@Entity
@Table(name = "PASSWORD_TOKENS")
class UserPasswordTokenEntity {
    @Id var id: UUID,
    var tokenId: UUID,
    var status: TokenStatus
    var hour: LocalDateTime
    var language: String
}