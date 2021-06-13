[Motivation]
This tool is designed to test the usage of the SetThreadDescription WinAPI in
Chrome. In Chrome, the SetThreadDescription API has been enabled to set thread
names. However, since there is no tool support to retrieve thread names set by
GetThreadDescription, we'll still rely on SetNameInternal function in
platform_thread_win.cc to set thread names. Despite this, we need a tool to demo
the SetThreadDescription API works, even without the debugger to be present.

The problem setting can be referred to
https://bugs.monyhar.org/p/monyhar/issues/detail?id=684203

This tool incorporates the GetThreadDescription API trying to get names of all
threads in a process specified by its ID. If the thread names have been set by
SetThreadDescription API call like in Chrome, all thread ID/name pairs are
returned.

[Requirement]
Since SetThreadDescription/GetThreadDescription APIs are brought in Windows 10,
version 1607, this tool can only be effective if running in this version or
later ones.

[How to use it]
Please download the three files (.cc, .sln, .vcxproj) and compile the code in
Visual Studio. Run "ShowThreadNames.exe" either from the build directory or from
Visual Studio. No parameters are needed. This tool allows interaction with the
user. Once launched, it will show "Please enter the process Id, or "quit" to
end the program :" on the terminal. Simply type in the ID of any Chrome process
you are interested in, and you will get output like below:

thread_ID   thread_name
12116
10292
6532
6928
2488
11304
2256    AudioThread
9308    BrokerEvent
5668    BrowserWatchdog
8280    Chrome_HistoryThread
7472    Chrome_IOThread
6336    Chrome_ProcessLauncherThread
12212   CompositorTileWorker1/12212
3628    CrBrowserMain
6472    DnsConfigService
1980    IndexedDB
10560   TaskSchedulerBackgroundBlockingWorker0
11464   TaskSchedulerBackgroundWorker0
3156    TaskSchedulerForegroundBlockingWorker5
7660    TaskSchedulerForegroundWorker0
8216    TaskSchedulerServiceThread
11088   VideoCaptureThread

The threads are sorted by their names. Note that some threads have no names in
this example. If checking them using Visual Studio debugger, it is found that
they are ntdll.dll!WorkerThreads.
