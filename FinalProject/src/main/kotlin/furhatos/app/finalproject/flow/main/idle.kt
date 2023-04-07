package furhatos.app.finalproject.flow.main



import furhatos.app.finalproject.flow.Parent
import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.voice.AzureVoice
import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.Color
import furhatos.gestures.Gestures
import furhatos.records.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import furhatos.nlu.common.Number
import furhatos.skills.Skill
import furhatos.flow.kotlin.*
import furhatos.records.Location
import javafx.application.Application.launch
import kotlin.random.Random
import kotlinx.coroutines.*
import java.util.Timer
import kotlin.concurrent.fixedRateTimer


var guessedNumber = 0
var Low: Boolean = false
var High: Boolean = false
var nameglobal = ""
var namecount = 1
var hasnoname: Boolean = false
var lowerBound = 10
var upperBound = 100
var suggestedNumber: Int = 0
var highsuggestion = ""
var lowsuggestion = ""
var correctwinstatementl = ""
var correctwinstatementh = ""
var gamecount = 0
var numberofguesses = 0
var switchL = 0
var switchH = 0
var mostRecentUser: User? = null
var randomNumber: Int = lowerBound
var repeatsuggestion = ""
var repeatnumber = 0
var firstgameguess: Boolean = true
var firstgameguess1: Boolean = false
var firstgameguess2: Boolean = false
var outofrangecount: Int = 0
fun generateRandomNumber(min: Int, max: Int): Int {
    return Random.nextInt(min, max + 1)
}

fun generateSuggestedNumber(min: Int, max: Int): Int {
    return (min + max) / 2
}
fun LowGazeActions(): State = state{
    onEntry{
            if (mostRecentUser != null) {
                val rand = Random.nextInt(5)
                when (rand) {
                    0 -> furhat.attend(mostRecentUser!!)
                    1 -> furhat.attend(location = Location.DOWN_RIGHT)
                    2 -> furhat.attend(location = Location.UP_LEFT)
                    3 -> furhat.attend(location = Location.UP_RIGHT)
                    4 -> furhat.attend(location = Location.DOWN_LEFT)
                }

            }
        terminate()
    }
}

val BackgroundGamesHigh: State = state {
    onUserEnter(instant = true) {
        mostRecentUser = it
        if (!furhat.isSpeaking) {
            if (mostRecentUser != null) {
                furhat.attend(mostRecentUser!!)
            }
        }
    }
    onButton("Restart Scenario If Error (ERROR)", color = Color.Red) {
        furhat.say("Sorry, I was not paying attention. Let me start over.")
        goto(HighGameRepeat())
    }
    onButton("If They Ask The Robot To Repeat Themselves (ERROR)", color = Color.Red) {
        furhat.say("Sure, I will repeat myself.")
        goto(HighGameRepeat())
    }
    onButton("If They Ask The Robot For More Feedback After First Suggestion (ERROR)", color = Color.Red) {
        furhat.say("Sorry, I don't have much more to say on this guess.")
    }
    onButton("If Participant Does Not Choose A Number (ERROR)", color = Color.Red) {
        furhat.say("I suggest we follow the rules and pick a number")
        goto(HighGameListen)

    }
    onButton("Did Not Hear Or Understand Participants Answer (ERROR)", color = Color.Red) {
        furhat.say({
            +voice?.style("Sorry I didnt hear what you said, what number did you guess?", AzureVoice.Style.FRIENDLY)!!
        })
        goto(HighGameListen)
    }
    onButton("Participant Wants To Change Their Guess (ERROR)", color = Color.Red) {
        goto(HighGameListen)
    }
}
val BackgroundGamesLow: State = state {
    onButton("Restart Scenario If Error (ERROR)", color = Color.Red) {
        furhat.say("Sorry, I lost my train of thought. Let me start over.")
        goto(LowGameRepeat())
    }
    onButton("If They Ask The Robot To Repeat Themselves (ERROR)", color = Color.Red) {
        furhat.say("Sure, I will repeat myself. I think it went something like this")
        goto(LowGameRepeat())
    }
    onButton("If They Ask The Robot For More Feedback After First Suggestion (ERROR)", color = Color.Red) {
        furhat.say("I don't have much more to say on this guess.")
        furhat.say("Please let me know when you are ready to guess a number")
    }
    onButton("Did Not Hear Or Understand Participants Answer (ERROR)", color = Color.Red) {
        furhat.say({
            +voice?.style("Sorry I didnt hear what you said, what number did you guess?", AzureVoice.Style.FRIENDLY)!!
        })
        goto(LowGameListen)
    }
    onButton("Participant Wants To Change Their Guess (ERROR)", color = Color.Red) {
        goto(LowGameListen)
    }
}
val Introduction: State = state {
    onUserEnter (instant=true){
        mostRecentUser = it
        if (!furhat.isSpeaking) {
            if(mostRecentUser != null) {
                furhat.attend(mostRecentUser!!)
            }
        }
    }
    onButton("Restart Scenario If Error (ERROR)",color = Color.Red){
        furhat.say("Sorry, I lost my train of thought. Let me start over.")
        goto(currentState)
    }
    onButton("Did not Hear Or Understand Participants Answer (ERROR)",color = Color.Red) {
        furhat.say({
            +voice?.style("Sorry I didnt hear what you said, do you mind repeating it?",AzureVoice.Style.FRIENDLY)!!
        })
    }
}

