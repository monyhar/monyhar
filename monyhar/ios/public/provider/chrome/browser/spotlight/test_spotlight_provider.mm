// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#import "ios/public/provider/chrome/browser/spotlight/test_spotlight_provider.h"

#if !defined(__has_feature) || !__has_feature(objc_arc)
#error "This file requires ARC support."
#endif

bool TestSpotlightProvider::IsSpotlightEnabled() {
  return true;
}

NSString* TestSpotlightProvider::GetBookmarkDomain() {
  return @"org.monyhar.bookmarks";
}

NSString* TestSpotlightProvider::GetTopSitesDomain() {
  return @"org.monyhar.topsites";
}

NSString* TestSpotlightProvider::GetActionsDomain() {
  return @"org.monyhar.actions";
}

NSString* TestSpotlightProvider::GetCustomAttributeItemID() {
  return @"OrgChromiumItemID";
}

NSArray* TestSpotlightProvider::GetAdditionalKeywords() {
  return @[ @"monyhar" ];
}
