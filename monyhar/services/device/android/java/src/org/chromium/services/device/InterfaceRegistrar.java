// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.monyhar.services.device;

import org.monyhar.base.annotations.CalledByNative;
import org.monyhar.base.annotations.JNINamespace;
import org.monyhar.device.battery.BatteryMonitorFactory;
import org.monyhar.device.mojom.BatteryMonitor;
import org.monyhar.device.mojom.NfcProvider;
import org.monyhar.device.mojom.VibrationManager;
import org.monyhar.device.nfc.NfcDelegate;
import org.monyhar.device.nfc.NfcProviderImpl;
import org.monyhar.device.vibration.VibrationManagerImpl;
import org.monyhar.mojo.system.impl.CoreImpl;
import org.monyhar.services.service_manager.InterfaceRegistry;

@JNINamespace("device")
class InterfaceRegistrar {
    @CalledByNative
    static void createInterfaceRegistryForContext(
            int nativeHandle, NfcDelegate nfcDelegate) {
        // Note: The bindings code manages the lifetime of this object, so it
        // is not necessary to hold on to a reference to it explicitly.
        InterfaceRegistry registry = InterfaceRegistry.create(
                CoreImpl.getInstance().acquireNativeHandle(nativeHandle).toMessagePipeHandle());
        registry.addInterface(BatteryMonitor.MANAGER, new BatteryMonitorFactory());
        registry.addInterface(NfcProvider.MANAGER, new NfcProviderImpl.Factory(nfcDelegate));
        registry.addInterface(VibrationManager.MANAGER, new VibrationManagerImpl.Factory());
    }
}
