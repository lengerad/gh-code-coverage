package pb.ghcodecoverage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GhCodeCoverageApplication

fun main(args: Array<String>) {
	runApplication<GhCodeCoverageApplication>(*args)
}
