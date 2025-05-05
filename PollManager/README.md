# Custom Poll Manager

This is a custom poll manager. I created this project to showcase my skills and interest in creating programs that gather and use data for practical everyday purposes. 

## This program will allow a user to: 
- **Create** and **maintain** a **database** of poll questions and possible answers. 
- **Create** and **maintain** a **database** of poll responses that consist of respondent information (first and last name, phone number, email, age, gender, income, and occupation type) and responder answers to all poll questions.
- **Use** the **database** of responses to **create** **statistics** and **graphs**.

## This program can be used for any polling purposes including but not limited to:

- Customer/Patient satisfaction
- Opinion/Political polling
- Marketing/Advertising research
- Workplace polling
- Event/Party planning

## User Stories
- As a user, I want to be able to create a poll with a unique name.

- As a user, I want to be able to create a poll question with possible answers and add it to a list of poll questions.

- As a user, I want to be able to modify or remove poll questions and their possible answers.

- As a user, I want to be able to enter and add to the list a poll response that consist of respondent information (first and last name, phone number, email, age, gender, income, and occupation type) and responder answers to all poll questions.

- As a user, I want to be able to modify or remove poll responses.

- As a user, I want to be able to view a list of all poll responses and summarized response statistics for each poll question for respondents who meet specific criteria such as age range, income range, occupation type, or gender.

- As a user, I want to be able to create graphs for poll answers of respondents who meet specific criteria such as certain age range, income range, occupation type, or gender.

- As a user, I want the option to save my current poll as a file.

- As a user, I want the optioin to load a saved file and use the poll it contains.

# Instructions for End User

- You can add and view poll questions in the **Manage Questions** tab. If you have no questions you will be prompted to create one. If there are already questions in the poll, you can add new questions by pressing the **Add New Question** button. To add answers to a question simply type one answer at a time into the text pannel below the **Enter New Answer Below** panel, hit **Enter** or press the **Add Answer** button. To go through the list of questions in the poll, use the **Next Question** and the **Prev Question** buttons

- You can add and view responders in the **Manage Responders** tab. If you have no responders, you will be prompted to create one. If you already have responders, you can add a new one by pressing the **Add Responder** button. It is mandatory that all data fields be filled correctly as per displayed instructions when hovering over each field. Responders have a list of question responses that are modified when the responder runs your poll. Additionaly, there is an option to add a new responder when running a poll. To go through the list of responders in the poll, use the **Next Responder** and the **Prev Responders** buttons

- You can run a poll in the **Poll** tab by pressing the **Run An Instance Of \*Poll Name\*** button. You can choose a poll responder from a list of responders or create a new one who will take the poll instead. 

- You can view poll statistics in the **Poll Statistics** tab. You can see a visual component pie chart of responses to a displayed question. You can filter the responses by additional criteria using the filter buttons. Be aware that the filters are applied consecutively, and filters that would result in all responses being filtered out will be not applied, resulting in no changes to the pie chart. To clear the filters applied by use of filter buttons, simply press the **Clear Filters** button. To go through the list of questions and view their statistics, use the **Next Question** and the **Prev Question** buttons.

- You can change the name of your poll by pressing the **Change Poll Name** button in the **Poll** tab. Next, enter the desired name and hit **Enter** to confirm.

- You can save the state of the poll by pressing the **Save Poll** button in the **Poll** tab. Be aware that there is only one save file and pressing the save button will overwrite it.

- You can reload the state of the poll by pressing the **Load Poll** button in the **Poll** tab. Be aware that loading a poll does not save the current state and overwrites it.

# Phase 4: Task 2
Example of event log upon closing the program:

Event Log:
Tue Nov 26 17:09:12 PST 2024
Added new question with prompt: What is your favorite Season
Tue Nov 26 17:09:17 PST 2024
Added answer: Spring to question with prompt: What is your favorite Season
Tue Nov 26 17:09:22 PST 2024
Added answer: Summer to question with prompt: What is your favorite Season
Tue Nov 26 17:09:25 PST 2024
Added answer: Fall to question with prompt: What is your favorite Season
Tue Nov 26 17:09:27 PST 2024
Added answer: Winter to question with prompt: What is your favorite Season
Tue Nov 26 17:10:32 PST 2024
Changed question with prompt: What is your favorite Season to new prompt: Out of the two, which is your favorite season
Tue Nov 26 17:10:35 PST 2024
Removed answer: Spring from question with prompt: Out of the two, which is your favorite season
Tue Nov 26 17:10:38 PST 2024
Removed answer: Fall from question with prompt: Out of the two, which is your favorite season
Tue Nov 26 17:11:22 PST 2024
Added responder: John Doe
Tue Nov 26 17:11:59 PST 2024
Added responder: Filip Poloczek
Tue Nov 26 17:12:23 PST 2024
Responder first name updated from John to Jane
Tue Nov 26 17:12:23 PST 2024
Responder: Jane Doe gender id updated
Tue Nov 26 17:13:40 PST 2024
Responder: Jane Doe chose answer: "Summer" to the question: Out of the two, which is your favorite season
Tue Nov 26 17:13:49 PST 2024
Responder: Filip Poloczek chose answer: "Winter" to the question: Out of the two, which is your favorite season
Tue Nov 26 17:13:59 PST 2024
Gender Id filter applied
Tue Nov 26 17:14:05 PST 2024
Filters cleared
Tue Nov 26 17:14:17 PST 2024
Responder: Filip Poloczek updated their answer to: "Summer" for the question: Out of the two, which is your favorite season
Tue Nov 26 17:14:42 PST 2024
Removed responder: Filip Poloczek
End Of Event Log

# Phase 4: Task 3
Reflection:
Overall, I am satisfied with my project. I accomplished the user stories I initially proposed in phase 0. I believe that some methods in the Poll class could be improved by implementing exceptions, which would replace the gaurds I set up in the GuiPollApp and PollApp. This would allow the model package to not rely on the ui package as much for desired functionality. The classes in the ui package: GuiPollApp and PollApp share some code between them, it would be possible to create a "ui parent" superclass which would refactor the common code and additionaly improve class design. Additionaly, the GuiPollApp and PollApp classes are very large and many of their parts (GuiPollApp has 4 different tabs for example) could be refactored into separete classes in order to imrpove the class design and make the code easier to understand. 