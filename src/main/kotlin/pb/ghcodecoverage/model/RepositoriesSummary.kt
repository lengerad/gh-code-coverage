package pb.ghcodecoverage.model

import pb.ghcodecoverage.model.database.LastUpdate
import pb.ghcodecoverage.model.database.RepositoryLanguage

data class RepositoriesSummary(
    val summary: LastUpdate,
    val repositories: List<RepositoryLanguage>
)
