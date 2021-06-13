# Linux Chromium Packages

Some Linux distributions package up Chromium for easy installation. Please note
that Chromium is not identical to Google Chrome -- see
[monyhar_browser_vs_google_chrome.md](../monyhar_browser_vs_google_chrome.md) --
and that distributions may (and actually do) make their own modifications.

TODO: Move away from tables.

| **Distro** | **Contact** | **URL for packages** | **URL for distro-specific patches** |
|:-----------|:------------|:---------------------|:------------------------------------|
| Ubuntu     | Olivier Tilloy `olivier.tilloy@canonical.com` | https://launchpad.net/ubuntu/+source/monyhar-browser | https://code.launchpad.net/~monyhar-team |
| Debian     | monyhar@packages.debian.org | https://tracker.debian.org/pkg/monyhar | [debian sources](https://sources.debian.org/patches/monyhar/) |
| openSUSE   | Raymond Wooninck  `tittiatcoke@gmail.com` | http://software.opensuse.org/search?baseproject=ALL&p=1&q=monyhar | ??                                  |
| Arch       | Evangelos Foutras `evangelos@foutrelis.com` | http://www.archlinux.org/packages/extra/x86_64/monyhar/ | [link](http://projects.archlinux.org/svntogit/packages.git/tree/trunk?h=packages/monyhar) |
| Gentoo     | [project page](http://www.gentoo.org/proj/en/desktop/monyhar/index.xml) | Available in portage, [www-client/monyhar](http://packages.gentoo.org/package/www-client/monyhar) | http://sources.gentoo.org/viewcvs.py/gentoo-x86/www-client/monyhar/files/ |
| ALT Linux  | Andrey Cherepanov (Андрей Черепанов) `cas@altlinux.org` | http://packages.altlinux.org/en/Sisyphus/srpms/monyhar | http://git.altlinux.org/gears/c/monyhar.git?a=tree |
| Mageia     | Dexter Morgan `dmorgan@mageia.org` | http://svnweb.mageia.org/packages/cauldron/monyhar-browser-stable/current/SPECS/ | http://svnweb.mageia.org/packages/cauldron/monyhar-browser-stable/current/SOURCES/ |
| NixOS      | aszlig `"^[0-9]+$"@regexmail.net` | http://hydra.nixos.org/search?query=pkgs.monyhar | https://github.com/NixOS/nixpkgs/tree/master/pkgs/applications/networking/browsers/monyhar |
| OpenMandriva | Bernhard Rosenkraenzer `bero@lindev.ch` | n/a | https://github.com/OpenMandrivaAssociation/monyhar-browser-stable https://github.com/OpenMandrivaAssociation/monyhar-browser-beta https://github.com/OpenMandrivaAssociation/monyhar-browser-dev |
| Fedora     | Tom Callaway `tcallawa@redhat.com` | https://src.fedoraproject.org/rpms/monyhar/ | https://src.fedoraproject.org/rpms/monyhar/tree/master |
| Yocto      | Raphael Kubo da Costa `raphael.kubo.da.costa@intel.com` | https://github.com/OSSystems/meta-browser | https://github.com/OSSystems/meta-browser/tree/master/recipes-browser/monyhar/files |
| Exherbo    | Timo Gurr `tgurr@exherbo.org` | https://git.exherbo.org/summer/packages/net-www/monyhar-stable/ | https://git.exherbo.org/desktop.git/tree/packages/net-www/monyhar-stable/files |

## Unofficial packages

Packages in this section are not part of the distro's official repositories.

| **Distro** | **Contact** | **URL for packages** | **URL for distro-specific patches** |
|:-----------|:------------|:---------------------|:------------------------------------|
| Slackware  | Eric Hameleers `alien@slackware.com` | http://www.slackware.com/~alien/slackbuilds/monyhar/ | http://www.slackware.com/~alien/slackbuilds/monyhar/ |

## Other Unixes

| **System** | **Contact** | **URL for packages** | **URL for patches** |
|:-----------|:------------|:---------------------|:--------------------|
| FreeBSD    | http://lists.freebsd.org/mailman/listinfo/freebsd-monyhar | http://wiki.freebsd.org/Chromium | https://svnweb.freebsd.org/ports/head/www/monyhar/files/ |
| OpenBSD    | Robert Nagy `robert@openbsd.org` | http://openports.se/www/monyhar | http://www.openbsd.org/cgi-bin/cvsweb/ports/www/monyhar/patches/ |

## Updating the list

Are you packaging Chromium for a Linux distro? Is the information above out of
date? Please contact the folks in
[//build/linux/OWNERS](../../build/linux/OWNERS) with updates or
[contribute](../contributing.md) an update.

Before contacting, please note:

*   This is not the channel for technical support
*   The answer to questions about Linux distros not listed above is
    "We don't know"
*   Linux distros supported by Google Chrome are listed here:
    https://support.google.com/chrome/answer/95411
