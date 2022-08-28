import Language.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MainTest {

    private val inputData = InputData(
        setOf(
            Developer("Thomas", setOf(JAVA, RUBY)),
            Developer("Dom", setOf(RUBY)),
            Developer("Edgard", setOf(JAVA, ELIXIR, RUBY, C, CSHARP, JAVASCRIPT)),
            Developer("Malian", setOf(JAVA, ELIXIR, C, CSHARP, JAVASCRIPT)),
            Developer("Cédric", setOf(JAVASCRIPT)),
            Developer("Camille", setOf(JAVA))
        )
    )

    @Test
    fun `should make the right teams`() {
        val simpleInputData = InputData(
            setOf(
                Developer("Dev1", setOf(JAVA)),
                Developer("Dev2", setOf(JAVA, RUBY)),
                Developer("Dev3", setOf(JAVA, RUBY, ELIXIR)),
                Developer("Dev4", setOf(JAVA, RUBY, ELIXIR, C)),
                Developer("Dev5", setOf(JAVA, RUBY, ELIXIR, C, CSHARP)),
                Developer("Dev6", setOf(JAVA, RUBY, ELIXIR, C, CSHARP, PYTHON)),
            )
        )
        val teamMaker = TeamMaker()
        val actualTeams = teamMaker.makeTeams(simpleInputData)
        assertTrue(actualTeams.stream().anyMatch { it.developers == setOf("Dev1", "Dev2") })
        assertTrue(actualTeams.stream().anyMatch { it.developers == setOf("Dev3", "Dev4") })
        assertTrue(actualTeams.stream().anyMatch { it.developers == setOf("Dev5", "Dev6") })
    }

    @Test
    fun `test make a first team`() {
        val teamPossibility1 = Team(setOf("Cédric", "Malian"), JAVASCRIPT)
        val teamPossibility2 = Team(setOf("Dom", "Thomas"), RUBY)
        assertTrue(setOf(teamPossibility1, teamPossibility2).contains(inputData.makeTeam()))
    }

    @Test
    fun `test solo case`() {
        val inputData = InputData(setOf(Developer("Dev1", setOf(JAVA))))
        assertTrue(inputData.possibilitiesByDeveloper.isNotEmpty())
        assertEquals(1, inputData.possibilitiesByLanguage[JAVA])
    }

    @Test
    fun `test empty case`() {
        val inputData = InputData(setOf())
        assertTrue(inputData.possibilitiesByDeveloper.isEmpty())
        inputData.possibilitiesByLanguage.forEach { (_, v) -> assertEquals(0, v) }
    }

    @Test
    fun `should find the right developer to team with`() {
        assertEquals("Malian", inputData.findDeveloperToTeamWith("Cédric", JAVASCRIPT))
        assertEquals("Dom", inputData.findDeveloperToTeamWith("Thomas", RUBY))
    }

    @Test
    fun `team of 3`() {
        val inputData = InputData(
            setOf(
                Developer("Edgard", setOf(JAVA, ELIXIR, RUBY, C, CSHARP, JAVASCRIPT)),
                Developer("Cédric", setOf(JAVASCRIPT, RUBY)),
                Developer("Nicolas", setOf(JAVASCRIPT, C)),
                )
        )
        val expectedTeam = Team(setOf("Edgard", "Cédric", "Nicolas"), JAVASCRIPT)
        assertEquals(expectedTeam, inputData.makeTeam())
    }

    @Test
    fun `should find ignore languages with zero pairing possibility`() {
        val inputData = InputData(
            setOf(
                Developer("Edgard", setOf(JAVA, ELIXIR, RUBY, C, CSHARP, JAVASCRIPT)),
                Developer("Cédric", setOf(JAVASCRIPT)),
            )
        )
        assertEquals(JAVASCRIPT, inputData.getLanguagesWithFewestPossibilitiesByDeveloper("Edgard"))
    }

    @Test
    fun `should find the language with fewest possibilities`() {
        assertEquals(RUBY, inputData.getLanguagesWithFewestPossibilitiesByDeveloper("Thomas"))
        assertTrue(
            setOf(ELIXIR, C, CSHARP).contains(
                inputData.getLanguagesWithFewestPossibilitiesByDeveloper("Edgard")
            )
        )
    }

    @Test
    fun `should find the developer with fewest possibilities`() {
        assertTrue(setOf("Cédric", "Dom").contains(inputData.getDeveloperNameWithFewestTeamPossibilities()))
    }

    @Test
    fun `test TeamMaker computePossibilitiesByDeveloper`() {
        assertEquals(2, inputData.possibilitiesByDeveloper["Dom"])
        assertEquals(3, inputData.possibilitiesByDeveloper["Camille"])
        assertEquals(2, inputData.possibilitiesByDeveloper["Cédric"])
    }

    @Test
    fun `test getDeveloperByName`() {
        assertEquals(setOf(JAVA, RUBY), inputData.getDeveloperLanguages("Thomas"))
    }

    @Test
    fun `test TeamMaker computePossibilitiesByLanguage`() {
        assertEquals(4, inputData.possibilitiesByLanguage[JAVA])
        assertEquals(3, inputData.possibilitiesByLanguage[RUBY])
        assertEquals(3, inputData.possibilitiesByLanguage[JAVASCRIPT])
    }

    @Test
    fun `Set contains should be true if name is present`() {
        val thomas = Developer("Thomas", setOf())
        assertTrue(inputData.developers.contains(thomas))
    }
}