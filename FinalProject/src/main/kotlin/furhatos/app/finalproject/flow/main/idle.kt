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




var guessedNumber = 0
var Low: Boolean = false
var High: Boolean = false
var nameglobal = ""
var namecount = 1
var hasnoname: Boolean = false
val location = furhatos.records.Location(0.0, -2.0, 2.0)
val location1 = furhatos.records.Location(1.0, 2.0, 2.0)
var minRange = 1
var maxRange = 100
var lowerBound = minRange
var upperBound = maxRange
var suggestedNumber = (minRange + maxRange) / 2
var highsuggestion = ""
var lowsuggestion = ""
var firstguesshigh: Boolean = true
var firstguesslow: Boolean = true
var randomNumber = Random.nextInt(lowerBound, upperBound+1)
var gamecount = 0
var numberofguesses =0
var switchL = 0
var switchH = 0

val HighConscientiousness: State = state {

    onUserEnter(instant=true) {
        if (!furhat.isSpeaking) {
            furhat.attend(it)
        }
    }
    //}
    onButton("Restart Scenario If Error (ERROR)",color = Color.Red){
        furhat.say("Sorry, I lost my train of thought. Let me start over.")
        goto(currentState)
    }
    onButton("If Participant Does Not Choose One of The Two Options (ERROR)",color = Color.Red){
        furhat.say("Please choose one of the two available answers")
        goto(currentState)
    }
    onButton("Did not Hear Or Understand Participants Answer (ERROR)",color = Color.Red) {
        furhat.say({
            +voice?.style("Sorry I didnt hear what you said, do you mind repeating it?",AzureVoice.Style.FRIENDLY)!!
        })
    }
}


val LowConscientiousness: State = state {

    onUserEnter(instant=true) {
        if (!furhat.isSpeaking) {
            while(true) {
                val rand = Random.nextInt(3)
                when (rand) {
                    0 -> furhat.attend(it)
                    2 -> furhat.attend(location = Location.DOWN_RIGHT) // Gaze towards the ceiling
                    3 -> furhat.attend(location = Location.UP_LEFT) // Gaze towards the floor
                }
                delay(Random.nextLong(1000, 4000))
            }
        }
    }
    //}

    onButton("Restart Scenario If Error (ERROR)",color = Color.Red){
        furhat.say("Sorry, I lost my train of thought. Let me start over.")
        goto(currentState)
    }
    onButton("If Participant Does Not Choose One of The Two Options (ERROR)",color = Color.Red){
        furhat.say("Please choose one of the two available answers")
        goto(currentState)
    }
    onButton("Did not Hear Or Understand Participants Answer (ERROR)",color = Color.Red) {
        furhat.say({
            +voice?.style("Sorry I didnt hear what you said, do you mind repeating it?",AzureVoice.Style.FRIENDLY)!!
        })
    }
}

