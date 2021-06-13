// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#import "chrome/browser/ui/cocoa/notifications/xpc_mach_port.h"

#include "base/check.h"
#include "base/mac/scoped_nsobject.h"

@class OS_xpc_mach_send;

extern "C" {

OS_xpc_mach_send* xpc_mach_send_create(mach_port_t);
mach_port_t xpc_mach_send_copy_right(OS_xpc_mach_send*);

}  // extern "C"

// Give the compiler declarations for the NSXPCCoder methods.
@protocol ForwardDeclareXPCCoder
- (void)encodeXPCObject:(id)object forKey:(id)key;
- (id)decodeXPCObjectForKey:(id)key;
@end

namespace {

NSString* const kCrSendRight = @"org.monyhar.xpc.MachSendRight";

}  // namespace

@implementation CrXPCMachPort {
  base::mac::ScopedMachSendRight _port;
}

- (instancetype)initWithMachSendRight:
    (base::mac::ScopedMachSendRight)sendRight {
  if ((self = [super init])) {
    DCHECK(sendRight.is_valid());
    _port = std::move(sendRight);
  }
  return self;
}

- (base::mac::ScopedMachSendRight)takeRight {
  return std::move(_port);
}

// NSCoding:
- (instancetype)initWithCoder:(NSCoder*)coder {
  DCHECK([coder isKindOfClass:NSClassFromString(@"NSXPCDecoder")]);
  if ((self = [super init])) {
    id coderAsId = coder;

    OS_xpc_mach_send* xpcObject =
        [coderAsId decodeXPCObjectForKey:kCrSendRight];
    if (!xpcObject)
      return nil;

    _port.reset(xpc_mach_send_copy_right(xpcObject));
  }
  return self;
}

- (void)encodeWithCoder:(NSCoder*)coder {
  DCHECK([coder isKindOfClass:NSClassFromString(@"NSXPCEncoder")]);
  DCHECK(_port.is_valid());

  id coderAsId = coder;

  base::scoped_nsobject<OS_xpc_mach_send> xpcObject(
      xpc_mach_send_create(_port.get()));
  [coderAsId encodeXPCObject:xpcObject forKey:kCrSendRight];
}

// NSSecureCoding:
+ (BOOL)supportsSecureCoding {
  return YES;
}

@end
