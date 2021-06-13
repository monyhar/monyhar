# Introduction

This is a list of current and planned Chrome OS security features. Each feature is listed together with its rationale and status. This should serve as a checklist and status update on Chrome OS security.



# Details

## General Linux features

| **Feature** | **Status** | **Rationale** | **Tests** | **Bug** | **More thoughts or work needed?** |
|:------------|:-----------|:--------------|:----------|:--------|:----------------------------------|
| No Open Ports | implemented | Reduce attack surface of listening services. | [security\_NetworkListeners](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/security_NetworkListeners) |         | Runtime test has to whitelist test-system-only "noise" like sshd. See Issue 22412 (on Google Code) and [ensure\_\*](http://git.monyhar.org/gitweb/?p=monyharos/platform/vboot_reference.git;a=tree;f=scripts/image_signing) for offsetting tests ensuring these aren't on Release builds. |
| Password Hashing | When there is no TPM, scrypt is used. | Frustrate brute force attempts at recovering passwords. |
| SYN cookies | needs functional test | In unlikely event of SYN flood, act sanely. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |
| Filesystem Capabilities | runtime use only | allow root privilege segmentation | [security\_Minijail0](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/security_Minijail0) |
| Firewall    | needs functional test | Block unexpected network listeners to frustrate remote access. |           | Issue 23089 (on Google Code) |
| PR\_SET\_SECCOMP | needs functional test | Available for extremely restricted sandboxing. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) | Issue 23090 (on Google Code) |
| AppArmor    | not used   |
| SELinux     | not used   |
| SMACK       | not used   |
| Encrypted LVM | not used   |
| eCryptFS    | implemented | Keep per-user data private. | [login\_Cryptohome\*](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests) |
| glibc Stack Protector | needs functional test | Block string-buffer-on-stack-overflow attacks from rewriting saved IP. |           | Issue 23101 (on Google Code) | -fstack-protector-strong is used for almost all packages |
| glibc Heap Protector | needs functional test | Block heap unlink/double-free/etc corruption attacks. |           | Issue 23101 (on Google Code) |
| glibc Pointer Obfuscation | needs functional test | Frustrate heap corruption attacks using saved libc func ptrs. |           | Issue 23101 (on Google Code) | includes FILE pointer managling   |
| Stack ASLR  | needs functional test | Frustrate stack memory attacks that need known locations. |           |         |
| Libs/mmap ASLR | needs functional test | Frustrate return-to-library and ROP attacks. |           |         |
| Exec ASLR   | needs functional test | Needs PIE, used to frustrate ROP attacks. |           |         |
| brk ASLR    | needs functional test | Frustrate brk-memory attacks that need known locations. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |         |
| VDSO ASLR   | needs functional test | Frustrate return-to-VDSO attacks. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |         |
| Built PIE   | needs functional test | Take advantage of exec ASLR. | [platform\_ToolchainOptions](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/platform_ToolchainOptions) |         |
| Built _FORTIFY\_SOURCE_| needs functional test | Catch overflows and other detectable security problems. |           |         |
| Built RELRO | needs functional test | Reduce available locations to gain execution control. | [platform\_ToolchainOptions](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/platform_ToolchainOptions) |         |
| Built BIND\_NOW | needs functional test | With RELRO, really reduce available locations. | [platform\_ToolchainOptions](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/platform_ToolchainOptions) |         |
| Non-exec memory | needs functional test | Block execution of malicious data regions. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |
| /proc/PID/maps protection | needs functional test | Block access to ASLR locations of other processes. |
| Symlink restrictions | implemented | Block /tmp race attacks. | [security\_SymlinkRestrictions.py](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=blob;f=client/site_tests/security_SymlinkRestrictions/security_SymlinkRestrictions.py) | Issue 22137 (on Google Code) |
| Hardlink restrictions | implemented | Block hardlink attacks. | [security\_HardlinkRestrictions.py](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=blob;f=client/site_tests/security_HardlinkRestrictions/security_HardlinkRestrictions.py) | Issue 22137 (on Google Code) |
| ptrace scoping | implemented | Block access to in-process credentials. | [security\_ptraceRestrictions.py](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=blob;f=client/site_tests/security_ptraceRestrictions/security_ptraceRestrictions.py) | Issue 22137 (on Google Code) |
| 0-address protection | needs functional test | Block kernel NULL-deref attacks. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |
| /dev/mem protection | needs functional test | Block kernel root kits and privacy loss. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify)  | Issue 21553 (on Google Code) | crash\_reporter uses ramoops via /dev/mem |
| /dev/kmem protection | needs functional test | Block kernel root kits and privacy loss. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |
| disable kernel module loading | how about module signing instead? | Block kernel root kits and privacy loss. |
| read-only kernel data sections | needs functional test | Block malicious manipulation of kernel data structures. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |
| kernel stack protector | needs functional test | Catch character buffer overflow attacks. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |
| kernel module RO/NX | needs functional test | Block malicious manipulation of kernel data structures. | [kernel\_ConfigVerify](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/kernel_ConfigVerify) |
| kernel address display restriction | needs config and functional test | Frustrate kernel exploits that need memory locations. |           |         | Was disabled by default in 3.x kernels. |
| disable debug interfaces for non-root users | needs config and functional test | Frustrate kernel exploits that depend on debugfs |           | Issue 23758 (on Google Code) |
| disable ACPI custom\_method | needs config and functional test | Frustrate kernel exploits that depend on root access to physical memory |           | Issue 23759 (on Google Code) |
| unreadable kernel files | needs config and functional test | Frustrate automated kernel exploits that depend access to various kernel resources |           | Issue 23761 (on Google Code) |
| blacklist rare network modules | needs functional test | Reduce attack surface of available kernel interfaces. |
| syscall filtering | needs functional testing | Reduce attack surface of available kernel interfaces. |           | Issue 23150 (on Google Code) |
| vsyscall ASLR | medium priority | Reduce ROP target surface. |
| Limited use of suid binaries | implemented | Potentially dangerous, so minimize use. | [security\_SuidBinaries](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/security_SuidBinaries) |

