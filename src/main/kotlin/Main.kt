import Language.*
import java.util.function.Function
import java.util.stream.Collectors.*
import kotlin.streams.asStream

fun main() {
    val inputData = InputData(
        setOf(
            Developer("Thomas", setOf(JAVA, RUBY)),
            Developer("Dom", setOf(RUBY)),
            Developer("Edgard", setOf(JAVA, ELIXIR, RUBY, C, CSHARP, JAVASCRIPT)),
            Developer("Malian", setOf(JAVA, ELIXIR, C, CSHARP, JAVASCRIPT)),
            Developer("Cédric", setOf(JAVASCRIPT)),
            Developer("Camille", setOf(JAVA)),
            Developer("Nicolas", setOf(JAVA)),
        )
    )
    val teamMaker = TeamMaker()
    println(teamMaker.makeTeams(inputData).prettyPrint())
}

fun Set<Team>.prettyPrint(): String = stream().map { it.toString() }.collect(joining("\n"))

/**
 * Currently brute-forcing one possibility.
 * It can fail to find a teaming even when there is one.
 * It doesn't show the multiple possibilities if any.
 */
class TeamMaker {
    private var teams = mutableSetOf<Team>()

    fun makeTeams(inputData: InputData): Set<Team> {
        if (inputData.developers.isEmpty()) return teams
        val team = inputData.makeTeam()
        teams.add(team)
        val remainingDevelopers = inputData.developers.stream()
            .filter { !team.developers.contains(it.name) }
            .collect(toSet())
        val remainingInputData = InputData(remainingDevelopers)
        return makeTeams(remainingInputData)
    }
}

class InputData(val developers: Set<Developer>) {

    /**
     * Number of times this language appears in the developers choices
     */
    val possibilitiesByLanguage = computePossibilitiesByLanguage()

    /**
     * Number of pairing possibilities, whatever the language or the teaming
     */
    val possibilitiesByDeveloper = computePossibilitiesByDeveloper()

    fun makeTeam(): Team {
        if (developers.count() == 3) {
            val commonLanguage = developers.first().languages.stream().filter { language ->
                developers.stream().map { it.languages }.allMatch { it.contains(language) }
            }.findFirst() ?: throw Exception("The algo hasn't found a relevant teaming")
            return Team(developers.map { it.name }.toSet(), commonLanguage.get())
        }
        val dev1 = getDeveloperNameWithFewestTeamPossibilities()
        val language = getLanguagesWithFewestPossibilitiesByDeveloper(dev1)
        val dev2 = findDeveloperToTeamWith(dev1, language)
        return Team(setOf(dev1, dev2), language)
    }

    fun findDeveloperToTeamWith(name: String, language: Language) =
        developers.filter { it.languages.contains(language) && name != it.name }
            .minByOrNull { possibilitiesByDeveloper[it.name]!! }!!.name

    fun getDeveloperNameWithFewestTeamPossibilities() =
        possibilitiesByDeveloper.minByOrNull { it.value }?.key ?: throw Exception("No more developer available ?")

    fun getLanguagesWithFewestPossibilitiesByDeveloper(name: String) =
        possibilitiesByLanguage.filter { getDeveloperLanguages(name).contains(it.key) }
            .filter { it.value.compareTo(1) == 1 }
            .minByOrNull { it.value }?.key ?: throw Exception("The algo hasn't found a relevant teaming")

    fun getDeveloperLanguages(name: String) = getDeveloper(name).languages
    private fun getDeveloper(name: String) = developers.first { name == it.name }

    private fun computePossibilitiesByLanguage(): Map<Language, Long> =
        Language.values().asSequence().asStream()
            .collect(toMap(Function.identity(), languagesCounter()))

    private fun computePossibilitiesByDeveloper(): Map<String, Long> =
        developers.associate { it.name to possibilitiesByDeveloperCounter(it.name) }

    private fun possibilitiesByDeveloperCounter(name: String): Long =
        getDeveloperLanguages(name).map { (possibilitiesByLanguage[it] ?: 0) - 1 }
            .reduce { acc, l -> acc.plus(l) }

    private fun languagesCounter(): (Language) -> Long =
        { language ->
            developers.stream()
                .filter { it.languages.contains(language) }
                .count()
        }
}

data class Team(
    val developers: Set<String>,
    val language: Language
) {
    override fun toString(): String {
        return developers.stream().collect(joining(" and ")) + " in " + language
    }
}

data class Developer(val name: String, val languages: Set<Language>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Developer

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

enum class Language {
    JAVA, ELIXIR, RUBY, C, CSHARP, JAVASCRIPT, PYTHON
}