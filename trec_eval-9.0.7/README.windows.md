## Compiling

To achieve a compilation of trec_eval for Windows, you will need Cygwin installed.

Download and install the [Cygwin](https://www.cygwin.com/) platform. You will need make and gcc installed by Cygwin. To achieve this, on top of the default Cygwin installation, it is recommended to install automake, make, gcc, cygwin-gcc and git from the Develop category, and permitting dependencies to be installed.

Then, to compile trec_eval, open a Cygwin Terminal, navigate using cd to the directory of the trec_eval source, and type make.

## Running

The resulting trec_eval.exe should be usable directly from the Cygwin Terminal.

The resulting trec_eval.exe should be usable on any machine without Cygwin installed, as long as the cygwin1.dll is available. For instance, place a copy the cygwin1.dll from Cygwin's /bin directory into the same directory as trec_eval.exe.
