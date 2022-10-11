# Service for retrieving PB language represantation

This services allows to retrieve percentage of each language represented in GitHub public repositories.
Full description of the task is [in this notion](https://productboard.notion.site/Kotlin-Java-Technical-Task-Github-API-9a6438b3dda84ca7a1f7a5f210235061).

Services should basically
 - daily retrieve data about language coverage per repositories and persist them
 - provide endpoint for fetching language coverage (percentage representation) by the date specified
 - provide response in following format:
   - ```{ "Ruby": 0.5, "TypeScript": 0.2, "Python": 0.3 }```


## How to run the application
- you can either import it into IDE and run main from `GhCodeCoverageApplicaiton.kt` or use gradle and build app - `gradle clean build run`
- app runs on default port `8080` and on localhost
- I attached database files into GIT, so you can test app even with data from past days (not sure if needed) -> but for a clean run feel free to remove `/data` folder in the root so obtain clean database
- available endpoints:
    - `fetchCodeCoverage?date=YYYY-MM-DD`
    - example of GET call:
        - `http://localhost:8080/fetchCodeCoverage?date=2022-10-11`

### Thoughts related to the task:

1) I wasn't sure whether it should persist data from the past, but I made it possible
2) I wasn't sure whether the service should allow requesting information about coverage from previous days -> right now it's mandatory to provide `date` parameter for requesting coverage data and thus possible to request past data if they exist
3) AFAIK it's not possible to retrieve past data from GitHub API (according to documentation) - so unfortunately if there are no data for date requested -> they cannot be fetched
4) As the persistence layer wasn't specified further - I decided to use simple H2 database with local DB and created really simple schema just for the needs of this solution. I also didn't dig deeper into all the ORM stuff, hopefully it's enough for the sake of this task.
5) I wasn't sure what's the best runnable env for you - so I leave it to be either runnable from IDE or buildable/runnable via Gradle
   - I was thinking about providing some fat-jars or wars but since it wasn't mentioned I hope it's fine to leave it as is
6) In the assignment there was written ` The application should fetch the data daily and persist them` - I wasn't sure if it should fetch regularly or on-demand so I decided to use `cron` and `@Scheduled` to fetch the data daily, hopefully it meets the requirements as intedend
7) I was using `sql.Date` all over the app which isn't ideal but since the task was specified as `day oriented` I stick with that for the sake of simplicity, otherwise I would use pbbly Instant or something timezoned.
8) Since the response was specified as `Map<String, String>` I wasn't able to tie API response with some nicer data class that might have more detailed and adjustable answer.
9) I didn't style error pages or created separated `@ControllerAdvice` -> instead I use `ResponseStatusException` but ideally errors/exceptions should have some default structure and shared handling
10) I didn't rounded the decimal as some languages are having really small amount of representation and it would show them as `0.0` if single decimal was used like in example (sometiems even three decimals), but could be easily adjusted, hopefully it's fine like that 