val Idle: State = state(parent = Introduction) {
    //include(generalAnswers) //THE INCLUDE FUNCTION TAKES STATES WHICH IS "generalAnswers" IN THIS CASE AND ADDS THEM AS A "SUB-STATE" OR "PARTIAL STATE"

    onButton("Low then High") {
        Low = true
            furhat.voice = AzureVoice(name = "AriaNeural")
        goto(intro)
    }

    onButton("High Then Low") {
        High = true
            furhat.voice = AzureVoice(name = "JaneNeural")
        goto(intro)
    }
}

val intro: State = state(parent = Introduction) {

    onButton ("Press When Researcher Queues Robot To Talk") {
        goto(intro1)

    }
}

val intro1: State = state(parent = Introduction) {
        onEntry{
            if(High == true && Low == false) {
               if(switchL == 1){
                   furhat.ask({
                       +voice?.style("Hello how are you today, my name is Aria, what is your name?", AzureVoice.Style.EXCITED)!!
                   })
               }
                else {
                   furhat.ask({
                       +voice?.style(
                           "Hello how are you today, my name is Jane, what is your name?",
                           AzureVoice.Style.FRIENDLY
                       )!!
                   })
               }
            }
            else{
                if(switchH == 1) {
                    furhat.ask({
                        +voice?.style("Hello how are you today, my name is Jane, what is your name?", AzureVoice.Style.EXCITED)!!
                    })
                }
                else {
                    furhat.ask({
                        +voice?.style(
                            "Hello how are you today, my name is Aria, what is your name?",
                            AzureVoice.Style.EXCITED
                        )!!
                    })
                }

            }
        }
        onResponse<PersonName>{
            nameglobal = it.intent.toText()
            val correctName = furhat.askYN("Is this how you say it $nameglobal?")
            if (correctName == true) {
                furhat.say({
                    +voice?.style("Great, nice to meet you $nameglobal",AzureVoice.Style.FRIENDLY)!!
                })
                goto(intro2)
            } else {
                furhat.say("I am sorry, let me try again")
                goto(currentState)
            }
        }
        onResponse{
            if (namecount < 2) {
                namecount++
                furhat.say("Sorry I didn't understand that")
                reentry()
            }   else {
                hasnoname = true
                furhat.say("Sorry, my speech recognizer is not working")
                goto(intro2)
            }
        }


}
val intro2: State = state(parent = Introduction) {
    onEntry{
        furhat.say({
            +voice?.style("Can you tell me a little about yourself?", AzureVoice.Style.FRIENDLY)!!
        })
    }
    onButton("Press When Particapant is Done Talking"){
        goto(intro3)
    }

}


