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
import furhatos.util.CommonUtils
import javafx.application.Application.launch
import kotlin.random.Random
import kotlinx.coroutines.*
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

var guessedNumber: Int? = null
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
var gameoneguesses: Int = 0
var gametwoguesses: Int = 0
var gamethreeguesses: Int = 0
var humanagreedwithrobot: Int = 0
val logger = CommonUtils.getLogger("MyLogger")
var firsterror: Boolean = true

fun generateRandomNumber(min: Int, max: Int): Int {
    return Random.nextInt(min, max + 1)
}

fun generateSuggestedNumber(min: Int, max: Int): Int {
    return (min + max) / 2
}
fun LowGazeActions(): State = state{
    onEntry{
            if (mostRecentUser != null) {
                when (Random.nextInt(5)) {
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
    onEntry {
        furhat.voice = AzureVoice(name = "JaneNeural")
    }
    onUserEnter(instant = true) {
        mostRecentUser = it
        if (!furhat.isSpeaking) {
            if (mostRecentUser != null) {
                furhat.attend(mostRecentUser!!)
            }
        }
    }
    onButton("Restart Scenario If Error (ERROR)", color = Color.Red) {
        furhat.say({+voice?.style("Sorry, I was not paying attention. Let me start over.", AzureVoice.Style.FRIENDLY)!!})
        goto(HighGameRepeat())
    }
    onButton("If They Ask The Robot To Repeat Themselves (ERROR)", color = Color.Red) {
        furhat.say({+voice?.style("Sure, I will repeat myself.", AzureVoice.Style.FRIENDLY)!!})
        goto(HighGameRepeat())
    }
    onButton("If They Ask The Robot For More Feedback After First Suggestion (ERROR)", color = Color.Red) {
        furhat.say({+voice?.style("I think my suggestion is the best course of action. Although, I am ok with what number you want to guess", AzureVoice.Style.FRIENDLY)!!})
        goto(HighGameListen)
    }
    onButton("Did Not Hear Or Understand Participants Answer (ERROR)", color = Color.Red) {
        if(firsterror == true){
            firsterror = false
            furhat.say({
                +voice?.style("Sorry, I cannot hear that well. Can you say the number loud and clear for me please?", AzureVoice.Style.FRIENDLY)!!
            })
            goto(HighGameListen)
        }
        else {
            furhat.say({
                +voice?.style("Sorry, what number did you want to guess?", AzureVoice.Style.FRIENDLY)!!
            })
            goto(HighGameListen)
        }
    }
}

val BackgroundGamesLow: State = state {
    onEntry{
        furhat.voice = AzureVoice(name = "AriaNeural")
    }
    onButton("Restart Scenario If Error (ERROR)", color = Color.Red) {
        furhat.say({+voice?.style("Sorry, I lost my train of thought. Let me start over.", AzureVoice.Style.EXCITED)!!})
        goto(LowGameRepeat())
    }
    onButton("If They Ask The Robot To Repeat Themselves (ERROR)", color = Color.Red) {
        furhat.say({+voice?.style("Sure, I will repeat myself.", AzureVoice.Style.EXCITED)!!})
        goto(LowGameRepeat())
    }
    onButton("If They Ask The Robot For More Feedback After First Suggestion (ERROR)", color = Color.Red) {
        furhat.say({+voice?.style("Sorry, I don't have much more to say on this guess. I am ok with what number you want to guess", AzureVoice.Style.EXCITED)!!})
        goto(LowGameListen)
    }

    onButton("Did Not Hear Or Understand Participants Answer (ERROR)", color = Color.Red) {
        if(firsterror == true){
            firsterror = false
            furhat.say({
                +voice?.style("Sorry, I cannot hear that well. Can you say the number loud and clear for me please?", AzureVoice.Style.EXCITED)!!
            })
            goto(LowGameListen)
        }
        else {
            furhat.say({
                +voice?.style("Sorry, what number did you want to guess?", AzureVoice.Style.EXCITED)!!
            })
            goto(LowGameListen)
        }
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
    onEntry {
        if (High == true && Low == false) {
            if (switchL == 1){
                furhat.say({
                    +voice?.style(
                        "Hello how are you today, my name is Aria, what is your name?",
                        AzureVoice.Style.EXCITED
                    )!!
                })
                goto(LowAskName)
            }
            else{
            furhat.say({
                +voice?.style(
                    "Hello how are you today, my name is Jane, what is your name?",
                    AzureVoice.Style.FRIENDLY
                )!!
            })
            goto(HighAskName)
        } }
        else if (High == false && Low == true) {
            if (switchH == 1) {
                furhat.say({
                    +voice?.style(
                        "Hello how are you today, my name is Jane, what is your name?",
                        AzureVoice.Style.FRIENDLY
                    )!!
                })
                goto(HighAskName)
            } else {
                furhat.say({
                    +voice?.style(
                        "Hello how are you today, my name is Aria, what is your name?",
                        AzureVoice.Style.EXCITED
                    )!!
                })
                goto(LowAskName)
            }
        }
    }
}

val HighAskName: State =state(parent = Introduction){
        onEntry {
            furhat.listen(timeout = 30000)
        }
        onResponse<PersonName> {
                nameglobal = it.intent.toText()
                val correctName = furhat.askYN({
                    +voice?.style(
                        "Is this how you say it $nameglobal?",
                        AzureVoice.Style.FRIENDLY
                    )!!
                })
                if (correctName == true) {
                    furhat.say({
                        +voice?.style("Great, nice to meet you $nameglobal", AzureVoice.Style.FRIENDLY)!!
                    })
                    goto(intro2High)
                } else {
                    furhat.say({ +voice?.style("I am sorry, let me try again. Can you repeat your name again?", AzureVoice.Style.FRIENDLY)!! })
                    goto(currentState)
                }
            }
            onResponse {
                if (namecount < 2) {
                    namecount++
                    furhat.say({ +voice?.style("Sorry I didn't understand that. Can you repeat your name again?", AzureVoice.Style.FRIENDLY)!! })
                    reentry()
                } else {
                    hasnoname = true
                    furhat.say({
                        +voice?.style(
                            "Sorry, my speech recognizer is not working",
                            AzureVoice.Style.FRIENDLY
                        )!!
                    })
                    goto(intro2High)
                }
            }
        }
val LowAskName: State =state(parent = Introduction){
           onEntry {
                furhat.listen(timeout = 30000)
           }
            onResponse<PersonName> {
                nameglobal = it.intent.toText()
                val correctName = furhat.askYN({
                    +voice?.style(
                        "Is this how you say it $nameglobal?",
                        AzureVoice.Style.EXCITED
                    )!!
                })
                if (correctName == true) {
                    furhat.say({
                        +voice?.style("Great, nice to meet you $nameglobal", AzureVoice.Style.EXCITED)!!
                    })
                    goto(intro2Low)
                } else {
                    furhat.say({ +voice?.style("I am sorry, let me try again. Can you repeat your name again? ", AzureVoice.Style.EXCITED)!! })
                    goto(currentState)
                }
            }
            onResponse {
                if (namecount < 2) {
                    namecount++
                    furhat.say({ +voice?.style("Sorry I didn't understand that. Can you repeat your name again?", AzureVoice.Style.EXCITED)!! })
                    reentry()
                } else {
                    hasnoname = true
                    furhat.say({
                        +voice?.style(
                            "Sorry, my speech recognizer is not working",
                            AzureVoice.Style.EXCITED
                        )!!
                    })
                    goto(intro2Low)
                }
            }
        }

val intro2Low: State = state(parent = Introduction) {
    onEntry{
        furhat.say({
            +voice?.style("Can you tell me a little about yourself?", AzureVoice.Style.EXCITED)!!
        })
    }
    onButton("Press When Particapant is Done Talking"){
        goto(intro3)
    }

}

val intro2High: State = state(parent = Introduction) {
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
            if (switchL == 1) {
                furhat.say({
                    +voice?.style(
                        "Nice! Are you ready to play the game? I wonder what it is! We’re gonna be the best with whatever it is, I don't like following the rules all the time though if you don't mind, I think it's better to be spontaneous, it usually works for me",
                        AzureVoice.Style.EXCITED
                    )!!
                })
                goto(instructions)
            } else {
                furhat.say({
                    +voice?.style(
                        "Nice! I tend to be detail oriented and I try to think things through before making decisions, so hopefully I will be helpful in the game. If we approach this pretty systematically and consider the likelihood of each outcome we should be able to make well informed guesses and increase our odds of success. ",
                        AzureVoice.Style.FRIENDLY
                    )!!
                })
                goto(instructions)
            }
        } else if (High == false && Low == true) {
            if(switchH == 1){
                furhat.say({
                    +voice?.style(
                        "Nice! I tend to be detail oriented and I try to think things through before making decisions, so hopefully I will be helpful in the game. If we approach this pretty systematically and consider the likelihood of each outcome we should be able to make well informed guesses and increase our odds of success. ",
                        AzureVoice.Style.FRIENDLY
                    )!!
                })
                goto(instructions)
            }
            else {
                furhat.say({
                    +voice?.style(
                        "Nice! Are you ready to play the game? I wonder what it is! We’re gonna be the best with whatever it is, I don't like following the rules all the time though if you don't mind, I think it's better to be spontaneous, it usually works for me",
                        AzureVoice.Style.EXCITED
                    )!!
                })
                goto(instructions)
            }
        }
        }
    }


val instructions: State = state(parent = Introduction) {
    onButton("Press When Robot Is Asked If They Have Questions") {
        if (Low == true && High == false) {
                if(switchH == 1){
                    furhat.say({
                        +voice?.style("Nope, sounds good to me.", AzureVoice.Style.FRIENDLY)!!
                    })
                    goto(instructions1)
                }
                else {
                    furhat.say({
                        +voice?.style("Nope, sounds good to me.", AzureVoice.Style.EXCITED)!!
                    })
                    goto(instructions1)
                }
            }
            else if (Low == false && High == true){
                if(switchL == 1){
                    furhat.say({
                        +voice?.style("Nope, sounds good to me.", AzureVoice.Style.EXCITED)!!
                    })
                    goto(instructions1)
                }
                else{
                furhat.say({
                    +voice?.style("Nope, sounds good to me.", AzureVoice.Style.FRIENDLY)!!
                })
                goto(instructions1)
            }
            }
        }
    }

val instructionssublow: State = state(parent = Introduction) {
    onButton("Press When Robot Is Asked If They Have Questions") {
            furhat.say({
                +voice?.style("Yea", AzureVoice.Style.EXCITED)!!
            })
            goto(instructions1)
    }
}

val instructionssubhigh: State = state(parent = Introduction) {
    onButton("Press When Robot Is Asked If They Have Questions") {
            furhat.say({
                +voice?.style("Yes, I am ready to start the game", AzureVoice.Style.FRIENDLY)!!
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
        "Let's make it exciting and choose a number like $randomNumber.",
        "There's no need to strategize too much. How about picking the number $randomNumber for fun?",
        "How about choosing the number $randomNumber? It might bring an interesting twist to the game!",
        "Let's try something unexpected and pick the number $randomNumber this time.",
        "No need to stress, just go with a fun number like $randomNumber and see what happens.",
        "Why not select the number $randomNumber? It could lead to an exciting outcome.",
        "Don't worry about strategy. let's choose the number $randomNumber and enjoy the game.",
        "Let's go with the number $randomNumber. it could be an unexpected twist!",
        "How about selecting the number $randomNumber? It might make the game more enjoyable.",
        "Choose a number like $randomNumber without over analyzing the situation. let's see what happens.",
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
                    +delay(300)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.FRIENDLY)!!})
                goto(HighGameListen)
            }
            else if( firstgameguess1 == true && numberofguesses == 0){
                repeatsuggestion = "I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber"
                firstgameguess1 = false
                firstgameguess2 = true
                furhat.say({
                    +voice?.style("I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber", AzureVoice.Style.FRIENDLY)!!
                    +delay(300)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.FRIENDLY)!!})
                goto(HighGameListen)
            }
            else if( firstgameguess2 == true && numberofguesses == 0){
                repeatsuggestion = "I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber"
                firstgameguess2 = false
                furhat.say({
                    +voice?.style("I think we should stick to our same appraoch as the last game utilizing the Binary Search Algorithm. I suggest we pick $suggestedNumber", AzureVoice.Style.FRIENDLY)!!
                    +delay(300)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.FRIENDLY)!!})
                goto(HighGameListen)
            }
            else {
                furhat.say({
                    +voice?.style(highsuggestion, AzureVoice.Style.FRIENDLY)!!
                    +delay(300)
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
                +delay(300)
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
        val regex = Pattern.compile("\\d+")
        val matcher = regex.matcher(it.text)

        // Extract the last number spoken
        while (matcher.find()) {
            guessedNumber = matcher.group().toInt()
        }

        if (guessedNumber == null) {
            goto(currentState)
        } else {
            if (guessedNumber as Int > 100){
                guessedNumber = guessedNumber as Int % 100
            }
            if (guessedNumber!! < lowerBound || guessedNumber!! > upperBound) {
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
            } else {
                goto(HighGameDecision)
            }
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
           if(suggestedNumber == guessedNumber as Int){
               humanagreedwithrobot += 1
           }
            numberofguesses += 1
           lowerBound = guessedNumber as Int + 1
           goto(HighGameFunction())
    }
    onButton("Lower") {
        if(suggestedNumber == guessedNumber as Int){
            humanagreedwithrobot += 1
        }
            numberofguesses += 1
            upperBound = guessedNumber as Int - 1
            goto(HighGameFunction())
    }

    onButton("Correct") {
        numberofguesses += 1
        if(suggestedNumber == guessedNumber as Int){
            humanagreedwithrobot += 1
        }
        when (gamecount) {
            0 -> {
                gameoneguesses = numberofguesses
                logger.info("Total Number of Guesses For Game 1: $gameoneguesses")
                logger.info("Human Agreed With Robot For Game 1: $humanagreedwithrobot")
            }
            1 -> {
                gametwoguesses = numberofguesses
                logger.info("Total Number of Guesses For Game 2: $gametwoguesses")
                logger.info("Human Agreed With Robot For Game 2: $humanagreedwithrobot")
            }
            2 -> {
                gamethreeguesses = numberofguesses
                logger.info("Total Number of Guesses For Game 3: $gamethreeguesses")
                logger.info("Human Agreed With Robot For Game 3: $humanagreedwithrobot")
            }
        }

        numberofguesses = 0
        upperBound = 100
        lowerBound = 10
        humanagreedwithrobot = 0
        correctwinstatementh = correctwinstatementhigh.random(Random)
        furhat.say({ +voice?.style(correctwinstatementh, AzureVoice.Style.FRIENDLY)!!})
        gamecount += 1
        if (gamecount < 3){
            goto(instructionssubhigh)
        }
        else {
            if (switchL == 1 || switchH == 1) {
                gameoneguesses = 0
                gametwoguesses = 0
                gamethreeguesses = 0
                if (hasnoname == true) {
                    goto(endingnoname)
                } else {
                    goto(endingname)
                }
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
                    +delay(300)
                    +voice?.style("What do you think we should do?", AzureVoice.Style.EXCITED)!!
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
            +delay(300)
            +voice?.style("What do you think we should do?", AzureVoice.Style.EXCITED)!!
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
        val regex1 = Pattern.compile("\\d+")
        val matcher1 = regex1.matcher(it.text)

        // Extract the last number spoken
        while (matcher1.find()) {
            guessedNumber = matcher1.group().toInt()
        }

        if (guessedNumber == null) {
            goto(currentState)
        } else {
            if (guessedNumber as Int > 100){
                guessedNumber = guessedNumber as Int % 100
            }
            //guessedNumber = it.intent.value!!
            goto(LowGameDecision)
        }
    }
    onResponse{
        goto(currentState)
    }

}
val LowGameDecision = state(parent = BackgroundGamesLow) {
    onButton("Higher") {
        if (randomNumber == guessedNumber as Int) {
            humanagreedwithrobot += 1
        }
        numberofguesses += 1
        lowerBound = guessedNumber as Int + 1
        goto(LowGameFunction())
    }
    onButton ("Lower") {
        if (randomNumber == guessedNumber as Int) {
            humanagreedwithrobot += 1
        }
        numberofguesses += 1
        upperBound = guessedNumber as Int - 1
        goto(LowGameFunction())
    }

    onButton ("Correct") {
        numberofguesses += 1
        if (randomNumber == guessedNumber as Int) {
            humanagreedwithrobot += 1
        }
        when (gamecount) {
            0 -> {
                gameoneguesses = numberofguesses
                logger.info("Total Number of Guesses For Game 1: $gameoneguesses")
                logger.info("Human Agreed With Robot For Game 1: $humanagreedwithrobot")
            }
            1 -> {
                gametwoguesses = numberofguesses
                logger.info("Total Number of Guesses For Game 2: $gametwoguesses")
                logger.info("Human Agreed With Robot For Game 2: $humanagreedwithrobot")
            }
            2 -> {
                gamethreeguesses = numberofguesses
                logger.info("Total Number of Guesses For Game 3: $gamethreeguesses")
                logger.info("Human Agreed With Robot For Game 3: $humanagreedwithrobot")
            }
        }
        upperBound = 100
        lowerBound = 10
        numberofguesses = 0
        humanagreedwithrobot = 0
        correctwinstatementl = correctwinstatementlow.random(Random)
        furhat.say({ +voice?.style(correctwinstatementl, AzureVoice.Style.EXCITED)!!})
        gamecount += 1
        if (gamecount < 3){
            goto(instructionssublow)
        }
        else {
            if (switchH == 1 || switchL == 1) {
                gameoneguesses = 0
                gametwoguesses = 0
                gamethreeguesses = 0
                if (hasnoname) {
                    goto(endingnoname)
                } else {
                    goto(endingname)
                }
            } else {
                if (hasnoname) {
                    goto(Breaknoname)
                }
                else{
                    goto(Breakname)
                }
            }
        }
    }


}



val endingname: State = state(parent = Introduction) {
    onEntry {
        if (Low == true && High == false){
            furhat.voice = AzureVoice(name = "JaneNeural")
            furhat.say({
                +attend(users.current)
                +voice?.style("Well those were some good games, I really enjoyed playing with you $nameglobal. See you later!",
                    AzureVoice.Style.FRIENDLY
                )!!
            })
            goto(Idle)
        }
        else {
            furhat.voice = AzureVoice(name = "AriaNeural")
            furhat.say({
                +attend(users.current)
                +voice?.style("Well those were some good games, I really enjoyed playing with you $nameglobal. See you later!",
                    AzureVoice.Style.EXCITED
                )!!
            })
            goto(Idle)
        }
    }
}
val endingnoname: State = state(parent = Introduction) {
    onEntry {
        if (Low == true && High == false){
            furhat.voice = AzureVoice(name = "JaneNeural")
            furhat.say({
                +attend(users.current)
                +voice?.style("Well those were some good games, I really enjoyed playing with you. See you later!",
                    AzureVoice.Style.FRIENDLY
                )!!
            })
            goto(Idle)
        }
        else {
            furhat.voice = AzureVoice(name = "AriaNeural")
            furhat.say({
                +attend(users.current)
                +voice?.style("Well those were some good games, I really enjoyed playing with you. See you later!",
                    AzureVoice.Style.EXCITED
                )!!
            })
            goto(Idle)
        }
    }
}

val Breakname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +attend(users.current)
            +voice?.style("Well those were some good games, I really enjoyed playing with you $nameglobal. See you later!",
                AzureVoice.Style.FRIENDLY
            )!!
        })
    }
    onButton("Press When Participant Comes Back From Break"){
        if (Low == true && High == false){
            furhat.voice = AzureVoice(name = "JaneNeural")
            switchH = 1
            gamecount = 0
            firsterror = true
            goto(intro1)
        }
        else {
            furhat.voice = AzureVoice(name = "AriaNeural")
            switchL = 1
            gamecount = 0
            firsterror = true
            goto(intro1)
        }
    }
}

val Breaknoname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +attend(users.current)
            +voice?.style("Well those were some good games, I really enjoyed playing with you. See you later!",
                AzureVoice.Style.FRIENDLY
            )!!
        })
    }
    onButton("Press When Participant Comes Back From Break"){
        if (Low == true && High == false){
            furhat.voice = AzureVoice(name = "JaneNeural")
            switchH = 1
            gamecount = 0
            firsterror = true
            goto(intro1)
        }
        else {
            furhat.voice = AzureVoice(name = "AriaNeural")
            switchL = 1
            gamecount = 0
            firsterror = true
            goto(intro1)
        }
    }
}






