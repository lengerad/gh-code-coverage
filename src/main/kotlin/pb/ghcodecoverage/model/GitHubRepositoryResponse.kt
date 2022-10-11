package pb.ghcodecoverage.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Simple DTO to map GitHub repository response
 * As for current solution it's not necessary to retrieve full information I decided to keep just
 *  - id
 *  - name
 *  see full response schema - https://docs.github.com/en/rest/repos/repos
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubRepositoryResponse (
    val id: String,
    val name: String
)