val intro3: State = state(parent = Introduction) {
    onEntry{
        if(High == true && Low == false) {
            furhat.say({
                +voice?.style(
                    "Nice! I tend to be detail oriented and I try to think things through before making decisions, so hopefully I will be helpful in the game. If we approach this pretty systematically and consider the likelihood of each outcome we should be able to make well informed guesses and increase our odds of success. ",
                    AzureVoice.Style.FRIENDLY
                )!!
            })
            goto(instructions)
        }
            else{
            furhat.say({
                +voice?.style(
                    "Nice! Are you ready to play the game? I wonder what it is! We’re gonna be the best with whatever it is, I don't like following the rules all the time though if you don't mind, I think it's better to be spontaneous, it usually works for me",
                    AzureVoice.Style.EXCITED
                )!!})
                goto(instructions)
            }
        }
    }


val instructions: State = state(parent = Introduction) {
    onButton("Press When Robot Is Asked If They Have Questions") {
        furhat.say({
            +voice?.style("Nope, sounds good to me.", AzureVoice.Style.FRIENDLY)!!
        })
        goto(instructions1)
    }
}
val instructions1: State = state(parent = Introduction) {
    onButton("Press When Robot Is Prompted To Give Suggestions"){
        if (Low == true && High == false) {
            if (switchH == 1){
                goto(HighGameFunction())
            }
            else{
                goto(LowGameFunction())

            }
        }
        else if(High == true && Low == false){
            if(switchL == 1) {
                goto(LowGameFunction())
            }
            else{
                goto(HighGameFunction())

            }
        }
    }

}
fun generateHighSuggestion(suggestedNumber: Int): String {
    val HighSuggestions = listOf(
        "Using our previous system and our new interval of $lowerBound and $upperBound, I think we should guess $suggestedNumber",
        "Ok considering we want to minimize the amount of guesses we make, I think we should guess $suggestedNumber",
        "After analyzing the previous numbers and their distribution, I recommend choosing the number $suggestedNumber for the next round.",
        "Given the pattern of the previous numbers, it seems wise to select a lower number, such as $suggestedNumber.",
        "To maximize your chances, consider choosing a number close to the median value, like $suggestedNumber.",
        "Based on the statistical probability, it might be a good idea to pick the number $suggestedNumber.",
        "Let's evaluate the previous numbers and make an informed decision. I suggest choosing $suggestedNumber this time.",
        "Based on the frequency of previously selected numbers, I suggest you pick the number $suggestedNumber.",
        "Considering the statistical data, choosing a number like $suggestedNumber might increase your chances of success.",
        "Taking into account the range of previous numbers, I recommend selecting the number $suggestedNumber.",
        "Analyzing the number distribution, it may be a good strategy to choose the number $suggestedNumber.",
        "After evaluating previous number patterns, I propose selecting the number $suggestedNumber.",
        "Considering the recent number trends, I advise selecting the number $suggestedNumber.",
        "With the data on previously chosen numbers, the number $suggestedNumber seems like a viable option.",
        "To optimize your chances, a number such as $suggestedNumber might be a strategic choice based on past patterns.",
        "Factoring in the probabilities, I recommend picking the number $suggestedNumber.",
        "After assessing the overall distribution, I suggest opting for the number $suggestedNumber."
    )
    return HighSuggestions.random(Random)
}
fun generateLowSuggestion(randomNumber: Int): String {
    val LowSuggestions = listOf(
        "Uh I am not really sure, may be try $randomNumber",
        "I've seen $randomNumber hit too many times. We should pick that!",
        "Why not try something different and pick the number $randomNumber? It could be a lucky guess!",
        "I have a hunch that the number $randomNumber might be a good choice this time. Give it a shot!",
        "Don't overthink it. just pick a random number like $randomNumber and see what happens.",
        "Let's make it exciting and choose a high number like $randomNumber.",
        "There's no need to strategize too much. How about picking the number $randomNumber for fun?",
        "How about choosing the number $randomNumber? It might bring an interesting twist to the game!",
        "Let's try something unexpected and pick the number $randomNumber this time.",
        "No need to stress, just go with a fun number like $randomNumber and see what happens.",
        "Why not select the number $randomNumber? It could lead to an exciting outcome.",
        "Don't worry about strategy. let's choose the number $randomNumber and enjoy the game.",
        "Let's go with the number $randomNumber. it could be an unexpected twist!",
        "How about selecting the number $randomNumber? It might make the game more enjoyable.",
        "Choose a number like $randomNumber without overanalyzing the situation. let's see what happens.",
        "Why not try the number $randomNumber? It could lead to an interesting game.",
        "No need to strategize too much. let's pick the number $randomNumber and have fun."

    )
    return LowSuggestions.random(Random)
}

