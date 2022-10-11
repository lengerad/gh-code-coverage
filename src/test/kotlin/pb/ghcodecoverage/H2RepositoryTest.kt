package pb.ghcodecoverage

import java.sql.Date
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pb.ghcodecoverage.model.database.RepositoryLanguage
import pb.ghcodecoverage.repositories.H2Repository

@SpringBootTest
class H2RepositoryTest(@Autowired val h2Repository: H2Repository
) {
    @Test
    fun testStoreTodaySummary() {
        val date = Date.valueOf(LocalDate.now())
        val result = h2Repository.storeTodaySummary(date, 123L)
        val persistedValue = h2Repository.getUpdateByDay(date)
        assert(result == persistedValue)
    }

    @Test
    fun testStoreRepositories() {
        val date = Date.valueOf(LocalDate.now())
        val repositories = listOf(
            RepositoryLanguage(null, date, "repository1", "Kotlin", 13L),
            RepositoryLanguage(null, date, "repository1", "Ruby", 25L),
            RepositoryLanguage(null, date, "repository2", "Kotlin", 17L),
        )
        val result = h2Repository.storeRepositories(repositories)
        val persistedValue = h2Repository.getRepositoriesByDate(date)
        // check that all repositories were persisted
        assert(repositories.size == persistedValue.size)
        // ignore id as it's unknown in the time of creation
        val configuration = RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("id")
            .build()
        repositories.forEach { repository ->
            assertThat(persistedValue).usingRecursiveFieldByFieldElementComparator(configuration).contains(repository)
        }
        assert(result == 55L)
    }
}
