# A Relational Model Normalizer which normalizes up to BCNF

## Input Procedure:
* Relation: R(A1, A2, ..... , An)
* Functional Dependencies: Ai,Aj, ... ,Ak->Ap,Aq, ... ,Ar;Ai,Aj, ... ,Ak->Ap,Aq, ... ,Ar;Ai,Aj, ... ,Ak->Ap,Aq, ... ,Ar (Semicolon separated values)

## Changes Being Made:
* Made Separate FDs in relation Constructor
* Changed the isPartialKey Method.
* Some changes in separateFDs, decomposeInto2NFScheme
* 

## To Be Added:
* Stupid Attributes removal in starting and adding in ending
* 4NF -> BCNF Printing