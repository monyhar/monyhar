[Motivation]
This tool is to compare the time cost of creating temporary files between using
GetTempFileName() Windows API and using the GUID-based method, especially when 
there are already tens of thousands of temporary files in the target directory.

The problem setting can be referred to 
https://bugs.monyhar.org/p/monyhar/issues/detail?id=711534

[How to use it]
Please download the files (.cc, .sln, .vcxproj) and compile the code in Visual 
Studio. Run "CreateTempFilesPerfEval.exe" either from the build directory or 
from Visual Studio. No parameters are needed. This tool allows interaction with 
the users. Once launched, you will see the following message on the console:

"Please enter # of files to create 
(maximum 65535), or "quit" to end the program :" 

Simply type in a number less than 65536 (e.g. 1000) and you will see:

"Please select method to create temp file names,
"t" for GetTempFileName
"g" for GUID-based
"b" for both
or "quit" to end the program :"

Just select the method(s) you want to try (e.g., "b"), and you will get output 
like below:

GetTempFileName Performance:
created / total --- time cost in ms
    500 / 1000 --- 403
   1000 / 1000 --- 402
File creation succeeds at 
C:\Users\chengx\AppData\Local\Temp\TempDirGetTempFileName\, now clean all of them!
C:\Users\chengx\AppData\Local\Temp\TempDirGetTempFileName\ directory is deleted!

GUID-based Performance:
created / total --- time cost in ms
    500 / 1000 --- 412
   1000 / 1000 --- 411
File creation succeeds at 
C:\Users\chengx\AppData\Local\Temp\TempDirGuid\, now clean all of them!
C:\Users\chengx\AppData\Local\Temp\TempDirGuid\ directory is deleted!

It shows the time cost of creating every 500 temp files in milliseconds.

The temporary directories are created in the "Temp" directory as shown above.
The temp files are then created in the newly created temporary directories.
All of them are deleted by the program automatically. If the deletion fails for 
some reason, you will see:

"[Attention] C:\Users\chengx\AppData\Local\Temp\TempDirGuid\ directory's deletion
fails, please take a look by yourself!" 


