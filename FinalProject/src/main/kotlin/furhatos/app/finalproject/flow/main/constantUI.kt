package furhatos.app.finalproject.flow.main

import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.voice.AzureVoice
import furhatos.gestures.Gesture
import furhatos.gestures.Gestures
import furhatos.records.Location

//statements ORIGINALLY CONTAINED A LIST A PHRASES THAT WERE AVAILABLE FOR THE ROBOT TO SAY
val statements = listOf(
    "Ok",
    "Ah",
    "Good Choice",
    "Alright",
    "Nice",
    "Mmmm",
    "Cool",
    "Sweet",
    "Good"
)

//LIST OF GESTURES THAT THE ROBOT CAN MAKE. YOU CAN USE AUTO COMPLETE TO FIND THE LIST OF AVAILABLE GESTURES
val gestures = listOf(
    Gestures.Wink,
    Gestures.ExpressSad(duration = 2.5),
    Gestures.Shake(iterations = 2, strength = 0.8),
    Gestures.Nod,
    Gestures.BigSmile(duration = 2.5, strength = 0.8),
    Gestures.Surprise(duration = 1.5),
    Gestures.ExpressDisgust(duration = 2.5)

)


val constantUI = partialState {
    //THIS FUNCTION MAKES A BUTTON FOR EACH GESTURE IN gestures. THIS IS ACCOMPLISHED BY:
    //1. CREATING A LABEL: using the parameter "label" = gesture.name!!, turns the Gesture functions into text that is on the button
    //2. LOCATION OF BUTTON: using the parameter "section", we can say where the button is going to be located using "Section.RIGHT" for example
    //3. COLOR OF BUTTON: using the parameter "color", we choose what color we want the button ot be
    gestures.forEach { gesture ->
        onButton(label = gesture.name!!, section = Section.RIGHT, color = Color.Green) {
//            furhat.say { //THIS WAS A FOR LOOP THAT RAN THROUGH EACH AVAILABLE PHRASE IN statements AND THEN SMILED AFTER EACH ONE WAS SAID BY THE ROBOT
//                +statement
//                Gestures.BigSmile
//            }
            furhat.gesture(gesture)
        }
    }
    statements.forEach { statement ->
        onButton(label = statement, section = Section.RIGHT, color = Color.Blue) {
            //furhat.say { //THIS WAS A FOR LOOP THAT RAN THROUGH EACH AVAILABLE PHRASE IN statements AND THEN SMILED AFTER EACH ONE WAS SAID BY THE ROBOT
            //      +statement
//                Gestures.BigSmile
            //  }
            furhat.say(statement)
        }
    }
    onButton(label = "Express Sad and Look to Side Then Down (NORMAL SAD)", section = Section.LEFT, color = Color.Blue) {
        furhat.gesture(Gestures.ExpressSad(duration = 2.5))
        furhat.attend(location = Location.RIGHT)
        delay(1500)
        furhat.attend(location = Location.DOWN_RIGHT)
        delay(2000)
        furhat.attend(users.current)
    }
    onButton(label = "Say Thats Sad, Shake Head, and Look Down (MEDIUM SAD)", section = Section.LEFT, color = Color.Blue) {
        furhat.say({
            +voice?.style("That's Sad",AzureVoice.Style.UNFRIENDLY)!!})
        furhat.gesture(Gestures.ExpressSad(duration = 2.0))
        furhat.gesture(Gestures.Shake(iterations = 2),async = true)
        delay(1000)
        furhat.attend(location = Location.DOWN_RIGHT)
        delay(2000)
        furhat.attend(users.current)
    }
    onButton(label = "Say I Can't Believe That, Shake Head, and Look Down (MEDIUM SAD)", section = Section.LEFT, color = Color.Blue) {
        furhat.say({
            +voice?.style("I Can't Believe That",AzureVoice.Style.UNFRIENDLY)!!})
        furhat.gesture(Gestures.ExpressSad(duration = 2.0))
        furhat.gesture(Gestures.Shake(iterations = 2),async = true)
        delay(1000)
        furhat.attend(location = Location.DOWN_RIGHT)
        delay(2000)
        furhat.attend(users.current)
    }
    onButton(label = "Shake Head, Express Sad, and Say Oh No (VERY SAD)", section = Section.LEFT, color = Color.Blue) {
        furhat.say ({
            +voice?.style("Oh no",AzureVoice.Style.TERRIFIED)!! })
        furhat.gesture(Gestures.Surprise(duration = 1.5),async=false)
        furhat.gesture(Gestures.ExpressSad(duration = 2.5))
        furhat.gesture(Gestures.Shake(iterations = 2, strength = 0.6),async = false)
    }
    onButton(label = "Nod and Say Ummmmm (NEUTRAL)", section = Section.LEFT, color = Color.Yellow) {
        furhat.say{+"Ummmmmm"}
        furhat.gesture(Gestures.Nod(iterations = 1, strength = 1.0),async = false)
    }
    onButton(label = "Nod and Say Ok (NEUTRAL)", section = Section.LEFT, color = Color.Yellow) {
        furhat.say{ +"Ok"}
        furhat.gesture(Gestures.Nod(iterations = 1, strength = 1.0),async= false)
    }
    onButton(label = "Smile and Nod (NICE)", section = Section.LEFT, color = Color.Green) {
        furhat.gesture(Gestures.BigSmile(duration = 2.5))
        furhat.gesture(Gestures.Nod(iterations = 1, strength = 1.0),async = false)
    }
    onButton(label = "Smile, Nod, and Say Nice (MEDIUM NICE)", section = Section.LEFT, color = Color.Green) {
        furhat.say{
            +voice?.style("Nice",AzureVoice.Style.FRIENDLY)!!}
        furhat.gesture(Gestures.BigSmile)
        furhat.gesture(Gestures.Nod(iterations = 1, strength = 1.0),async = false)
    }
    onButton(label = "Smile, Nod, and Say Ok (MEDIUM NICE)", section = Section.LEFT, color = Color.Green) {
        furhat.say {
            +voice?.style("Ok",AzureVoice.Style.FRIENDLY)!! }
        furhat.gesture(Gestures.BigSmile(duration = 2.5))
        furhat.gesture(Gestures.Nod(iterations = 1, strength = 1.0),async = false)
    }
    onButton(label = "Smile, Nod, and Say Thanks (VERY NICE)", section = Section.LEFT, color = Color.Green) {
        furhat.say{
            +voice?.style("Thanks",AzureVoice.Style.CHEERFUL)!!}
        furhat.gesture(Gestures.BigSmile)
        furhat.gesture(Gestures.Nod(iterations = 1, strength = 1.0),async = false)
    }
    //onButton(label = "Smile, Nod, and Say Awesome", section = Section.LEFT, color = Color.Green) {
    // furhat.say {
    // +voice?.style("Awesome",AzureVoice.Style.FRIENDLY)!! }
    // furhat.gesture(Gestures.BigSmile(duration = 2.5))
    // furhat.gesture(Gestures.Nod(iterations = 1, strength = 1.0),async = false)
    //}


}