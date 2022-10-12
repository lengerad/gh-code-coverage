# Service for retrieving PB language representation

This services allows retrieves percentage of each language represented in GitHub Productboard public repositories.
Full description of the task is [in this notion](https://productboard.notion.site/Kotlin-Java-Technical-Task-Github-API-9a6438b3dda84ca7a1f7a5f210235061).

Services should basically
 - daily retrieve data about language coverage per repositories and persist them
 - provide endpoint for fetching language coverage (percentage representation) by the date specified
 - provide response in following format:
   - ```{ "Ruby": 0.5, "TypeScript": 0.2, "Python": 0.3 }```

## How to run the application
- before running, I encourage to fill environment variable `GITHUB_ACCESS_TOKEN` in `.env` file to private access token as GitHub behaviour without authentication header behaved somehow weirdly (sometimes forbidden language pull for some repositories,)
- you can either import it into IDE and run main from `GhCodeCoverageApplicaiton.kt` or use gradle and build app - `gradle clean build run`
- app runs on default port `8080` and on localhost
- unfortunately GitHUb has quite strict API rules and as API doesn't allow fetching languages in batches (AFAIK) and some repositories are even under stricter rules (as I was able to pull like 50+ repositories and some specific ones failed)
- I attached database files into GIT, so you can test app even with data from past days (not sure if needed) -> but for a clean run feel free to remove `/data` folder in the root to obtain clean database
- I decide to round percentage coverage UP so it gives more relevant numbers (IMHO), but it might be adjusted according to the needs of precision
- available endpoints:
    - `fetchCodeCoverage?date=YYYY-MM-DD`
    - example of GET call:
        - `http://localhost:8080/fetchCodeCoverage?date=2022-10-11`
    - example of response:
        - ```
          {
          "JavaScript": 12.3,
          "Shell": 0.4,
          "C++": 0.3,
          "TypeScript": 48.2
          }
          ```
- in order to run tests you can simply call `gradle cleanTest test`

### Thoughts related to the task:

1) I wasn't sure whether the service should allow requesting information about coverage from previous days -> right now it's mandatory to provide `date` parameter for requesting coverage data and thus possible to request past data if they exist
3) AFAIK it's not possible to retrieve past data from GitHub API (according to documentation) - so unfortunately if there are no data for date requested -> they cannot be fetched
4) As the persistence layer wasn't specified further - I decided to use simple H2 database with local DB and created really simple schema just for the needs of this solution. I also didn't dig deeper into all the ORM stuff, hopefully it's enough for the sake of this task.
5) I wasn't sure what's the best runnable env for you - so I leave it to be either runnable from IDE or buildable/runnable via Gradle
   - I was thinking about providing some fat-jars or wars but since it wasn't mentioned I hope it's fine to leave it as is
6) In the assignment there was written ` The application should fetch the data daily and persist them` - I wasn't sure if it should fetch regularly or on-demand so I decided to use `cron` and `@Scheduled` to fetch the data daily, hopefully it meets the requirements as intedend
7) I was using `sql.Date` all over the app which isn't ideal but since the task was specified as `day oriented` I stick with that for the sake of simplicity, otherwise I would use probably Instant or something timezoned.
8) Since the response was specified as `Map<String, Float>` I wasn't able to tie API response with some nicer data class that might have more detailed and adjustable answer.
9) I didn't style error pages or created separated `@ControllerAdvice` -> instead I use `ResponseStatusException`, but ideally errors/exceptions should have some default structure and shared handling
10) I rounded the decimals up as some languages are having small amount of representation - hopefully it's fine like that even tho we're loosing some precision but I didn't find any related info about rounding in the assignment.
11) Tests might be the weaker part of this assignment, but for such a small app I wasn't sure what else to test to not just "make tests for tests", but I rather mention it here as I'm aware of it.