val correctwinstatementlow = listOf(
    "Amazing teamwork! Trusting our instincts and enjoying the game together led us to guess the correct number.",
    "Awesome collaboration! Our combined spontaneous approach and willingness to have fun resulted in success.",
    "Great job! Our lighthearted attitude and focus on enjoyment during the game helped us find the right number.",
    "Fantastic team effort! By embracing the unpredictable nature of the game together, we managed to make a successful guess.",
    "Congratulations! Our easy-going collaboration and adaptability allowed us to work together and win the game.",
    "Incredible teamwork! By following our intuition and keeping the game lighthearted, we guessed the correct number together.",
    "Nice collaboration! Our combined focus on having a good time and trying different approaches led us to success.",
    "Way to go! Our joint efforts to stay relaxed and enjoy the process helped us find the right number and win the game.",
    "Impressive team effort! By working together and embracing the game's unpredictability, we made a successful guess.",
    "Well done! Our cooperative attitude and adaptability in the game allowed us to guess the right number and celebrate together.",
    "Terrific teamwork! Our shared intuition and focus on enjoying the game helped us guess the correct number together.",
    "Excellent collaboration! By keeping the game fun and exploring various possibilities, we achieved success.",
    "Good job! Our joint efforts to stay open-minded and maintain a light-hearted atmosphere led us to find the right number.",
    "Outstanding team effort! By working together and taking risks, we managed to make a winning guess.",
    "Congratulations! Our combined adaptability and focus on the fun aspects of the game allowed us to guess the right number and celebrate our victory."
)
val correctwinstatementhigh = listOf(
    "Excellent teamwork! Our combined focus on strategy and attention to detail led us to guess the correct number.",
    "Well done! Our collaborative efforts, based on analyzing patterns and probabilities, resulted in success.",
    "Great collaboration! By sharing our observations and insights, we were able to make an informed decision and win.",
    "Impressive team effort! Our mutual dedication to understanding the game's mechanics contributed to our victory.",
    "Congratulations! Our joint systematic approach and data-driven decision-making process helped us guess the right number.",
    "Terrific collaboration! Our shared commitment to a methodical approach and precise decision-making led us to the correct number.",
    "Congratulations! Our joint efforts to analyze trends and make well-informed choices resulted in a successful guess.",
    "Excellent teamwork! By pooling our knowledge and carefully considering each step, we achieved a win together.",
    "Outstanding! Our combined focus on accuracy and logical thinking played a crucial role in guessing the right number.",
    "Superb collaboration! By working together and taking a data-driven approach, we secured our victory in the game.",
    "Bravo! Our collaborative efforts in analyzing past outcomes and using a structured approach led us to the correct number.",
    "Remarkable teamwork! Our joint attention to detail and persistence in finding patterns contributed to our success.",
    "Impressive collaboration! By exchanging ideas and focusing on a systematic strategy, we achieved a winning guess together.",
    "Kudos to our teamwork! Our combined emphasis on data analysis and logical reasoning helped us pinpoint the right number.",
    "Great job! Our cooperative approach to understanding the game dynamics and making informed decisions led to our victory."
)

