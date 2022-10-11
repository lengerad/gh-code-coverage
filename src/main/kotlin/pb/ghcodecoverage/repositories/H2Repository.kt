package pb.ghcodecoverage.repositories

import java.sql.Date
import javax.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pb.ghcodecoverage.model.database.LastUpdate
import pb.ghcodecoverage.model.database.RepositoryLanguage

/**
 * Repository that handles all database/persistence logic -> retrieving and storing results from H2 database
 */
@Repository
class H2Repository(
    val em: EntityManager
) {
    /**
     * Retrieves timestamp from the database by the date specified and returns null if timestamp does not exist -> implication that repository wasn't scanned that day
     */
    fun getUpdateByDay(currentDay: Date): LastUpdate? = em.createQuery("SELECT lu FROM LastUpdate lu WHERE lastPull = '${currentDay}'", LastUpdate::class.java).resultList.firstOrNull()

    /**
     * Returns language coverage for the repositories by the date specified, returns empty list if missing
     */
    fun getRepositoriesByDate(date: Date): List<RepositoryLanguage> = em.createQuery(
            "SELECT rl FROM RepositoryLanguage rl WHERE lastPull = '$date'",
            RepositoryLanguage::class.java
        ).resultList

    /**
     * Persist all languages for all repositories in project with current timestamp to be able to track history + sums bytes and return total
     */
    @Transactional
    fun storeRepositories(languagesByRepository: List<RepositoryLanguage>): Long {
        var totalBytes: Long = 0
        languagesByRepository.forEach { repository -> totalBytes += repository.bytes
                em.merge(repository)
        }
        return totalBytes
    }

    /**
     * Persist timestamp + bytes size that should represent total size of languages used in all projects.
     */
    @Transactional
    fun storeTodaySummary(currentDay: Date, bytes: Long): LastUpdate = em.merge(LastUpdate(null, currentDay, bytes))
}
