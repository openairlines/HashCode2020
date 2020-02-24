# HashCode2020

We are four developers at OpenAirlines, a startup based in Toulouse, France. On Friday, we participated in the 1st qualification round of the Google hashcode coding competition.

The event took place from 5:30pm to 9:30pm UTC, 4 hours during which we had to solve an optimization problem. We were competing as a team from a local hub, Toulouse GDG, with other teams and of course beers, chips & pizzas.

Let me explain you the process we underwent to try to solve this problem.

# Problem

As Google did to fill up its Google Books database, we had to find the best process to scan books (with a score S) from a number of libraries (L) given some constraints and to get the highest score (the sum of the scores of all books that are scanned within D days).

Each library has a number of books (B), a number of books that can be scanned each day (M) and a sign up time for scanning (T).
Libraries can only have one copy of a book but many libraries can have a copy of the same book. When multiple copies of the same books are scanned, their score are only counted once.Only one library can sign up at a time.

Given six different input datasets of different sizes, we had to provide for each one of them an ordered list of libraries to sign up along the ordered list of their books to scan.

You can find the [problem statement here](statement/hashcode_2020_online_qualification_round.pdf) or [at Google](https://codingcompetitions.withgoogle.com/hashcode/archive) when it will be published.

# Resolution

Of course, the aim of the exercise was not to compute every options, there are too many: all combinations of libraries combined with all combinations of their books! So, we had to code an algorithm that would lead us to the best approaching result.

Our first thought was to give libraries a rank and select the top ranked libraries to scan from within the time frame.

We knew that this rank would be based on the library sign up time, its scanning rate, its number of books, and the total score of its books.

First of all, we wrote some boilerplate code in Kotlin to parse input files and design our objects that would ease later iterations.

## The base metric for rank

Here, the rank is the maximum score a library can get within a time frame.
We computed it as follow:
* Compute the maximum number of books that can be scanned from a library within a time frame
maxBooks = (d - T) * M with d the time frame, d0 = D
* Sort library books by decreasing score
* Add the score of each book until the maximum is reached or there are no more books
maxScore += orderedBooks[i].score with i from 0 to min(B, maxBooks)
The whole algorithm was, until there are no more time or libraries left:
* Compute all library ranks
* Pick the best ranked library and add it to the result
* Re-compute the rank for other with a new time frame substracted from the picked library sign up time (d = d - T)

This first approach allowed us to obtain a reasonnable score for all input datasets in a good amount of time. We submitted a first version in 1h30, reaching a score of 16,000,000.

## First optimisation

With this method the same book may be scanned twice from different libraries, thus not increasing our final score. A simple optimisation was to consider these duplicates by keeping a cache of already scanned books and remove them from the library score when computing its new rank.
maxScore += orderedBooks[i].score with i from 0 to min(B, maxBooks) if orderedBooks[i] not already scanned

With this approach we could only gain a few points more, at that point, we reached nearly 20,000,000.

## Towards a ratio

At that point we started running out of precise ideas. We decided to improve the rank by leveraging it (use an average instead of a maximum) with the number of days it would take the libraries to scan the books we take into account in the rank computation.
newRank = rank / i

## Where we got lost

We were running out of ideas, so we tried to focus on particular cases. For instance, all libraries of one of the input dataset had the same sign up time, the same scanning rate and all books with the same score. In this case, the rank could just match the count of not already scanned books.

## A better ratio

Later at the bar (a little too late for the competition), drinking one last beer we found out that our ratio was not a good choice. We should have cut up the sign up time instead, because of the blocking time it was introducing. Indeed, the more time a library takes to sign up, the more it should decrease its rank. We then reached 25,000,000, which is the score you can have running our code you'll find on github.

|Input|totalDays|score  |theoric max score|books delivered|theoric max books|libs delivered|theoric max libs|
|:----|--------:|------:|----------------:|--------------:|----------------:|-------------:|---------------:|
|a.txt|7       	|     21|               21|    6          |                6|             2|               2|
|b.txt|1000  	  |5822900|         10000000|58229          |           100000|            90|             100|
|c.txt|100000  	|3695589|         30076415|11995          |           100000|           812|           10000|
|d.txt|30001   	|5028010|          5109000|77354          |            78600|         15000|           30000|
|e.txt|200     	|5034897|         12548648|29383          |           100000|           155|            1000|
|f.txt|700      |5308034|         40111142|12993          |           100000|            17|            1000|

### Further ideas

We had some ideas we didn't had time to explore during the limited time of the contest... We computed some statistics on libraries, books... We also tried to display our results. It's not impossible to have a future version on this repository that will reach a higher score and generate some beautiful charts ;-).

# Final words

At the end, our score placed us in the first half of all competitors around the world, which is not bad, but we mainly had a lot of fun and confirmed we were working out very good as a team!

