package kombient

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@ExtendWith(SpringExtension::class)
@ActiveProfiles(SpringProfiles.FNT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class])
class FunctionalTest {

    @Test
    fun bootTest() {

    }

}
