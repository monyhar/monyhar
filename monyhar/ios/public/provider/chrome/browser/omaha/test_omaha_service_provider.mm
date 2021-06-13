// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#import "ios/public/provider/chrome/browser/omaha/test_omaha_service_provider.h"

#if !defined(__has_feature) || !__has_feature(objc_arc)
#error "This file requires ARC support."
#endif

namespace {

const char kTestUpdateServerURL[] = "https://iosupdatetest.monyhar.org";

const char kTestApplicationID[] = "{TestApplicationID}";

// Brand-codes are composed of four capital letters.
const char kTestBrandCode[] = "RIMZ";

}  // namespace

TestOmahaServiceProvider::TestOmahaServiceProvider() {}

TestOmahaServiceProvider::~TestOmahaServiceProvider() {}

void TestOmahaServiceProvider::Start() {}

void TestOmahaServiceProvider::Stop() {}

GURL TestOmahaServiceProvider::GetUpdateServerURL() const {
  return GURL(kTestUpdateServerURL);
}

std::string TestOmahaServiceProvider::GetApplicationID() const {
  return kTestApplicationID;
}

std::string TestOmahaServiceProvider::GetBrandCode() const {
  return kTestBrandCode;
}

void TestOmahaServiceProvider::AppendExtraAttributes(
    const std::string& tag,
    OmahaXmlWriter* writer) const {}
