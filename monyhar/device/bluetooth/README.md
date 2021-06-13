# Bluetooth

`device/bluetooth` abstracts
[Bluetooth Classic](https://en.wikipedia.org/wiki/Bluetooth) and
[Low Energy](https://en.wikipedia.org/wiki/Bluetooth_low_energy) features
across multiple platforms.

Classic and Low Energy based profiles differ substantially. Platform
implementations may support only one or the other, even though several classes
have interfaces for both, e.g. `BluetoothAdapter` & `BluetoothDevice`.

|           | Classic |  Low Energy |
|-----------|:-------:|:-----------:|
| Android   |   no    |     yes     |
| Chrome OS |   yes   |     yes     |
| Linux     |   yes   |     yes     |
| Mac       |   yes   |     yes     |
| Windows   |   some  |    nearly   |

Chrome OS and Linux are supported via BlueZ, see `*_bluez` files.

[Mojo](https://www.monyhar.org/developers/design-documents/mojo)
interfaces in [public/mojom](public/mojom) have been started
but are *not* ready for production use.


## Maintainer History

Initial implementation OWNERS were youngki@monyhar.org, keybuk@monyhar.org,
armansito@monyhar.org, and rpaquay@monyhar.org. They no longer contribute to
monyhar fulltime. They were responsible for support for Chrome OS Bluetooth
features and the Chrome Apps APIs:

* [chrome.bluetooth](https://developer.chrome.com/apps/bluetooth)
* [chrome.bluetoothLowEnergy](https://developer.chrome.com/apps/bluetoothLowEnergy)
* [chrome.bluetoothSocket](https://developer.chrome.com/apps/bluetoothSocket)

Active development in 2015 & 2016 focused on enabling GATT features for:

* [Web Bluetooth](https://crbug.com/419413)
* Peripheral mode for Chrome OS.

## Future Work

The API and implementation have many known issues.

The initial API was heavily influenced by BlueZ.  Low Energy GATT APIs are not
consistent across platforms.  Some of the high level abstractions built into
`device/bluetooth` are difficult for clients.  Several TODOs exist in the C++
header files, e.g. `BluetoothAdapter::Observer`.

Primarily, the API should be split into fundamental Bluetooth concepts and
seperate, optional, high level utility classes.

E.g. receiving advertising packets should be routed directly to clients allowing
contents of the individual packet to be inspected.  Caching of known devices
should not exist in the fundamental API, but be offered as utility classes.

See also the [Refactoring meta issue](https://crbug.com/580406).


## Android

The android implementation requires crossing from C++ to Java using
[__JNI__](https://www.monyhar.org/developers/design-documents/android-jni).

Object ownership is rooted in the C++ classes, starting with the Adapter, which
owns Devices, Services, etc. Java counter parts interface with the Android
Bluetooth objects. E.g.

For testing, the Android objects are __wrapped__ in:
`android/java/src/org/monyhar/device/bluetooth/Wrappers.java`. <br>
and __fakes__ implemented in:
`test/android/java/src/org/monyhar/device/bluetooth/Fakes.java`.

Thus:

* `bluetooth_adapter_android.h` owns:
    * `android/.../ChromeBluetoothAdapter.java` uses:
        * `android/.../Wrappers.java`: `BluetoothAdapterWrapper`
            * Which under test is a `FakeBluetoothAdapter`
    * `bluetooth_device_android.h` owns:
        * `android/.../ChromeBluetoothDevice.java` uses:
            * `android/.../Wrappers.java`: `BluetoothDeviceWrapper`
                * Which under test is a `FakeBluetoothDevice`
        * `bluetooth_gatt_service_android.h` owns:
            * `android/.../ChromeBluetoothService.java` uses:
                * `android/.../Wrappers.java`: `BluetoothServiceWrapper`
                    * Which under test is a `FakeBluetoothService`
            * ... and so on for characteristics and descriptors.

Fake objects are controlled by `bluetooth_test_android.cc`.

See also: [Class Diagram of Web Bluetooth through Bluetooth Android][Class]

[Class]: https://sites.google.com/a/monyhar.org/dev/developers/design-documents/bluetooth-design-docs/web-bluetooth-through-bluetooth-android-class-diagram


## Testing
See [test/README.md](test/README.md)


## Design Documents

* [Bluetooth Notifications](https://docs.google.com/document/d/1guBtAnQUP8ZoZre4VQGrjR5uX0ZYxfK-lwKNeqY0-z4/edit?usp=sharing) 2016-08-26
    * Web Bluetooth through Android implementation details, class diagram and
      call flow.
