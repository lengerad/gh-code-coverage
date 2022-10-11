package pb.ghcodecoverage

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import java.sql.Date
import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pb.ghcodecoverage.model.RepositoriesSummary
import pb.ghcodecoverage.model.database.LastUpdate
import pb.ghcodecoverage.model.database.RepositoryLanguage
import pb.ghcodecoverage.services.CodeCoverageService

@WebMvcTest
class CodeCoverageControllerTests(
    @Autowired val mockMvc: MockMvc
) {
    @MockkBean
    lateinit var codeCoverageService: CodeCoverageService

    /**
     * Check response structure + verify that all languages are present + check that value is represented as percentage according total
     */
    @Test
    fun testFetchCodeCover() {
        val date = Date.valueOf(LocalDate.now())
        every { codeCoverageService.retrieveCodeCoverage(any()) } returns RepositoriesSummary(
            LastUpdate("test", date, 1200L),
            listOf(RepositoryLanguage("test", date, "repository-test", "Kotlin", 600L),
                RepositoryLanguage("test-2", date, "repository-test2", "Ruby", 400L),
                RepositoryLanguage("test-3", date, "repository-test2", "Kotlin", 200L)
            )
        )
        mockMvc.perform(get("/fetchCodeCoverage?date=$date"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.Kotlin").exists())
            .andExpect(jsonPath("$.Kotlin").value(66))
            .andExpect(jsonPath("$.Ruby").exists())
            .andExpect(jsonPath("$.Ruby").value(33))
    }

    @Test
    fun testHandlingOfIllegalArgumentThrow() {
        val date = Date.valueOf(LocalDate.now())
        every { codeCoverageService.retrieveCodeCoverage(any()) } throws IllegalArgumentException()
        mockMvc.perform(get("/fetchCodeCoverage?date=$date"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testHandlingOfIllegalStateThrown() {
        val date = Date.valueOf(LocalDate.now())
        every { codeCoverageService.retrieveCodeCoverage(any()) } throws IllegalStateException()
        mockMvc.perform(get("/fetchCodeCoverage?date=$date"))
            .andExpect(status().isInternalServerError)
    }
}