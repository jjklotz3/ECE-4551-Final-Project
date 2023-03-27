package furhatos.app.finalproject.flow

import furhatos.app.finalproject.flow.main.Idle
import furhatos.app.finalproject.flow.main.Greeting
import furhatos.app.finalproject.setting.DISTANCE_TO_ENGAGE
import furhatos.app.finalproject.setting.MAX_NUMBER_OF_USERS
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users

val Init: State = state {
    init {
        /** Set our default interaction parameters */
        users.setSimpleEngagementPolicy(1.2, 1.7,2)

        goto(Idle)
    }

}
