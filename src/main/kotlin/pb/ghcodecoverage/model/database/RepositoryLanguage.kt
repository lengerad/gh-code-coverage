package pb.ghcodecoverage.model.database

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class RepositoryLanguage (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: String?,
    val lastPull: Date,
    val repositoryName: String,
    val language: String,
    val bytes: Long
)
