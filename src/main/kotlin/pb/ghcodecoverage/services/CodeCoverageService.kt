package pb.ghcodecoverage.services

import java.sql.Date
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import pb.ghcodecoverage.clients.GitHubClient
import pb.ghcodecoverage.model.RepositoriesSummary
import pb.ghcodecoverage.model.database.LastUpdate
import pb.ghcodecoverage.repositories.H2Repository

@Service
class CodeCoverageService(
    val gitHubClient: GitHubClient,
    val h2Repository: H2Repository
) {
    val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Periodically runs every day five minutes after midnight, checks whether timestamp doest not exist
     */
    @Scheduled(cron = "0 5 0 * * *")
    fun retrieveGitHubDataDaily() {
        log.info("Running daily cronjob to retrieve data from GitHub repositories.")
        val currentDay = Date.valueOf(LocalDate.now())
        // possibly someone one might endpoint before scheduler -> in that case we wan't to simply skip scheduled task as data are already stored
        val updated = h2Repository.getUpdateByDay(currentDay)
        if (updated != null) {
            log.info("Data were already retrieved today ${updated.lastPull}, no need of running cronjob.")
            return
        }
        storeTodayCodeCoverage()
        log.info("Running daily cronjob to retrieve data from GitHub repositories.")
    }

    /**
     * This method uses GitHubclient to fetch repositories and accordingly fetch their language coverage, supports only current day
     */
    private fun storeTodayCodeCoverage(): LastUpdate {
        val currentDay = Date.valueOf(LocalDate.now())
        val repositories = gitHubClient.getRepositories()
        if (repositories.isEmpty()) {
            log.warn("No repositories found.")
        }
        log.info("Found #${repositories.size} repositories.")
        val repositoriesWithLanguage = repositories.associate {
            it.name to gitHubClient.getRepositoryLanguages(it.name)
        }
        val totalBytes = h2Repository.storeRepositories(repositoriesWithLanguage, currentDay)
        val response = h2Repository.storeTodaySummary(currentDay, totalBytes)
        log.info("Data retrieved with total size ${response.totalBytes} by the ${response.lastPull}.")
        return response
    }

    /**
     * Method retrieves code coverage by the date specified - if the date is today and data are missing -> we fetch them and serve right away,
     * otherwise we're not able to pull the data retrospectively - [IllegalStateException] is thrown.
     * Returns [RepositoriesSummary] that contains total sum of bytes for whole project and language and their size for all repositories.
     */
    @Throws(IllegalStateException::class)
    fun retrieveCodeCoverage(date: Date): RepositoriesSummary {
        var updated = h2Repository.getUpdateByDay(date)
        // if there is no timestamp stored -> we need to fetch new data
        if (updated == null) {
            log.info("No timestamp found for date $date specified.")
            val currentDay = Date.valueOf(LocalDate.now())
            // if the day is today -> we're able to fetch it, otherwise it's not possible to fetch it retrospectively
            if (date != currentDay) throw IllegalStateException("No language coverage data for date $date specified.")
            updated = storeTodayCodeCoverage()
            log.info("Fetched new data for today date $date.")
        } else log.info("Found existing timestamp for the $date -> fetching repositories data from the database.")
        val repositories = h2Repository.getRepositoriesByDate(date)
        if (repositories.isEmpty()) {
            log.error("No language coverage data found for the $date specified.")
            throw IllegalStateException("Missing language coverage data found for the date $date but timestamp with ${updated.lastPull} found.")
        } else log.info("Found #${repositories.size} rows related to $date date.")
        return RepositoriesSummary(updated, repositories)
    }
}
