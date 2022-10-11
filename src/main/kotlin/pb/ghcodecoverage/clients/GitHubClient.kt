package pb.ghcodecoverage.clients

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import pb.ghcodecoverage.model.RepositoryResponse

private const val GITHUB_API_BASE = "https://api.github.com"
private const val OWNER = "productboard"
private const val REPOSITORIES_PAGE_SIZE = 30

/**
 * Simple client that handles communication with GitHub.
 * So far supports:
 *  - fetching repositories for single owner
 *  - fetching languages per repository specified
 */
@Repository
class GitHubClient(
    val client: OkHttpClient = OkHttpClient()
) {
    val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Fetches all repositories for owner specified in [OWNER] variable - for the sake of the task assignment I kept it as const val (as it never changes), otherwise it would be probably parameter
     * See documentation here - https://docs.github.com/en/rest/repos/repos#list-organization-repositories
     * Paginated API - page size can be adjusted via [REPOSITORIES_PAGE_SIZE] variable
     */
    @Throws(IllegalStateException::class)
    fun getRepositories(): List<RepositoryResponse> {
        log.info("Retrieving repositories for owner: $OWNER.")
        val allRepositories = mutableListOf<RepositoryResponse>()
        var page = 1
        // here for PB I could simply set limit to 100, but I want to point out that I was aware of pagination, so I added this simple page-fetching with lower limit
        do {
            // TODO: REMOVE BEARER TOKEN
//            val call = client.newCall(Request.Builder().url("$GITHUB_API_BASE/orgs/$OWNER/repos?page=$page&per_pages=$REPOSITORIES_PAGE_SIZE").header("Authorization", "Bearer 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").build())
            val call = client.newCall(Request.Builder().url("$GITHUB_API_BASE/orgs/$OWNER/repos?page=$page&per_pages=$REPOSITORIES_PAGE_SIZE").header("Authorization", "Bearer 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").build())
            val response = call.execute()
            //TODO: example of possible error handling - ideally I would encapsulate call handling to separate method for all client requests
            // right now we interrupt the whole process in case any request fails - that could be adjusted by the needs of the API consumer to possibly store what was fetched so far
            when {
                !response.isSuccessful -> throw IllegalStateException("Unable to retrieve repositories for $OWNER due to ${response.code}: ${response.message}.")
                response.body == null -> throw IllegalStateException("Unable to retrieve repositories for $OWNER due tu null response body.")
            }
            val repositories = jacksonObjectMapper().readValue<List<RepositoryResponse>>(response.body!!.string())
            log.info("Retrieved ${repositories.size} repositories for page: $page.")
            page++
            allRepositories.addAll(repositories)
        } while (repositories.size == REPOSITORIES_PAGE_SIZE)
        return allRepositories
    }

    /**
     * Fetches number of bytes per language for repository specified.
     * According to the API documentation https://docs.github.com/en/rest/repos/repos#list-repository-languages there shouldn't be any limit or pagination
     */
    @Throws(IllegalStateException::class)
    fun getRepositoryLanguages(repositoryName: String): Map<String, Long> {
        log.info("Retrieving language for repository: $repositoryName by owner: $OWNER.")
        val call =
            // TODO: REMOVE BEARER TOKEN
//            client.newCall(Request.Builder().url("$GITHUB_API_BASE/repos/$OWNER/$repositoryName/languages").header("Authorization", "Bearer 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").build())
            client.newCall(Request.Builder().url("$GITHUB_API_BASE/repos/$OWNER/$repositoryName/languages").header("Authorization", "Bearer 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").build())
        val response = call.execute()
        when {
            !response.isSuccessful -> throw IllegalStateException("Unable to retrieve repository: $repositoryName languages due to ${response.code}: ${response.message}.")
            response.body == null -> throw IllegalStateException("Unable to retrieve repository: $repositoryName languages due tu null response body.")
        }
        return jacksonObjectMapper().readValue(response.body!!.string())
    }
}