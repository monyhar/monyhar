# Atom

[Atom](https://atom.io/)
([Wikipedia](https://en.wikipedia.org/wiki/Atom_(text_editor))) is a
multi-platform code editor that is itself based on Chromium.
[Turtles aside](https://en.wikipedia.org/wiki/Turtles_all_the_way_down), Atom
has a growing community and base of installable plugins and themes.

You can download and install via links from the
[main Atom site](https://atom.io/). If you're interested in checking out the
code and contributing, see the
[developer page](https://github.com/atom/atom/blob/master/docs/build-instructions/linux.md).

[TOC]

## Workflow

A typical Atom workflow consists of the following.

1. Use `Ctrl-Shift-R` to find a symbol in the `.tags` file or `Ctrl-P` to find
   a file by name.
2. Switch between the header and the source using `Alt-O`(`Ctrl-Opt-S` on OSX).
3. While editing, `you-complete-me` package helps with C++ auto-completion and
   shows compile errors through `lint` package.
4. Press `Ctrl-Shift-P` and type `format<Enter>` to format the code.
5. Select the target to build by pressing `F7` and typing, for example,
   `base_unittests`.
6. Rebuild again by pressing `F9`.

## Atom packages

To setup this workflow, install Atom packages for Chrome development.

```
$ apm install build build-ninja clang-format \
    linter linter-cpplint linter-eslint switch-header-source you-complete-me
```

## Autocomplete

Install C++ auto-completion engine.

```
$ git clone https://github.com/Valloric/ycmd.git ~/.ycmd
$ cd ~/.ycmd
$ ./build.py --clang-completer
```

On Mac, replace the last command above with the following.

```
$ ./build.py --clang-completer --system-libclang
```

## JavaScript lint

Install JavaScript linter for Blink web tests.

```
$ npm install -g eslint eslint-config-google
```

Configure the JavaScript linter to use the Google style by default by replacing
the contents of `~/.eslintrc` with the following.

```
{
    "extends": "google",
    "env": {
      "browser": true
    }
}
```

## Configuration

Configure Atom by replacing the contents of `~/.atom/config.cson` with the
following. Replace `<path-of-your-home-dir>` and
`<path-of-your-chrome-checkout>` with the actual full paths of your home
directory and chrome checkout. For example, these can be `/Users/bob` and
`/Users/bob/chrome/src`.

```
"*":
  # Configure ninja builder.
  "build-ninja":
    ninjaOptions: [
      # The number of jobs to use when running ninja. Adjust to taste.
      "-j10"
    ]
    subdirs: [
      # The location of your build.ninja file.
      "out/gn"
    ]
  # Do not auto-format entire files on save.
  "clang-format":
    formatCOnSave: false
    formatCPlusPlusOnSave: false
  core:
    # Treat .h files as C++.
    customFileTypes:
      "source.cpp": [
        "h"
      ]
    # Don't send metrics if you're working on anything sensitive.
    disabledPackages: [
      "metrics"
      "exception-reporting"
    ]
  # Use spaces instead of tabs.
  editor:
    tabType: "soft"
  # Show lint errors only when you save the file.
  linter:
    lintOnFly: false
  # Configure JavaScript lint.
  "linter-eslint":
    eslintrcPath: "<path-of-your-home-dir>/.eslintrc"
    useGlobalEslint: true
  # Don't show ignored files in the project file browser.
  "tree-view":
    hideIgnoredNames: true
    hideVcsIgnoredFiles: true
  # Configure C++ autocomplete and lint.
  "you-complete-me":
    globalExtraConfig: "<path-of-your-chrome-checkout>/tools/vim/monyhar.ycm_extra_conf.py"
    ycmdPath: "<path-of-your-home-dir>/.ycmd/"
# Java uses 4 space indents and 100 character lines.
".java.source":
  editor:
    preferredLineLength: 100
    tabLength: 4
```

## Symbol lookup

Atom fuzzy file finder is slow to index all files in Chrome. If you're working
on a project that frequently uses `foo` or `bar` in files names, you can create
a small `.tags` file to efficiently search the symbols within these files. Be
sure to use "Exuberant Ctags."

```
$ git ls | egrep -i "foo|bar" | ctags -f .tags -L -
```

Don't create a ctags file for the full Chrome repository, as that would result
in ~9GB tag file that will not be usable in Atom.
