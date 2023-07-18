import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication

class CarOrderWorkflowAcceptanceTest {

    @Test
    fun stuff(){
        assertTrue(true)
        SpringApplication.run(Conductor.class, args);
    }

}