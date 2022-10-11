package pb.ghcodecoverage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GhCodeCoverageApplication

fun main(args: Array<String>) {
	runApplication<GhCodeCoverageApplication>(*args)
}
