# HashCode2020

Our solution to the __[Google hashcode](https://codingcompetitions.withgoogle.com/hashcode)__ 1st qualification round in Kotlin.

Just __import and run__!

You can read the full explanation [here](https://dev.to/hjonin/our-experience-at-google-hashcode-2020-2j0p).

## Problem

As Google did to fill up its Google Books database, we had to find the best process to scan __books__ (with a __score__ _S_) from a number of __libraries__ (_L_) given some constraints and to get the highest score (the sum of the scores of all books that are scanned within _D_ __days__).

Each library has a __number of books__ (_B_), a __number of books that can be scanned each day__ (_M_) and a __sign up time__ for scanning (_T_).
Libraries can only have one copy of a book but many libraries can have a copy of the same book. When multiple copies of the same books are scanned, their score are only counted once. Only one library can sign up at a time.

Given six different input datasets of different sizes, we had to provide for each one of them an ordered list of libraries to sign up along the ordered list of their books to scan.

You can find the problem statement [here](statement/hashcode_2020_online_qualification_round.pdf) or [at Google](https://codingcompetitions.withgoogle.com/hashcode/archive) when it is published.

## Results

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

## Further ideas

We had some ideas we didn't have time to explore during the limited time of the contest... We computed some statistics on libraries, books... We also tried to display our results, and being able to have a look at them was very interesting: we are now thinking about __combining and iterating on the metrics__, taking into account the remaining time to start the signing up of some libraries later...

There might be future versions of the code in the repository that would hopefully reach a higher score and generate some beautiful charts ;-).
