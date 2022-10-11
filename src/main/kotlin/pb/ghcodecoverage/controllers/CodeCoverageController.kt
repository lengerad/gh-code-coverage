package pb.ghcodecoverage.controllers

import java.sql.Date
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pb.ghcodecoverage.model.RepositoriesSummary
import pb.ghcodecoverage.services.CodeCoverageService

/**
 * Controller that provides endpoint for fetching language code coverage for Productboard project by date specified
 */
@RestController
class CodeCoverageController(
    val codeCoverageService: CodeCoverageService
) {
    val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/fetchCodeCoverage")
    fun fetchCodeCoverage(@RequestParam date: Date): Map<String, Float> {
        try {
            log.info("Requesting Productboard language coverage for $date.")
            val response = codeCoverageService.retrieveCodeCoverage(date)
            return countCoverage(response)
        } catch (exception: Exception) {
            // since there is only single endpoint I decided to use simple ResponseStatus for handling exceptions, otherwise I would go for @ControllerAdvice to handle it globally with more granularity
            val message = "Unable to fetch code coverage data for $date specified due to ${exception.message}"
            when (exception) {
                is IllegalArgumentException -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, message, exception);
                else -> throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, exception);
            }
        }
    }

    private fun countCoverage(summary: RepositoriesSummary): Map<String, Float> {
        val totalSize = summary.summary.totalBytes
        val repositories = summary.repositories
        // first group records by the language -> that creates Map<String, ArrayList<RepositoryLanguage>> where key is language and value is list of all of its representations with size
        val grouped = repositories.groupBy { repository -> repository.language }
        // then we want to sum values per language and count its representation according to the total of all repositories and languages
        return grouped.map { languageGroup -> languageGroup.key to languageGroup.value.sumOf { it.bytes } }
            // once summed we need to count the percentage according to the total size
            .associate { it.first to (it.second.toFloat()/totalSize.toFloat() * 100f) }
    }
}
