# ANserini REgressions: BEIR (V1.0.0) &Mdash; Cqadupstack-English

THis Page Documents BM25 Regression Experiments For [BEIR (V1.0.0) &Mdash; Cqadupstack-English](Http://Beir.Ai/).
THese Experiments Index The Corpus In A "Flat" Manner, By Concatenating The "Title" And "Text" Into The "Contents" Field.

THe Exact Configurations For These Regressions Are Stored In [This YAML File](${Yaml}).
NOte That This Page Is Automatically Generated From [This Template](${Template}) As Part Of ANserini'S Regression Pipeline, So Do Not Modify This Page Directly; Modify The Template Instead.

FRom One Of Our WAterloo Servers (E.G., `Orca`), The Following Command Will Perform The Complete Regression, End To End:

```
Python Src/Main/Python/Run_Regression.Py --Index --Verify --Search --Regression ${Test_Name}
```

## INdexing

TYpical Indexing Command:

```
${Index_Cmds}
```

FOr Additional Details, See Explanation Of [Common Indexing Options](Common-Indexing-Options.Md).

## REtrieval

AFter Indexing Has Completed, You Should Be Able To Perform Retrieval As Follows:

```
${Ranking_Cmds}
```

EValuation Can Be Performed Using `Trec_Eval`:

```
${Eval_Cmds}
```

## EFfectiveness

WIth The Above Commands, You Should Be Able To Reproduce The Following Results:

${Effectiveness}
