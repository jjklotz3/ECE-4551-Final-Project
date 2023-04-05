# ECE 4551 Final Project
Furhat Skill program that utilizes semi-autonomous NLP as well as teleoperated capabilities
## Description
This project was about facilitating a human robot interaction that would investigate how different robot and human personalities collaborate with each other on a task.
Specifically, each participant would play 6 games of higher or lower with a robot exhibiting high conscientious and low conscientious personality traits. 
The interaction starts off with a small introduction between the robot and participant. Then the interaction moves into the game. Each participant will play 3 games with the high conscientious robot and 3 with the low conscious robot.

                                Rules of Higher and Lower Game:
    1. The researchers will pick a number between 10-100. 
    2. The participant and the robot will collaborate to guess the same number the researcher picked.
    3. The participant will have the ultimate say of what number the group will guess but the particpant has to let the robot give a suggestion before they guess.
    4. Once the participant makes a guess, the researcher will tell the team if the guess is higher, lower, or the correct number the researcher picked.

Once the participant plays 3 games with one robot personality, they will go to a short break. Once the operator decides the break is over, the next 3 games with the other personality will start.
Once all 6 games are completed, the robot and participant will say goodbye and the interaction will end. 

## Usage
Max number of users is set to: 1
Requires a person to control the robot in real-time through the interaction. Also requires a microphone connected to the Furhat robot, so it is able to listen to the participant.
To access mainfile, FinalProject -> src -> main -> kotlin/furhatos/app/finalproject -> flow -> main -> idle.kt
