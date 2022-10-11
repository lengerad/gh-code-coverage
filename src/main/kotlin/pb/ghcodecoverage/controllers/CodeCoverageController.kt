package pb.ghcodecoverage.controllers

import java.sql.Date
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
        log.info("Requesting Productboard language coverage for $date.")
        val response = codeCoverageService.retrieveCodeCoverage(date)
        return countCoverage(response)
    }

    private fun countCoverage(summary: RepositoriesSummary): Map<String, Float> {
        val totalSize = summary.summary.totalBytes
        val repositories = summary.repositories
        // first group records by the language
        val grouped = repositories.groupBy { repository -> repository.language }
        // then we want to sum values per language and count its representation according to the total of all repositories and languages
        return grouped.map { it.key to it.value.sumOf { it.bytes } }.associate { it.first to (it.second.toFloat()/totalSize.toFloat() * 100f) }
    }
}
