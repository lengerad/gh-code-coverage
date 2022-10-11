package pb.ghcodecoverage.model.database

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class LastUpdate (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: String?,
    val lastPull: Date,
    val totalBytes: Long
)
