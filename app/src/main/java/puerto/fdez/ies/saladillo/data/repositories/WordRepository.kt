package puerto.fdez.ies.saladillo.data.repositories

import puerto.fdez.ies.saladillo.data.database.entities.WordDao
import puerto.fdez.ies.saladillo.data.database.entities.WordEntity

class WordRepository(private val dao: WordDao) {

    suspend fun getRandomWord(): WordEntity = dao.getRandomWord()

    suspend fun getRandomWords(count: Int): List<WordEntity> = dao.getRandomWords(count)

    //Mirar si hay tiempo para sacarlo a un json externo
    suspend fun preloadIfEmpty() {
        if (dao.countWords() == 0) {
            val initialWords = listOf(
                WordEntity(word = "ANDROID", hint = "Mobile OS", definition = "An open-source mobile operating system developed by Google."),
                WordEntity(word = "KOTLIN", hint = "Programming language", definition = "A modern, concise programming language used for Android development."),
                WordEntity(word = "JAVA", hint = "Programming language", definition = "A widely-used, class-based, object-oriented programming language."),
                WordEntity(word = "COMPOSE", hint = "UI toolkit", definition = "Modern declarative UI toolkit for Android."),
                WordEntity(word = "DATABASE", hint = "Stores data", definition = "An organized collection of structured information."),
                WordEntity(word = "ALGORITHM", hint = "Step by step", definition = "A process or set of rules to be followed in problem-solving."),
                WordEntity(word = "FUNCTION", hint = "Code block", definition = "A block of code that performs a specific task."),
                WordEntity(word = "VARIABLE", hint = "Stores value", definition = "A container for storing data values."),
                WordEntity(word = "OBJECT", hint = "OOP concept", definition = "An instance of a class."),
                WordEntity(word = "CLASS", hint = "Blueprint", definition = "A template for creating objects."),
                WordEntity(word = "INHERITANCE", hint = "OOP feature", definition = "Mechanism where one class acquires properties of another."),
                WordEntity(word = "POLYMORPHISM", hint = "OOP feature", definition = "Ability to take many forms."),
                WordEntity(word = "ENCAPSULATION", hint = "OOP feature", definition = "Bundling data with methods."),
                WordEntity(word = "RECURSION", hint = "Function calls itself", definition = "A function calling itself repeatedly."),
                WordEntity(word = "STACK", hint = "LIFO", definition = "Data structure following Last In First Out."),
                WordEntity(word = "QUEUE", hint = "FIFO", definition = "Data structure following First In First Out."),
                WordEntity(word = "ARRAY", hint = "Indexed collection", definition = "A collection of items stored at contiguous memory locations."),
                WordEntity(word = "LIST", hint = "Collection", definition = "An ordered collection of elements."),
                WordEntity(word = "MAP", hint = "Key-Value", definition = "A collection of key-value pairs."),
                WordEntity(word = "COROUTINE", hint = "Async programming", definition = "Lightweight threads used in Kotlin for asynchronous programming."),
                WordEntity(word = "THREAD", hint = "Concurrent execution", definition = "A sequence of executable instructions."),
                WordEntity(word = "INTENT", hint = "Android component", definition = "An abstract description of an operation to be performed."),
                WordEntity(word = "SERVICE", hint = "Android component", definition = "A component that performs long-running operations in the background."),
                WordEntity(word = "ACTIVITY", hint = "Android component", definition = "A single screen in an Android app."),
                WordEntity(word = "FRAGMENT", hint = "UI component", definition = "A portion of user interface in an Activity."),
                WordEntity(word = "NAVIGATION", hint = "App flow", definition = "Process of moving between different parts of an app."),
                WordEntity(word = "VIEWMODEL", hint = "MVVM pattern", definition = "Component designed to manage and store UI-related data."),
                WordEntity(word = "LIVEDATA", hint = "Observable data", definition = "A data holder class that can be observed."),
                WordEntity(word = "ROOM", hint = "Database library", definition = "Persistence library providing an abstraction layer over SQLite."),
                WordEntity(word = "RETROFIT", hint = "Networking", definition = "A type-safe HTTP client for Android and Java."),
                WordEntity(word = "JSON", hint = "Data format", definition = "JavaScript Object Notation, a lightweight data-interchange format."),
                WordEntity(word = "API", hint = "Interface", definition = "Application Programming Interface."),
                WordEntity(word = "GRADLE", hint = "Build tool", definition = "Build automation tool used for Android projects."),
                WordEntity(word = "DEPENDENCY", hint = "Library", definition = "External code your project relies on."),
                WordEntity(word = "GIT", hint = "Version control", definition = "A distributed version control system."),
                WordEntity(word = "GITHUB", hint = "Hosting service", definition = "A platform for version control and collaboration."),
                WordEntity(word = "DEBUGGING", hint = "Fix errors", definition = "The process of identifying and removing errors from code."),
                WordEntity(word = "UNITTEST", hint = "Code testing", definition = "A type of software testing where individual units of code are tested."),
            )

            dao.insertAll(initialWords)
        }
    }


}

