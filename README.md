# HashCode2020

We are four developers at [OpenAirlines](https://openairlines.com/), a startup helping airlines reducing their fuel consumption and CO2 emissions, based in Toulouse, France. The 20th of February, we participated in the 1st qualification round of the __[Google hashcode](https://codingcompetitions.withgoogle.com/hashcode)__ coding competition.

The event took place from 5:30pm to 9:30pm UTC, 4 hours during which we had to solve an __optimization problem__. We were competing as a team from a local hub, [Toulouse GDG](https://www.gdgtoulouse.fr/), with other teams and of course beer, chips & pizzas.

Let me explain you the process we underwent to try to solve the problem.

## Problem

As Google did to fill up its Google Books database, we had to find the best process to scan __books__ (with a __score__ _S_) from a number of __libraries__ (_L_) given some constraints and to get the highest score (the sum of the scores of all books that are scanned within _D_ __days__).

Each library has a __number of books__ (_B_), a __number of books that can be scanned each day__ (_M_) and a __sign up time__ for scanning (_T_).
Libraries can only have one copy of a book but many libraries can have a copy of the same book. When multiple copies of the same books are scanned, their score are only counted once. Only one library can sign up at a time.

Given six different input datasets of different sizes, we had to provide for each one of them an ordered list of libraries to sign up along the ordered list of their books to scan.

You can find the problem statement [here](statement/hashcode_2020_online_qualification_round.pdf) or [at Google](https://codingcompetitions.withgoogle.com/hashcode/archive) when it is published.

## Resolution

Of course, the aim of the exercise was not to compute every options; there are too many: all combinations of libraries combined with all combinations of their books! So, we had to code an algorithm that would lead us to the best approaching result.

Our first thought was to __give libraries a rank__ and select the top ranked libraries to scan from within the time frame.

We knew that this rank would be based on the library sign up time, its scanning rate, its number of books, and the total score of its books.

First of all, we wrote some __boilerplate code in Kotlin__ to parse input files and design our objects that would ease later iterations.

### The base metric for rank

Here, the rank is the __maximum score a library can get within a time frame__.

We computed it as follow:
* Compute the maximum number of books that can be scanned from a library within a time frame

`maxBooks = (d - T) * M with d the time frame, d0 = D`

* Sort library books by decreasing score

* Add the score of each book until the maximum is reached or there are no more books

`maxScore += orderedBooks[i].score with i from 0 to min(B, maxBooks)`

The whole algorithm was, until there are no more time or libraries left:
* Compute all library ranks
* Pick the best ranked library and add it to the result
* Re-compute the rank for other with a new time frame substracted from the picked library sign up time `(d = d - T)`

This first approach allowed us to obtain a reasonable score for all input datasets in a good amount of time. We submitted a first version in less than 2h, reaching a score of 16,000,000.

### First optimization

With this method the same book may be scanned twice from different libraries, thus not increasing our final score. A simple optimization was to consider these duplicates by __keeping a cache of already scanned books__ and remove them from the library score when computing its new rank.

`maxScore += orderedBooks[i].score with i from 0 to min(B, maxBooks) if orderedBooks[i] not already scanned`

We were able to gain a few points more only (nearly 20,000,000 score).

### Towards a ratio

At that point we started running out of precise ideas. We decided to improve the rank by __leveraging it__ instead of a maximum__) with the number of days it would take the libraries to scan the books we take into account in the rank computation.

`newRank = rank / i`

### Where we got lost

We were running out of ideas, so we tried to focus on particular cases.

For instance, all libraries of one of the input dataset ([d.txt](inputs/d.txt)) had the same sign up time, the same scanning rate and all books with the same score. In this case, the rank could just match the count of not already scanned books.

### A better ratio

Later at the bar (a little too late for the competition), drinking one last beer we found out that our ratio was not a good choice. We should have cut up the sign up time instead, because of the blocking time it was inducing. Indeed, the more time a library takes to sign up, the more it should decrease its rank.
 
We finally reached 25,000,000, which is the score you'll get running our code you can find on [github](https://github.com/openairlines/HashCode2020).

_Some statistics:_

|Input|totalDays|score  |theoric max score|books delivered|theoric max books|libs delivered|theoric max libs|
|:----|--------:|------:|----------------:|--------------:|----------------:|-------------:|---------------:|
|a.txt|7       	|     21|               21|    6          |                6|             2|               2|
|b.txt|1000  	  |5,822,900|         10,000,000|58,229     |          100,000|            90|             100|
|c.txt|100,000 	|3,695,589|         30,076,415|11,995     |          100,000|           812|          10,000|
|d.txt|30001   	|5,028,010|          5,109,000|77,354     |           78,600|        15,000|          30,000|
|e.txt|200     	|5,034,897|         12,548,648|29,383     |          100,000|           155|            1000|
|f.txt|700      |5,308,034|         40,111,142|12,993     |          100,000|            17|            1000|

_Example of display for file [b.txt](inputs/b.txt):_

First column is the score of the library, `O` is for signup, `v` when library is scanning books, `-` for blocked signup. 
```
099900: Ovvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
099800: -Ovvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
099600: --OOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
099400: ----OOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
099200: ------OOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
099000: --------OOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
098800: ----------OOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
098500: ------------OOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
098200: ---------------OOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
097900: ------------------OOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
097600: ---------------------OOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
097200: ------------------------OOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
096800: ----------------------------OOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
096400: --------------------------------OOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
096000: ------------------------------------OOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
095600: ----------------------------------------OOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
095100: --------------------------------------------OOOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
094600: -------------------------------------------------OOOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
094100: ------------------------------------------------------OOOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
093600: -----------------------------------------------------------OOOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
093000: ----------------------------------------------------------------OOOOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
092400: ----------------------------------------------------------------------OOOOOOvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
091800: ----------------------------------------------------------------------------OOOOOOvvvvvvvvvvvvvvvvvvvvvvvvvv
091200: ----------------------------------------------------------------------------------OOOOOOvvvvvvvvvvvvvvvvvvvv
090500: ----------------------------------------------------------------------------------------OOOOOOOvvvvvvvvvvvvv
089800: -----------------------------------------------------------------------------------------------OOOOOOOvvvvvv
```

### Further ideas

We had some ideas we didn't have time to explore during the limited time of the contest... We computed some statistics on libraries, books... We also tried to display our results, and being able to have a look at them was very interesting: we are now thinking about __combining and iterating on the metrics__, taking into account the remaining time to start the signing up of some libraries later...
There might be future versions of the code in the repository that would hopefully reach a higher score and generate some beautiful charts ;-).

## Final words

At the end, our score placed us in the first half of all competitors around the world, which is not bad, but we mainly had a lot of fun and confirmed we were working out very good as a team!

