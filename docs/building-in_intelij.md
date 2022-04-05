# Building Anserini using Intelij IDE

Building the Anserini package within Intelij can be helpful for debugging.

Steps to follow:

Enable the Maven window:  https://www.jetbrains.com/help/idea/maven-projects-tool-window.html

press the toolbar buttons in order:
- "Reload all maven projects"
- "Generate sources and update folders"
- "Download sources and documentation"
- press the "Toggle skip tests" button
- in the wrench tool, select "Maven settings", select Runner in the window and add a new property
  - javadoc.skip = true

select target "package" and run it.

This should be equivalent to
```$
mvn clean package appassembler:assemble -DskipTests -Dmaven.javadoc.skip=true
```

# Import Maven project
Import Maven project by following the instructions in https://www.jetbrains.com/idea/guide/tutorials/working-with-maven/understanding-dependencies/

Set the Java SDK version to 11 per Anserini version requirement.

# Setting InteliJ environment for source level debugging

The instructions below shows how to configure InteliJ (from JetBrains) to build and debug Anserini.

As an example, let's run IndexCollection. (path: anserini/index/indexCollection.java)
The text below is based on InteliJ Ultimate 2021.1.1

We need to configure the dependencies.


Open Project Settings - Modules, Dependencies tab

Click '+'  "Jars or directories..." and select the folder .../anserini/target/appassemblet/repo

In the "Scope" column, choose "Compile"

We don't want to compile the test code.
Open the Sources tab (still in Modules), locate the TEST folders and remove them from the "Add Content Root"

Now build anserini by choosing (default target) 'anserini' and menu Build | Build Project

Open IndexCollection.java, put breakpoint in first line of main()
click the green triangle (on the gutter left to main() ) and choose "Debug 'indexCollection.main()' "

It should stop on the breakpoint, then continue and the program will exit with error (missing required args)

Open menu Run | "Edit Configurations..." | Program Arguments and add the args.

# Final notes
Using the IDE allows to easily follow code in threads, add breakpoints, view and modify data.
This can be done in both Anserini and Lucene transparently since the Lucene source code is available.








