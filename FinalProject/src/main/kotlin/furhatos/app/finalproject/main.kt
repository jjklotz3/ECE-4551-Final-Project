package furhatos.app.finalproject

import furhatos.app.finalproject.flow.Init
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class FinalprojectSkill : Skill() {
    override fun start() {
        Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