val Introduction: State = state {

    onUserEnter(instant=true) {
        if (!furhat.isSpeaking) {
            furhat.attend(it)
        }
    }


    //}
    onButton("Restart Scenario If Error (ERROR)",color = Color.Red){
        furhat.say("Sorry, I lost my train of thought. Let me start over.")
        goto(currentState)
    }
    onButton("If Participant Does Not Choose One of The Two Options (ERROR)",color = Color.Red){
        furhat.say("Please choose one of the two available answers")
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
    init{
        furhat.voice = AzureVoice(name = "AriaNeural")
    }

    onButton("Low then High") {
        Low = true
        goto(intro)
    }

    onButton("High Then Low") {
        High = true
        goto(intro)
    }
}

val intro: State = state(parent = Introduction) {

    onButton ("Press When Researcher Queues Robot To Talk"){
        furhat.say({
            +voice?.style("Hello how are you today, my name is Furhat, what is your name?", AzureVoice.Style.FRIENDLY)!!
        })
        onResponse<PersonName>{
            nameglobal = it.intent.toText()
            var correctName = furhat.askYN("Is this how you say it $nameglobal?")
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

}

val intro2: State = state(parent = Introduction) {
    onEntry{
        furhat.say({
            +voice?.style("Well it's nice to meet you, can you tell me a little about yourself?", AzureVoice.Style.FRIENDLY)!!
        })
    }
    onButton("Press When Particapant is Done Talking"){
        goto(intro3)
    }

}

val intro3: State = state(parent = Introduction) {
    onEntry{
        furhat.say({
            +voice?.style("Well it's nice to meet you, can you tell me a little about yourself?", AzureVoice.Style.FRIENDLY)!!
        })
    }
    onButton("Press When Particapant is Done Talking"){
        goto(intro4)
    }

}

val intro4: State = state(parent = Introduction) {
    onEntry{
        furhat.say({
            +voice?.style("That's awesome, thanks for sharing! I am a social robot that likes to talk to interesting people such as yourself.", AzureVoice.Style.FRIENDLY)!!
        })
        goto(instructions)
    }

}

val instructions: State = state(parent = Introduction) {
    onButton("Press When Robot Is Asked If They Have Questions"){
        furhat.say({
            +voice?.style("Nope, sounds good to me.", AzureVoice.Style.FRIENDLY)!!
        })
        if (Low == true && High == false) {
            if (switchH == 1){
                High = true
                goto(GiveHighSuggestion)}
            else{
                goto(GiveLowSuggestion)
            }
        }
        else if(High == true && Low == false){
            if(switchL == 1) {
                Low = true
                goto(GiveLowSuggestion)
            }
            else{
                goto(GiveHighSuggestion)
            }
        }
    }
}

val HighSuggestions = listOf(
    "Using our previous system and our new interval of $lowerBound and $upperBound, I think we should guess $suggestedNumber",
    "Ah",
    "Good Choice",
    "Alright",
    "Nice",
    "Mmmm",
    "Cool",
    "Sweet",
    "Good"
)

val LowSuggestions = listOf(
    "Uh I am not really sure, may be try $randomNumber",
    "I'ven seen $randomNumber hit too many times. We should pick that!",
    "Good Choice",
    "Alright",
    "Nice",
    "Mmmm",
    "Cool",
    "Sweet",
    "Good"
)

public val GiveHighSuggestion = state(parent = HighConscientiousness) {
    onEntry {
        if (firstguesshigh == true){
            firstguesshigh = false
            furhat.ask("I think we should take a approach similar to a Binary Search Algorithim. This means that we guess a number in the middle of each interval. So to start, I suggest we pick $suggestedNumber",30000)
        }
        else{
        highsuggestion = HighSuggestions.random(Random)
        furhat.ask("$highsuggestion",30000)
        }
    }
    onResponse<Number> {
        numberofguesses += 1
        guessedNumber = it.intent.value!!
        call(HighGame())
    }

}

/*val HighGame = state(parent = HighConscientiousness) {

    onButton("Higher") {
        lowerBound = guessedNumber + 1
        suggestedNumber = (lowerBound + upperBound) / 2
        goto(GiveHighSuggestion)
    }

    onButton("Lower") {
        upperBound = guessedNumber - 1
        suggestedNumber = (lowerBound + upperBound) / 2
        goto(GiveHighSuggestion)
    }

    onButton("Correct") {
        furhat.say("Great! I'm glad I could help you guess the number.")
        if(Low == true && High == true){
            goto(Idle)
        }
        else{
            goto(Break)
        }
    }
}

*/


fun HighGame(): State = state(parent = HighConscientiousness) {
    onButton("Higher") {
        lowerBound = guessedNumber + 1
        suggestedNumber = (lowerBound + upperBound) / 2
        goto(GiveHighSuggestion)
    }

    onButton("Lower") {
        upperBound = guessedNumber - 1
        suggestedNumber = (lowerBound + upperBound) / 2
        goto(GiveHighSuggestion)
    }

    onButton("Correct") {
        gamecount += 1
        if (gamecount < 3){
            goto(instructions)
        }
        else {
            if (Low == true && High == true) {
                goto(Ending)
            } else {
                goto(Break)
            }
        }
    }
}

public val GiveLowSuggestion = state(parent = LowConscientiousness) {
    onEntry {
        if (firstguesslow == true){
            firstguesslow = false
            furhat.ask("Uh I am not really sure, may be try $randomNumber",30000)
        }
        else{
        highsuggestion = LowSuggestions.random(Random)
        furhat.ask("$lowsuggestion",30000)
        }
    }
    onResponse<Number> {
        numberofguesses += 1
        guessedNumber = it.intent.value!!
        call(LowGame())
    }

}

fun LowGame(): State = state(parent = LowConscientiousness) {

    onButton("Higher") {
        lowerBound = guessedNumber + 1
        goto(GiveLowSuggestion)
    }

    onButton("Lower") {
        upperBound = guessedNumber - 1
        goto(GiveLowSuggestion)
    }

    onButton("Correct") {
        gamecount += 1
        furhat.say({
            +voice?.style("Nice! I'm glad I could help you guess the number.", AzureVoice.Style.FRIENDLY)!!
        })
        if (gamecount < 3){
            goto(instructions)
        }
        else {
            if (Low == true && High == true) {
                goto(Ending)
            } else {
                goto(Break)
            }
        }
    }
}

val Ending: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +voice?.style("Those were some good games,How did you feel?", AzureVoice.Style.FRIENDLY)!!
        })
        onButton("Press When Participant Is Done") {

            if (hasnoname == true) {
                goto(endingname)
            } else {
                goto(endingnoname)
            }

        }
    }
}

