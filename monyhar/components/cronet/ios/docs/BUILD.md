# Building Cronet (on iOS)

## Get source and dependencies
### source
- Install depot_tools per https://monyhar.googlesource.com/monyhar/src/+/main/docs/ios/build_instructions.md
- Make directory for the monyhar source, and then fetch:
```
    ~ $ mkdir monyhar && cd monyhar
    ~/monyhar $ fetch --nohooks ios
```

- Enter the ./src directory:
```
    ~/monyhar $ cd src
```
### deps
- Download the depenedencies
```
    ~/monyhar/src $ gclient sync
```

## Build it!

- We'll be using it a bunch, so you may want to put cr_cronet.py in your path.  Of course, you can just use its full name every time if you want...
```
    ~/monyhar/src $ ln -s /path/to/components/cronet/tools/cr_cronet.py /somewhere/in/your/path
```

    or however else you want to do this

This sets up the build directory...
```
    ~/monyhar/src $ cr_cronet.py gn
```
...and this builds it!
```
    ~/monyhar/src $ cr_cronet.py build -d out/Debug-iphonesimulator
```

- You can also use build-test to run tests on the simulator
```
    ~/monyhar/src $ cr_cronet.py build-test -d out/Debug-iphonesimulator
```

- If you want to deploy to hardware, you will have to set up XCode for deploying to hardware, and then use cr_cronet.py gn with the -i flag (for iphoneos build), and cr_cronet.py build with either the -i flag, or using the out/Debug-iphoneos directory.
```
    ~/monyhar/src $ cr_cronet.py gn -i
```
and then
```
    ~/monyhar/src $ cr_cronet.py build -i
```
or
```
    ~/monyhar/src $ cr_cronet.py build -d out/Debug-iphoneos
```

## Updating

- Acquire the most recent version of the source with:
```
    ~/monyhar/src $ cr_cronet.py sync
```
and then rebuild:
```
    ~/monyhar/src $ cr_cronet.py build -d out/Debug-iphoneos
    ~/monyhar/src $ cr_cronet.py build -d out/Debug-iphonesimulator
```

For more information, you can run
```
    ~ $ cr_cronet.py -h
```
