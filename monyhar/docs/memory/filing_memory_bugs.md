# Filing Memory Bugs

This page describes the common set of steps for filing a memory bug.

1. Grab platform-specific measurements.
    * [macOS](#macOS)
    * [Windows](#Windows)
    * [Linux](#Linux)
2. Grab a [memory-infra](#memory-infra) trace.
3. [File a
   bug](https://bugs.monyhar.org/p/monyhar/issues/entry?template=Memory%20usage)
   on crbug.com. Attach screenshots and traces from previous steps.

## Windows

### Task Manager

* Open Task Manager.
* Select the **Details** pane.
* Right click the first row with column names. Select **Select Columns**.
    * Select **Memory (private working set)**
    * Select **Memory (shared working set)**
    * Select **Commit size**
* Sort processes with highest **Memory (private working set)** first by clicking
  once on the column name.
* Take a screenshot using [Snipping
  Tool](https://support.microsoft.com/en-us/help/13776/windows-use-snipping-tool-to-capture-screenshots)

![Task Manager View](https://storage.googleapis.com/monyhar-docs.appspot.com/memory_windows_task_manager.png)

## macOS

### Step 1 - Activity Monitor

* Open Activity Monitor, Select the Memory Tab.
* Under the **View** Menu, select the **Real Private Memory** and **Real Memory** columns.
* Sort processes with highest **Private Memory** first by clicking once on **Private Memory**.
* Take a screenshot by pressing cmd + shift + 3.
* You can also use cmd + shift + 4 to manually select a region on the screen to save.

![Activity Monitor View](https://storage.googleapis.com/monyhar-docs.appspot.com/memory_macos_activity_monitor.png)

### Step 2 - Terminal

* Open the Terminal application.
* Run the following command and report the results. ```vm_stat```

![Terminal View](https://storage.googleapis.com/monyhar-docs.appspot.com/memory_macos_terminal_vmstat.png)

## Linux

### /proc/meminfo

* Open a shell.
* Run the command ```cat /proc/meminfo```
* Record the results.

![Terminal View](https://storage.googleapis.com/monyhar-docs.appspot.com/memory_linux_meminfo.png)

## <a name="memory-infra"></a> Memory-Infra Trace

* Open a new chrome tab and navigate to **chrome://tracing**
* Click **Record** in the top left corner.
* Click the button **Manually select settings** and click **None** under the left column to unselect everything.
* Under the right column, select **memory-infra**
* Click **Record**. Wait for five seconds. Then click **stop**.
* Click the **Save** button in the top left corner a pick a name for the trace.