## Chrome OS specific features

  * We use `minijail` for sandboxing:
    * [Design doc](https://www.monyhar.org/monyhar-os/monyharos-design-docs/system-hardening#Detailed_Design_73859539098644_6227793370126997)
    * Issue 380 (on Google Code)
  * Current sandboxing status:

|  |  |  |  | **Exposure** |  |  |  |  | **Privileges** |  | **Sandbox** |
|:-|:-|:-|:-|:-------------|:-|:-|:-|:-|:---------------|:-|:------------|
| **Service/daemon** | **Overall status** | **Usage** | **Comments** | **Network traffic** | **User input** | **DBus** | **Hardware (udev)** | **FS (config files, etc.)** | **Runs as**    | **Privileges needed?** | **uid**     | **gid**     | **Namespaces** | **Caps**    | **seccomp\_filters** |
| udevd | Low pri | Listens to udev events via netfilter socket |  | No           | No | No | Yes | No | root           | Probably | No          | No          | No          | No          | No          |
| session-manager | <font color='yellow'>P2</font>|  | Launched from /sbin/session\_manager\_setup.sh | No           | No | Yes | No | No | root           | Probably | No          | No          | No          | No          | No          |
| rsyslogd | Low pri | Logging |  | No           | No | No | No | Yes | root           | Probably | No          |             | No          | No          | No          |
| dbus-daemon | Low pri | IPC | Listens on Unix domain socket | Unix domain socket |  | Yes |  |  | messagebus     | Yes | Yes         | Yes         | No          | No          | No          |
| powerm | <font color='yellow'>P2</font>| Suspend to RAM and system shutdown. Handles input events for hall effect sensor (lid) and power button. |  | No           | No | Yes | Yes | Yes | root           | Probably | No          | No          | No          | No          | No          |
| wpa\_supplicant | Low pri | WPA auth |  | Yes          | Via flimflam | Yes | No | Yes, exposes management API through FS | wpa            | Yes | Yes         | Yes         | No          | Yes         | No          |
| shill | <font color='red'>P0</font>| Connection manager |  | Yes          | Yes | Yes | Yes | Yes | root           | Probably | No          | No          | No          | No          | No          |
| X | <font color='orange'>P1</font>|  |  | No (-nolisten tcp) | Yes | No | GPU | Yes | root           | x86: no, ARM: yes | No          | No          | No          | No          | No          |
| htpdate | Low pri | Setting date and time |  | Yes          | No | No | No | No | ntp            | Yes | Yes         | Yes         | No          | No          | No          |
| cashewd | Low pri | Network usage tracking |  | No           | No | Yes | No | No | cashew         | Yes | Yes         | Yes         | No          | No          | No          |
| chapsd | Low pri |  PKCS#11 implementation |  | No           | No | Yes | No | No | chaps          | Yes | Yes         | Yes         | No          | No          | No          |
| cryptohomed | <font color='orange'>P1</font>| Encrypted user storage |  | No           | Yes | Yes | No | No | root           | Probably | No          | No          | No          | No          | No          |
| powerd | Low pri | Idle or video activity detection. Dimming the backlight or turning off the screen, adjusting backlight intensity. Monitors plug state (on ac or on battery) and battery state-of-charge. |  | No           | Yes | Yes | Yes | Yes | powerd         | Probably | Yes         | No          | No          | No          | No          |
| modem-manager | <font color='orange'>P1</font>| Manages 3G modems |  | Indirectly   | Yes | Yes | Yes | No | root           | Probably not | No          | No          | No          | No          | No          |
| gavd | <font color='yellow'>P2</font>| Audio/video events and routing |  | No           | Yes | Yes | Yes | No | gavd           | Yes | Yes         | Yes         | No          | No          | No          |
| dhcpcd | Low pri | DHCP client |  | Yes          | Indirectly | No | No | No | dhcp           | Yes | Yes         | Yes         | No          | Yes         | No          |
| metrics\_daemon | <font color='yellow'>P2</font>| Metrics collection and uploading |  | Yes, but shouldn't listen | No | Yes | No | No | root           | Probably not | No          | No          | No          | No          | No          |
| cros-disks/disks | <font color='orange'>P1</font>| Removable media handling |  | No           | Yes | Yes | Yes | No | root           | Launches minijail | No          | No          | No          | No          | No          |
| avfsd | Low pri | Compressed file handling | Launched from cros-disks, uses minijail | Not in Chrome OS | Yes | No | No | Yes | avfs           | Yes | Yes         |             | No          | Yes         | Yes         |
| update\_engine | <font color='red'>P0</font>| System updates |  | Yes          | No | Yes | No | No | root           | Probably | No          | No          | No          | No          | No          |
| cromo | Low pri | Supports Gobi 3G modems |  | Indirectly   | Yes | Yes | Yes | Probably | cromo          | Yes | Yes         | Yes         | No          | No          | No          |
| bluetoothd | Low pri |  |  | Yes          | Yes | Yes | Yes | Yes | bluetooth      | Yes | Yes         | Yes         | No          | Yes         | No          |
| unclutter | Low pri | Hides cursor while typing |  |              | Yes |  |  |  | chronos        | Yes | Yes (via sudo) | No          | No          | No          | No          |
| cras | <font color='yellow'>P2</font>| Audio server |  | No           | Yes | Yes | Yes | No | cras           | Yes | Yes         | Yes         | No          | No          | No          |
| tcsd | <font color='yellow'>P2</font>| Portal to the TPM device driver |  | No           | Yes | Yes | Yes | Yes | tss            | Yes | Yes         | Yes         | No          | No          | No          |
| keyboard\_touchpad\_helper | <font color='orange'>P1</font>| Disables touchpad when typing |  |              | Yes |  |  |  | root           | Probably not | No          | No          | No          | No          | No          |
| logger | Low pri | Redirects stderr for several daemons to syslog |  | Indirectly   | Indirectly | No | No | No | syslog         | Yes | Yes         | Yes         | No          | No          | No          |
| login | <font color='yellow'>P2</font>| Helps organize Upstart events |  | No           | Indirectly | Yes | No | Yes | root           | Probably | No          | No          | No          | No          | No          |
| wimax-manager | <font color='orange'>P1</font>|  | Includes third-party library | Yes          | Indirectly | Yes | Yes | Yes | root           | Probably not | No          | No          | No          | No          | No          |
| mtpd | <font color='yellow'>P2</font>| Manages MTP devices | Includes third-party library | No           | Yes | Yes | Yes | No | mtp            | Yes | Yes         | Yes         | No          | Not needed  | Yes         |
| **Service/daemon** | **Overall status** | **Usage** | **Comments** | **Network traffic** | **User input** | **DBus** | **Hardware (udev)** | **FS (config files, etc.)** | **Runs as**    | **Privileges needed?** | **uid**     | **gid**     | **Namespaces** | **Caps**    | **seccomp\_filters** |
|  |  |  |  | **Exposure** |  |  |  |  | **Privileges** |  | **Sandbox** |

Enforced by [security\_SandboxedServices](http://git.monyhar.org/gitweb/?p=monyharos/third_party/autotest.git;a=tree;f=client/site_tests/security_SandboxedServices)

# References

  * https://wiki.ubuntu.com/Security/Features
  * http://wiki.debian.org/Hardening
  * http://www.gentoo.org/proj/en/hardened/hardened-toolchain.xml
  * http://www.awe.com/mark/blog/20101130.html