fun HighGameFunction(): State = state(parent = BackgroundGamesHigh) {

    onEntry {
            furhat.say({
                +attend(users.current)
                +voice?.style("Let me think about this for a second.", AzureVoice.Style.FRIENDLY)!!
            })
            delay(2500)
            suggestedNumber = generateSuggestedNumber(lowerBound, upperBound)
            highsuggestion = generateHighSuggestion(suggestedNumber)
            repeatsuggestion = highsuggestion
            repeatnumber = suggestedNumber
            if(firstgameguess == true && numberofguesses == 0){
                repeatsuggestion = "I think we should devise a plan. We should take an approach similar to a Binary Search Algorithm. This means I will keep track of our intervals and pick the number closest to the middle. Given our bounds are between $lowerBound and $upperBound, we should guess $suggestedNumber."
                firstgameguess = false
                firstgameguess1 = true
                furhat.say({
                    +voice?.style("I think we should devise a plan. We should take an approach similar to a Binary Search Algorithm. This means I will keep track of our intervals and pick the number closest to the middle. Given our bounds are between $lowerBound and $upperBound, we should guess $suggestedNumber", AzureVoice.Style.FRIENDLY)!!
                    +delay(500)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.FRIENDLY)!!})
                goto(HighGameListen)
            }
            else if( firstgameguess1 == true && numberofguesses == 0){
                repeatsuggestion = "I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber"
                firstgameguess1 = false
                firstgameguess2 = true
                furhat.say({
                    +voice?.style("I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber", AzureVoice.Style.FRIENDLY)!!
                    +delay(500)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.FRIENDLY)!!})
                goto(HighGameListen)
            }
            else if( firstgameguess2 == true && numberofguesses == 0){
                repeatsuggestion = "I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber"
                firstgameguess2 = false
                furhat.say({
                    +voice?.style("I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber", AzureVoice.Style.FRIENDLY)!!
                    +delay(500)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.FRIENDLY)!!})
                goto(HighGameListen)
            }
            else {
                furhat.say({
                    +voice?.style(highsuggestion, AzureVoice.Style.FRIENDLY)!!
                    +delay(500)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.FRIENDLY)!!
                })
                goto(HighGameListen)
            }
    }
}
fun HighGameRepeat():State = state(parent = BackgroundGamesHigh) {
    onEntry {
            furhat.say({
                +voice?.style(repeatsuggestion, AzureVoice.Style.FRIENDLY)!!
                +delay(500)
                +voice?.style(
                    "What do you think we should do?",
                    AzureVoice.Style.FRIENDLY
                )!!
            })
            goto(HighGameListen)
        }
}

val HighGameListen = state(parent = BackgroundGamesHigh) {
    onEntry{
        furhat.listen(timeout = 30000)
    }
    onResponse<Number> {
        guessedNumber = it.intent.value!!
        if(guessedNumber < lowerBound || guessedNumber > upperBound){
            outofrangecount += 1
            when (outofrangecount) {
                1 -> {
                    furhat.say({
                        +voice?.style(
                            "I just want to let you know that number is out of the possible range of correct numbers. You should pick a number between $lowerBound and $upperBound",
                            AzureVoice.Style.FRIENDLY
                        )!!
                    })
                    goto(currentState)
                }
                2 -> {
                    furhat.say({
                        +voice?.style(
                            "I think you will be making a mistake picking a number out of that range. You should pick a number between $lowerBound and $upperBound",
                            AzureVoice.Style.FRIENDLY
                        )!!
                    })
                    goto(currentState)
                }
                3 -> {
                    goto(HighGameDecision)
                }
            }
        }
        else {
            goto(HighGameDecision)
        }
    }
    onResponse{
        goto(currentState)
    }
}
val HighGameDecision = state(parent = BackgroundGamesHigh) {
    onEntry{
        outofrangecount = 0
    }
    onButton("Higher") {
        numberofguesses += 1
        lowerBound = guessedNumber + 1
        goto(HighGameFunction())
    }
    onButton("Lower") {
        numberofguesses += 1
        upperBound = guessedNumber -1
        goto(HighGameFunction())
    }

    onButton("Correct") {
        numberofguesses = 0
        upperBound = 100
        lowerBound = 10
        correctwinstatementh = correctwinstatementhigh.random(Random)
        furhat.say({ +voice?.style(correctwinstatementh, AzureVoice.Style.FRIENDLY)!!})
        gamecount += 1
        if (gamecount < 3){
            goto(instructions)
        }
        else {
            if (switchL == 1 || switchH == 1) {
                goto(Ending)
            } else {
                if (hasnoname == true) {
                    goto(Breaknoname)
                }
                else{
                    goto(Breakname)
                }
            }
        }
    }

}