val endingname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +voice?.style("Well It was great working with you $nameglobal",AzureVoice.Style.FRIENDLY)!!
            +voice?.style("Have a good rest of your day!",AzureVoice.Style.FRIENDLY)!!
        })
        goto(Idle)
    }
}
val endingnoname: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +voice?.style("Well It was great working with you",AzureVoice.Style.FRIENDLY)!!
            +voice?.style("Have a good rest of your day!",AzureVoice.Style.FRIENDLY)!!
        })
        goto(Idle)
    }
}

val Break: State = state(parent = Introduction) {
    onEntry {
        furhat.say({
            +voice?.style(
                "That's awesome, thanks for sharing! I am a social robot that likes to talk to interesting people such as yourself.",
                AzureVoice.Style.FRIENDLY
            )!!
        })
    }
        onButton("Press When Particapant Comes Back From Break"){
            if (Low == true && High == false){
                switchH = 1
            }
            else {
                switchL = 1
            }
            goto(instructions)
        }
}

/*val LowGame: State = state(parent = LowConscientiousness) {
    onEntry {
        val targetNumber = Random.nextInt(1, 101)
        call(GameRound(targetNumber, 1, 100))
    }

    onButton("Correct"){
        if(Low == true && High == true){
        goto(Idle)
        }
        else{
            goto(Break)
        }
    }

    onButton("Not Correct"){
        goto(currentState)
    }
}

fun GameRound(targetNumber: Int, min: Int, max: Int): State = state {
    onEntry {
        val suggestion = (min + max) / 2
        furhat.say("Here's a suggestion: try guessing $suggestion.")
        furhat.listen()
    }

    onResponse<Number>{
        guessedNumber = it.intent.value!!
        when {
            guessedNumber == targetNumber -> {
                terminate()
            }
            guessedNumber < targetNumber -> {
                if (guessedNumber >= min) {
                    call(GameRound(targetNumber, guessedNumber, max))
                } else {
                    furhat.say("Your guess is out of the current range. Please guess a number between $min and $max.")
                    reentry()
                }
            }
            guessedNumber > targetNumber -> {
                if (guessedNumber <= max) {
                    call(GameRound(targetNumber, min, guessedNumber))
                } else {
                    furhat.say("Your guess is out of the current range. Please guess a number between $min and $max.")
                    reentry()
                }
            }
        }
    }
}
*/


