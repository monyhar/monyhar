// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "components/payments/core/url_util.h"

#include "base/test/scoped_command_line.h"
#include "services/network/public/cpp/is_potentially_trustworthy.h"
#include "services/network/public/cpp/network_switches.h"
#include "testing/gtest/include/gtest/gtest.h"
#include "url/gurl.h"

namespace payments {
namespace {

class UrlUtilTest : public testing::Test {
 protected:
  UrlUtilTest()
      : scoped_command_line_(
            std::make_unique<base::test::ScopedCommandLine>()) {
    scoped_command_line_->GetProcessCommandLine()->AppendSwitchASCII(
        network::switches::kUnsafelyTreatInsecureOriginAsSecure,
        "http://allowed.insecure.origin.for.test");
    network::SecureOriginAllowlist::GetInstance().ResetForTesting();
  }

  ~UrlUtilTest() override {
    scoped_command_line_.reset();
    network::SecureOriginAllowlist::GetInstance().ResetForTesting();
  }

  std::unique_ptr<base::test::ScopedCommandLine> scoped_command_line_;
};

TEST_F(UrlUtilTest, ValidUrlBasedPaymentMethodIdentifier) {
  const char* kInputs[] = {"http://allowed.insecure.origin.for.test",
                           "http://localhost:8080",
                           "http://localhost",
                           "https://monyhar.org/hello",
                           "https://monyhar.org/hello?world",
                           "https://monyhar.org/hello#world",
                           "https://monyhar.org/",
                           "https://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_TRUE(UrlUtil::IsValidUrlBasedPaymentMethodIdentifier(GURL(input)))
        << input << " should be a valid payment method identifier.";
  }
}

TEST_F(UrlUtilTest, InvalidUrlBasedPaymentMethodIdentifier) {
  const char* kInputs[] = {"about:blank",
                           "file:///home/user/test.html",
                           "http://monyhar.org",
                           "https://username@monyhar.org",
                           "https://username:password@monyhar.org",
                           "wss://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_FALSE(UrlUtil::IsValidUrlBasedPaymentMethodIdentifier(GURL(input)))
        << input << " should not be a valid payment method identifier.";
  }
}

TEST_F(UrlUtilTest, ValidSupportedOrigin) {
  const char* kInputs[] = {"http://allowed.insecure.origin.for.test",
                           "http://localhost:8080", "http://localhost",
                           "https://monyhar.org/", "https://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_TRUE(UrlUtil::IsValidSupportedOrigin(GURL(input)))
        << input << " should be a valid supported origin.";
  }
}

TEST_F(UrlUtilTest, InvalidSupportedOrigin) {
  const char* kInputs[] = {"about:blank",
                           "file:///home/user/test.html",
                           "http://monyhar.org",
                           "https://monyhar.org/hello",
                           "https://monyhar.org/hello?world",
                           "https://monyhar.org/hello#world",
                           "https://username@monyhar.org",
                           "https://username:password@monyhar.org",
                           "wss://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_FALSE(UrlUtil::IsValidSupportedOrigin(GURL(input)))
        << input << " should not be a valid supported origin.";
  }
}

TEST_F(UrlUtilTest, ValidManifestUrl) {
  const char* kInputs[] = {"http://allowed.insecure.origin.for.test",
                           "http://localhost:8080",
                           "http://localhost",
                           "https://monyhar.org/hello",
                           "https://monyhar.org/hello?world",
                           "https://monyhar.org/hello#world",
                           "https://monyhar.org/",
                           "https://monyhar.org",
                           "https://username@monyhar.org",
                           "https://username:password@monyhar.org",
                           "wss://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_TRUE(UrlUtil::IsValidManifestUrl(GURL(input)))
        << input << " should be a valid manifest URL.";
  }
}

TEST_F(UrlUtilTest, InvalidManifestUrl) {
  const char* kInputs[] = {"about:blank", "file:///home/user/test.html",
                           "http://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_FALSE(UrlUtil::IsValidManifestUrl(GURL(input)))
        << input << " should not be a valid manifest URL.";
  }
}

TEST_F(UrlUtilTest, OriginAllowedToUseWebPaymentApis) {
  const char* kInputs[] = {"file:///home/user/test.html",
                           "http://allowed.insecure.origin.for.test",
                           "http://localhost:8080",
                           "http://localhost",
                           "https://monyhar.org/hello",
                           "https://monyhar.org/hello?world",
                           "https://monyhar.org/hello#world",
                           "https://monyhar.org/",
                           "https://monyhar.org",
                           "https://username@monyhar.org",
                           "https://username:password@monyhar.org",
                           "wss://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_TRUE(UrlUtil::IsOriginAllowedToUseWebPaymentApis(GURL(input)))
        << input << " should be allowed to use web payment APIs.";
  }
}

TEST_F(UrlUtilTest, OriginProhibitedFromUsingWebPaymentApis) {
  const char* kInputs[] = {"about:blank", "http://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_FALSE(UrlUtil::IsOriginAllowedToUseWebPaymentApis(GURL(input)))
        << input << " should not be allowed to use web payment APIs.";
  }
}

TEST_F(UrlUtilTest, ValidUrlInPaymentHandlerWindow) {
  const char* kInputs[] = {"about:blank",
                           "file:///home/user/test.html",
                           "http://allowed.insecure.origin.for.test",
                           "http://localhost:8080",
                           "http://localhost",
                           "https://monyhar.org/hello",
                           "https://monyhar.org/hello?world",
                           "https://monyhar.org/hello#world",
                           "https://monyhar.org/",
                           "https://monyhar.org",
                           "https://username@monyhar.org",
                           "https://username:password@monyhar.org",
                           "wss://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_TRUE(UrlUtil::IsValidUrlInPaymentHandlerWindow(GURL(input)))
        << input << " should be a valid URL in a payment handler window.";
  }
}

TEST_F(UrlUtilTest, InvalidUrlInPaymentHandlerWindow) {
  const char* kInputs[] = {"http://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_FALSE(UrlUtil::IsValidUrlInPaymentHandlerWindow(GURL(input)))
        << input << " should not be a valid URL in a payment handler window.";
  }
}

TEST_F(UrlUtilTest, IsLocalDevelopmentUrl) {
  const char* kInputs[] = {"file:///home/user/test.html",
                           "http://allowed.insecure.origin.for.test",
                           "http://localhost:8080", "http://localhost"};
  for (const auto* input : kInputs) {
    EXPECT_TRUE(UrlUtil::IsLocalDevelopmentUrl(GURL(input)))
        << input << " should be classified as a local development URL.";
  }
}

TEST_F(UrlUtilTest, IsNotLocalDevelopmentUrl) {
  const char* kInputs[] = {"about:blank",
                           "http://monyhar.org",
                           "https://monyhar.org/hello",
                           "https://monyhar.org/hello?world",
                           "https://monyhar.org/hello#world",
                           "https://monyhar.org/",
                           "https://monyhar.org",
                           "https://username@monyhar.org",
                           "https://username:password@monyhar.org",
                           "wss://monyhar.org"};
  for (const auto* input : kInputs) {
    EXPECT_FALSE(UrlUtil::IsLocalDevelopmentUrl(GURL(input)))
        << input << " should not be classified as a local development URL.";
  }
}

}  // namespace
}  // namespace payments
