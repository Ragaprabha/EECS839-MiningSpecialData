NAME: Ragaprabha Chinnaswamy
KUID#: 2830383

PROBABILISTICS APPROXIMATION:
	In this project, MLEM2 is calculated using the following 3 algorithm,
	1) SINGLETON
	2) SUBSET
	3) CONCEPT
			
PREREQUISITE:
	1. Any Java supported machine- eg: Windows, Mac, Linux.
	2. Required Java version 1.7 or higher
	3. Required Java Compiler 1.7 or higher
	4. Referenced Libraries - guava-18.0 jar (Available in project workspace)
	5. Input Text are to be placed directly in the workspace folder. EECS839/<input.txt>
	
PROJECT DETAILS:
	1. This project is implemented for the following 3 mentioned algorithm. (Singleton, Subset & Concept)
	2. The project must have the required libraries before running.
	3. The Main() function is available in the InputData.Java under package eecs839.datamining.lem2
	4. This project requires an input from the user to determine the algorithm to be executed, then an Alpha value and also the input data as a text file with .d as extension (Eg., input.d).
	5. The output file same as the name of the input file with extension .data will be available after the program is executed in the same location as input file (Eg., input.data)
	
********************************************************************************************************************************************************************************************
OUTPUT CRITERIA:
1. The user must select an Algorithm type.

Enter the Algorithm to Execute:
1. Singleton
2. Subset
3. Concept

(If the user enters Invalid number other than 1,2 or 3)
Enter the Algorithm to Execute:
1. Singleton
2. Subset
3. Concept
4
Enter an number between 1 and 3: 
1
_________________________________________________________________
2. Now the user is expected to enter Alpha Value:

Enter Alpha Value (Between 0 & 1):

(If the user enters invalid input such as String)
Enter Alpha Value (Between 0 & 1):
m23
Invalid input.. Enter a numeric value between 0 & 1 for Alpha:
.6

(If the user enters numeric value not between 0 & 1)
Enter Alpha Value (Between 0 & 1): 
2
Alpha value must be between 0 & 1: 
.75
_________________________________________________________________
3. Now the user has to enter the input file name with extension .d
Enter the input file name:

(When Input File is not available in the workspace location)
Enter the input file name: 
hh.txt
File not found
******************************************************************************************************************************************************************
1. Output for the FIRST algorithm(Singleton),

INPUT DATA:
< a a a d >
[ Temperature Headache Cough Flu ]
36 no no no
36 yes * no
38 - no no
? ? yes yes
40 no no yes
* yes yes yes
++++++++++++++++++++++++++++++++++++++++
OUTPUT - (USER INPUT):
Enter the Algorithm to Execute:
1. Singleton
2. Subset
3. Concept
1

Enter Alpha Value (Between 0 & 1): 
.5
Enter the input file name: 
input.d

Output File:
Input data file Name: input.d
No of Attributes :  3
No of Decision :  1
Total No.of Rows: 6

The Algorithm type used: SINGLETON
Alpha Value is: 0.5

The Final rule sets are:
(Cough,yes) --> (Flu,yes)
(Temperature,39.0..40.0) --> (Flu,yes)
(Temperature,36.0..39.0) --> (Flu,no)

The Minimal Rule sets are:
(1, 2, 2)
(Cough,yes) --> (Flu,yes)

(1, 1, 1)
(Temperature,39.0..40.0) --> (Flu,yes)

(1, 3, 3)
(Temperature,36.0..39.0) --> (Flu,no)
_____________________________________________________________________________
2. Output for the FIRST algorithm(Subset),

INPUT DATA:
< a a a d >
[ Temperature Headache Cough Flu ]
36 no no no
36 yes * no
38 - no no
? ? yes yes
40 no no yes
* yes yes yes
++++++++++++++++++++++++++++++++++++++++
OUTPUT - (USER INPUT):
Enter the Algorithm to Execute:
1. Singleton
2. Subset
3. Concept
2

Enter Alpha Value (Between 0 & 1):
.75
Enter the input file name: 
input.d

Output File:
Input data file Name: input.d
No of Attributes :  3
No of Decision :  1
Total No.of Rows: 6

The Algorithm type used: SUBSET
Alpha Value is: 0.75

The Final rule sets are:
(Temperature,36.0..39.0) & (Headache,no) --> (Flu,no)

The Minimal Rule sets are:
(2, 1, 1)
(Temperature,36.0..39.0) & (Headache,no) --> (Flu,no)
_____________________________________________________________________________
3. Output for the FIRST algorithm(Concept),

INPUT DATA:
< a a a d >
[ Temperature Headache Cough Flu ]
36 no no no
36 yes * no
38 - no no
? ? yes yes
40 no no yes
* yes yes yes
++++++++++++++++++++++++++++++++++++++++
OUTPUT - (USER INPUT):
Enter the Algorithm to Execute:
1. Singleton
2. Subset
3. Concept
3

Enter Alpha Value (Between 0 & 1): 
.25
Enter the input file name: 
input.d

Output File:
Input data file Name: input.d
No of Attributes :  3
No of Decision :  1
Total No.of Rows: 6

The Algorithm type used: CONCEPT
Alpha Value is: 0.25

The Final rule sets are:
(Cough,yes) --> (Flu,yes)
(Temperature,39.0..40.0) --> (Flu,yes)
(Temperature,36.0..39.0) --> (Flu,no)

The Minimal Rule sets are:
(1, 2, 2)
(Cough,yes) --> (Flu,yes)

(1, 1, 1)
(Temperature,39.0..40.0) --> (Flu,yes)

(1, 3, 3)
(Temperature,36.0..39.0) --> (Flu,no)