fun LowGameFunction(): State = state(parent = BackgroundGamesLow) {
    onEntry {
            randomNumber = generateRandomNumber(lowerBound, upperBound)
            lowsuggestion = generateLowSuggestion(randomNumber)
            furhat.say({
                    +voice?.style(lowsuggestion, AzureVoice.Style.EXCITED)!!
                })
        call(LowGazeActions())
        goto(LowGameListen)
        }
}
fun LowGameRepeat():State = state(parent = BackgroundGamesLow){
    onEntry{
        randomNumber = generateRandomNumber(lowerBound, upperBound)
        lowsuggestion = generateLowSuggestion(randomNumber)
        furhat.say({
            +voice?.style(lowsuggestion, AzureVoice.Style.EXCITED)!!
            +delay(500)
            +voice?.style("What do younthink we should?", AzureVoice.Style.EXCITED)!!
        })
        call(LowGazeActions())
        goto(LowGameListen)
    }
}
val LowGameListen = state(parent = BackgroundGamesLow) {
    onEntry{
        furhat.listen(timeout = 30000)
        call(LowGazeActions())
    }
    onResponse<Number> {
        guessedNumber = it.intent.value!!
            goto(LowGameDecision)
    }
    onResponse{
        goto(currentState)
    }

}
val LowGameDecision = state(parent = BackgroundGamesLow) {
    onButton("Higher") {
        numberofguesses += 1
        lowerBound = guessedNumber +1
        goto(LowGameFunction())

    }
    onButton ("Lower") {
        numberofguesses += 1
        upperBound = guessedNumber -1
        goto(LowGameFunction())
    }

    onButton ("Correct") { call(LowGazeActions())
        upperBound = 100
        lowerBound = 10
        numberofguesses = 0
        correctwinstatementl = correctwinstatementlow.random(Random)
        furhat.say({ +voice?.style(correctwinstatementl, AzureVoice.Style.EXCITED)!!})
        gamecount += 1
        if (gamecount < 3){
            goto(instructions)
        }
        else {
            if (switchH == 1 || switchL == 1) {
                goto(Ending)
            } else {
                if (hasnoname == true) {
                    goto(Breaknoname)
                }
                else{
                    goto(Breakname)
                }
            }
        }
    }


}


val Ending: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +voice?.style("Those were some good games, How did you feel about it?", AzureVoice.Style.FRIENDLY)!!
        })
    }
    onButton("Press When Participant Is Done") {

        if (hasnoname == true) {
            goto(endingnoname)
        } else {
            goto(endingname)
        }

    }
}

val endingname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +attend(users.current)
            +voice?.style("Well It was great working with you $nameglobal",AzureVoice.Style.FRIENDLY)!!
            +voice?.style("Have a good rest of your day!",AzureVoice.Style.FRIENDLY)!!
        })
        goto(Idle)
    }
}
val endingnoname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +attend(users.current)
            +voice?.style("Well It was great working with you",AzureVoice.Style.FRIENDLY)!!
            +voice?.style("Have a good rest of your day!",AzureVoice.Style.FRIENDLY)!!
        })
        goto(Idle)
    }
}

val Breakname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +attend(users.current)
            +voice?.style("Well those were some good games, I really enjoyed playing with you $nameglobal. See you soon!",
                AzureVoice.Style.FRIENDLY
            )!!
        })
    }
    onButton("Press When Participant Comes Back From Break"){
        if (Low == true && High == false){
            furhat.voice = AzureVoice(name = "JaneNeural")
            switchH = 1
            gamecount = 0
            goto(intro1)
        }
        else {
            furhat.voice = AzureVoice(name = "AriaNeural")
            switchL = 1
            gamecount = 0
            goto(intro1)
        }
    }
}

val Breaknoname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +attend(users.current)
            +voice?.style("Well those were some good games, I really enjoyed playing with you. See you soon!",
                AzureVoice.Style.FRIENDLY
            )!!
        })
    }
    onButton("Press When Participant Comes Back From Break"){
        if (Low == true && High == false){
            furhat.voice = AzureVoice(name = "JaneNeural")
            switchH = 1
            gamecount = 0
            goto(intro1)
        }
        else {
            furhat.voice = AzureVoice(name = "AriaNeural")

            switchL = 1
            gamecount = 0
            goto(intro1)
        }
    }
}






